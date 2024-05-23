package com.guhaejo.codeblack.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import com.guhaejo.codeblack.BottomNavActivity
import com.guhaejo.codeblack.R
import com.guhaejo.codeblack.data.remote.google.LoginGoogle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.Button


class LoginGoogleActivity : AppCompatActivity() {
    private lateinit var loginGoogle: LoginGoogle
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 초기화
        initGoogleSignIn()

        // Google 로그인 버튼 설정
        // 커스텀한 구글 로그인 버튼 설정
        val customGoogleSignInBtn: Button = findViewById(R.id.custom_google_sign_in_button)
        customGoogleSignInBtn.setOnClickListener {
            // 버튼 클릭 시 동작 설정
            Log.d(TAG, "Custom Google sign-in button clicked.")
            loginGoogle.signIn(googleSignInLauncher)
        }
    }


    private fun initGoogleSignIn() {
        // Google 로그인 객체 초기화
        loginGoogle = LoginGoogle(this, this)
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
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                Log.d(TAG, "handleSignInResult start.")
                val account = withContext(Dispatchers.IO) {
                    completedTask.getResult(ApiException::class.java)
                }

                if (account != null) {
                    // 로그인 성공 시 처리
                    Toast.makeText(this@LoginGoogleActivity, "로그인 성공: ${account.displayName}", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "로그인 성공: ${account.displayName}")

                    // 액세스 토큰 요청
                    loginGoogle.handleSignInResult(completedTask)

                    // 결과 전달
                    val resultIntent = Intent(this@LoginGoogleActivity, BottomNavActivity::class.java).apply {
                        putExtra("result", "success")
                        putExtra("accountName", account.displayName)
                    }
                    startActivity(resultIntent)
                    finish()
                } else {
                    Log.w(TAG, "Account is null")
                }
            } catch (e: ApiException) {
                // 로그인 실패 시 처리
                Log.w(TAG, "로그인 실패: ${e.statusCode}")
                Toast.makeText(this@LoginGoogleActivity, "로그인 실패: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val TAG = "GoogleLoginActivity"
    }
}