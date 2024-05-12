package com.guhaejo.codeblack.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import com.guhaejo.codeblack.BottomNavActivity
import com.guhaejo.codeblack.R
import com.guhaejo.codeblack.data.remote.LoginGoogle

class LoginGoogleActivity : AppCompatActivity() {
    private lateinit var loginGoogle: LoginGoogle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Google 로그인 객체 초기화
        loginGoogle = LoginGoogle(this)

        // Google 로그인 버튼 설정
        val signInBtn: Button = findViewById(R.id.sign_in_button)
        signInBtn.setOnClickListener {
            loginGoogle.signIn(this)
            // onActivityResult 실행
            // requestCode(1000), resultCode(RESULT_OK/RESULT_CANCELED), Intent 전달
        }
    }

    // 구글 로그인 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000) {  // SignIn 로그인 요청 코드: 1000
            if (resultCode == RESULT_OK) {  // 로그인 성공
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                handleSignInResult(task)
            } else { // 로그인 취소, 실패
                Toast.makeText(this, "로그인 취소 또는 실패", Toast.LENGTH_SHORT).show()
                Log.w("GoogleLoginActivity", "로그인 취소 또는 실패: resultCode $resultCode")
            }
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)

            // 로그인 성공 시 처리
            Toast.makeText(this, "로그인 성공: ${account?.displayName}", Toast.LENGTH_SHORT).show()
            Log.d("GoogleLoginActivity", "로그인 성공: ${account?.displayName}")

            // 액세스 토큰 요청
            loginGoogle.handleSignInResult(completedTask)

            // 결과 전달
            val resultIntent = Intent(this, BottomNavActivity::class.java).apply {
                putExtra("result", "success")
                putExtra("accountName", account?.displayName)
            }
            startActivity(resultIntent)
            finish()

        } catch (e: ApiException) {
            // 로그인 실패 시 처리
            Log.w("GoogleLoginActivity", "로그인 실패: ${e.statusCode}")
            Toast.makeText(this, "로그인 실패: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }
}