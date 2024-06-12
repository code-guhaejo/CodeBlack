package com.guhaejo.codeblack.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.guhaejo.codeblack.BottomNavActivity
import com.guhaejo.codeblack.R
import com.guhaejo.codeblack.data.remote.loginlocal.RetrofitClient
import com.guhaejo.codeblack.data.remote.loginlocal.model.SignUpRequest
import com.guhaejo.codeblack.data.remote.loginlocal.model.SignUpResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val inputName = findViewById<EditText>(R.id.input_name) // 이름
        val inputEmail = findViewById<EditText>(R.id.input_email) // 아이디(이메일)
        val inputPw = findViewById<EditText>(R.id.input_pw) // 비밀번호
        val reinputPw = findViewById<EditText>(R.id.reinput_pw) // 비밀번호 재입력
        val inputPhone = findViewById<EditText>(R.id.input_phone) // 전화번호
        val signUpBtn = findViewById<Button>(R.id.sign) // 가입하기

        signUpBtn.setOnClickListener {
            val name = inputName.text.toString()
            val email = inputEmail.text.toString()
            val password = inputPw.text.toString()
            val confirmPassword = reinputPw.text.toString()
            val phone = inputPhone.text.toString()

            if (password != confirmPassword) {
                Log.d("SignUpActivity", "password: $password, confirmPassword: $confirmPassword")
                Toast.makeText(this@SignUpActivity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val signUpRequest = SignUpRequest(name, email, password, confirmPassword, phone)

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.loginLocalApi.signUpUser(signUpRequest)
                    if (response.isSuccessful) {
                        val signUpResponse: SignUpResponse? = response.body()
                        if (signUpResponse != null) {
                            Toast.makeText(this@SignUpActivity, "회원가입 성공!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@SignUpActivity, BottomNavActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@SignUpActivity, "회원가입 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@SignUpActivity, "회원가입 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: HttpException) {
                    Toast.makeText(this@SignUpActivity, "회원가입 실패: ${e.message()}", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(this@SignUpActivity, "회원가입 실패: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
