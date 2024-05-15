package com.guhaejo.codeblack.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import com.guhaejo.codeblack.BottomNavActivity
import com.guhaejo.codeblack.R
import com.guhaejo.codeblack.data.remote.LoginGoogle

class LoginGoogleActivity : AppCompatActivity() {
    private lateinit var loginGoogle: LoginGoogle
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Google 로그인 객체 초기화
        loginGoogle = LoginGoogle(this)
        Log.d(TAG, "LoginGoogle initialized.")

        // ActivityResultLauncher 초기화
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            Log.d(TAG, "ActivityResult received.")
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            } else { // 로그인 취소, 실패
                Toast.makeText(this, "로그인 취소 또는 실패", Toast.LENGTH_SHORT).show()
                Log.w(TAG, "로그인 취소 또는 실패: resultCode ${result.resultCode}")
            }
        }
        Log.d(TAG, "ActivityResultLauncher initialized.")

        // Google 로그인 버튼 설정
        val signInBtn: SignInButton = findViewById(R.id.sign_in_button)
        signInBtn.setOnClickListener {
            Log.d(TAG, "Sign-in button clicked.")
            loginGoogle.signIn(googleSignInLauncher)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            Log.d(TAG, "handleSignInResult start.")
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
            Log.w(TAG, "로그인 실패: ${e.statusCode}")
            Toast.makeText(this, "로그인 실패: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val TAG = "GoogleLoginActivity"
    }
}