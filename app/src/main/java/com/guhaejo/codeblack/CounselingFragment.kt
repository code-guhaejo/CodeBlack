package com.guhaejo.codeblack

import android.util.Log
import android.app.AlertDialog
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.guhaejo.codeblack.data.remote.logingoogle.ClientInformation.OPEN_AI_SECRET
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
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.widget.TextView

class CounselingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etMsg: EditText
    private lateinit var btnSend: AppCompatImageView
    private lateinit var textnext: TextView

    private val messageList = ArrayList<Message>()
    private lateinit var messageAdapter: MessageAdapter

    private val JSON: MediaType = "application/json; charset=utf-8".toMediaType()


    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

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

        // 뷰 요소 초기화
        recyclerView = view.findViewById(R.id.chat_recyclerview)
        etMsg = view.findViewById(R.id.chat_input)
        btnSend = view.findViewById(R.id.chat_send_btn)
        textnext=view.findViewById(R.id.textView10)
        // RecyclerView 설정
        recyclerView.setHasFixedSize(true)
        val manager = LinearLayoutManager(context)
        manager.stackFromEnd = true
        recyclerView.layoutManager = manager

        // 메시지 목록 및 어댑터 초기화
        messageAdapter = MessageAdapter(messageList)
        recyclerView.adapter = messageAdapter

        // 전송 버튼에 클릭 리스너 추가
        btnSend.setOnClickListener {
            val question = etMsg.text.toString().trim()
            addToChat(question, Message.SENT_BY_ME)
            etMsg.setText("")
            callAPI(question)
        }
    }

    // 대화 목록에 메시지 추가
    private fun addToChat(message: String, sentBy: String) {
        activity?.runOnUiThread {
            messageList.add(Message(message, sentBy))
            messageAdapter.notifyDataSetChanged()
            recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        }
    }

    // 응답 추가
    private fun addResponse(response: String) {
        messageList.removeAt(messageList.size - 1)
        addToChat(response, Message.SENT_BY_BOT)
    }

    // OpenAI API 호출
    private fun callAPI(question: String) {
        // 대기 메시지 추가
        messageList.add(Message("...", Message.SENT_BY_BOT))

        // JSON 배열 생성
        val arr = JSONArray()
        val baseAi = JSONObject()
        val userMsg = JSONObject()
        try {
            // AI 속성 설정
            baseAi.put("role", "user")
            baseAi.put("content",
                "당신은 사용자의 증상을 가볍게 진단후, 그에 맞는 응급실 매칭 시스템을 위한 챗봇입니다. 사용자에게 응답을 제공할 때 다음과 같은 JSON 형식으로 응답을 제공합니다:\n" +
                        "\n" +
                        "1. `message`: 사용자에게 제공할 문장형 응답.\n" +
                        "2. `keywords`: 데이터베이스에 저장할 리스트: 증상요약, 응급실카테고리, 필요장비"+
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
                        " MRI\t 혈관촬영기\n 가야하는 응급실카테고리를 진단내용과 함꼐 간결하게 알려줘." +
                        "만약 증상이 애매하면 그후 자세한 증상에 대해 물어보고" +
                        "그때 매칭을 시켜줘도돼.")



            // 사용자 메시지 설정
            userMsg.put("role", "user")
            userMsg.put("content", question)
            // 배열에 추가
            arr.put(baseAi)
            arr.put(userMsg)
        } catch (e: JSONException) {
            throw RuntimeException(e)
        }

        // JSON 객체 생성
        val obj = JSONObject()
        try {
            // 모델명 및 메시지 수 설정
            obj.put("model", "gpt-3.5-turbo")
            obj.put("messages", arr)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        // 요청 생성
        val body = obj.toString().toRequestBody(JSON)
        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $OPEN_AI_SECRET")
            .post(body)
            .build()

        // 비동기 호출
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // 실패시 응답 추가
                addResponse("응답을 불러오지 못했습니다: " + e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    try {
                        // JSON 파싱
                        val responseBody = response.body?.string()
                        val jsonObject = JSONObject(responseBody)
                        val jsonArray = jsonObject.getJSONArray("choices")

                        // 결과 추출
                        val result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content")

                        // JSON 응답을 파싱
                        val resultJson = JSONObject(result)
                        val message = resultJson.getString("message")
                        val keywords = resultJson.getJSONArray("keywords")

                        // 결과 처리 (로그에 출력하거나, UI 업데이트 등)
                        addResponse(message.trim())

                        Log.d("BasicSyntax", "Keywords: ${keywords.join(", ")}")
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                } else {
                    // 실패시 응답 추가
                    addResponse("응답을 불러오지 못했습니다: " + response.body?.string())
                }
            }
        })
    }

    companion object {
        fun newInstance(string1: String,string2: String): CounselingFragment {
            return CounselingFragment()
        }
    }

}