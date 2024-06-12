package com.guhaejo.codeblack

import android.util.Log
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guhaejo.codeblack.data.remote.ClientInformation.OPEN_AI_SECRET
import com.guhaejo.codeblack.view.adapter.MessageAdapter
import com.guhaejo.codeblack.widget.utils.Message
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.guhaejo.codeblack.data.remote.loginlocal.RetrofitClient
import com.guhaejo.codeblack.data.remote.loginlocal.model.ChatRequest
import com.guhaejo.codeblack.data.remote.loginlocal.model.MessageRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class CounselingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etMsg: EditText
    private lateinit var btnSend: AppCompatImageView
    private lateinit var btnchat: Button

    private val messageList = ArrayList<Message>()
    private lateinit var messageAdapter: MessageAdapter

    private var chatId: Int = -1

    private val JSON: MediaType = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private fun startNewChat() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.chatService.addChat(ChatRequest(userId = 1))
                if (response.isSuccessful) {
                    chatId = response.body() ?: -1
                    if (chatId == -1) {
                        showError("채팅 ID를 생성할 수 없습니다.")
                    }
                } else {
                    showError("채팅 시작 오류: ${response.message()}")
                }
            } catch (e: HttpException) {
                showError("서버 통신 실패: ${e.message()}")
            } catch (e: Exception) {
                showError("오류 발생: ${e.message}")
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        Log.e("CounselingFragment", message)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_counseling, container, false)
    }

    @SuppressLint("WrongViewCast")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.chat_recyclerview)
        etMsg = view.findViewById(R.id.chat_input)
        btnSend = view.findViewById(R.id.chat_send_btn)
        btnchat = view.findViewById(R.id.chat_button)

        btnchat.visibility = View.GONE

        btnchat.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val hospitalFragment = HospitalFragment.newInstance("", "")
            transaction.replace(R.id.mainFrameLayout, hospitalFragment)
            transaction.commit()
        }

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true
        }

        messageAdapter = MessageAdapter(messageList)
        recyclerView.adapter = messageAdapter

        btnSend.setOnClickListener {
            val question = etMsg.text.toString().trim()
            if (question.isNotEmpty()) {
                addToChat(Message.Sender.USER, question)
                etMsg.setText("")
                callAPI(question)
            }
        }
        startNewChat()
    }

    private fun addToChat(sentBy: Message.Sender, message: String) {
        activity?.runOnUiThread {
            messageList.add(Message(sentBy, message))
            messageAdapter.notifyDataSetChanged()
            recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    private fun saveMessageToDatabase(chatId: Int, userId: Long, sender: Message.Sender, message: String) {
        val messageRequest = MessageRequest(chatId, userId, sender, message)
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.chatService.addMessage(messageRequest)
                if (response.isSuccessful) {
                    Log.d("CounselingFragment", "Message saved successfully")
                } else {
                    showError("메시지 저장 실패: ${response.message()}")
                }
            } catch (e: HttpException) {
                showError("서버 통신 실패: ${e.message()}")
            } catch (e: Exception) {
                showError("오류 발생: ${e.message}")
            }
        }
    }

    private fun addResponse(response: String) {
        messageList.removeAt(messageList.size - 1)
        addToChat(Message.Sender.AI, response)
    }

    private var contex = ""

    private fun callAPI(question: String) {
        messageList.add(Message(Message.Sender.AI, "..."))

        val arr = JSONArray()
        val baseAi = JSONObject()
        val userMsg = JSONObject()
        try {
            baseAi.put("role", "user")
            baseAi.put("content", "당신은 사용자의 증상을 가볍게 진단후, 그에 맞는 응급실 매칭 시스템을 위한 챗봇입니다. 사용자에게 응답을 제공할 때 다음과 같은 JSON 형식으로 응답을 제공합니다:\n" +
                    "\n" +
                    "1. `message`: 사용자에게 제공할 문장형 응답.\n" +
                    "2. `keywords`: 데이터베이스에 저장할 리스트: 증상요약, 응급실카테고리, 필요장비\n" +
                    "3. `category`: 사용자에게 매칭할 적당한 응급실카테고리, 증상이 너무 모호하거나 경증같으면 null값 반환\n" +
                    "이전 대화내용은 ${contex}이야.\n" +
                    "\n[중환자실] 일반\t [중환자실] 음압격리\t [중환자실] 소아\t [중환자실] 신생아\n" +
                    " [중환자실] 내과\t [중환자실] 심장내과\t [중환자실] 신경과\t [중환자실] 화상\n" +
                    " [중환자실] 외과\t [중환자실] 신경외과\t [중환자실] 흉부외과\t [응급전용] 입원실\n" +
                    " [응급전용] 입원실 음압격리\t [응급전용] 입원실 일반격리\t [응급전용] 중환자실\t [응급전용] 중환자실 음압격리\n" +
                    " [응급전용] 중환자실 일반격리\t [응급전용] 소아입원실\t [응급전용] 소아중환자실\t [외상전용] 중환자실\n" +
                    " [외상전용] 입원실\t [외상전용] 수술실\t [입원실] 일반\t [입원실] 음압격리\n" +
                    " [입원실] 정신과 폐쇄병동\t [기타] 수술실\t [기타] 분만실\t [기타] 화상전용처치실" +
                    "또 증상에 따라서 어떤장비가 있는곳으로 가야할지 알려줘" +
                    "\n 인공호흡기 일반\t 인공호흡기 조산아\t 인큐베이터\t CRRT\n" +
                    " ECMO\t 중심체온조절유도기\t 고압산소치료기\t CT\n" +
                    " MRI\t 혈관촬영기\n 가야하는 응급실카테고리를 진단내용과 함께 간결하게 알려줘.")

            userMsg.put("role", "user")
            userMsg.put("content", question)
            arr.put(baseAi)
            arr.put(userMsg)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }

        val obj = JSONObject()
        try {
            obj.put("model", "gpt-3.5-turbo")
            obj.put("messages", arr)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val body = obj.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $OPEN_AI_SECRET")
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                addResponse("응답을 불러오지 못했습니다: " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody)
                        val jsonArray = jsonObject.getJSONArray("choices")

                        val result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content")
                        val resultJson = JSONObject(result)
                        val message = resultJson.getString("message")
                        val keywords = resultJson.getJSONArray("keywords")
                        val category = resultJson.optString("category")

                        contex = message
                        activity?.runOnUiThread {
                            btnchat.visibility = if (category == "null") View.GONE else View.VISIBLE
                        }

                        addResponse(message.trim())

                        Log.d("BasicSyntax", "Keywords: ${keywords.join(", ")}")
                        Log.d("BasicSyntax", "category: $category")

                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    addResponse("응답을 불러오지 못했습니다: " + response.body?.string())
                }
            }
        })
    }

    companion object {
        fun newInstance(string1: String, string2: String): CounselingFragment {
            return CounselingFragment()
        }
    }
}
