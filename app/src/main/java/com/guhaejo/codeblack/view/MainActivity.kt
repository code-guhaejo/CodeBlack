package com.guhaejo.codeblack.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.SignInButton
import com.guhaejo.codeblack.BottomNavActivity
import com.guhaejo.codeblack.R
import com.guhaejo.codeblack.data.remote.LoginGoogle

class MainActivity : AppCompatActivity() {
    private lateinit var loginGoogle: LoginGoogle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Google 로그인 객체 초기화
        loginGoogle = LoginGoogle(this)

        // Google 로그인 버튼 설정
        findViewById<SignInButton>(R.id.sign_in_button).setOnClickListener {
            loginGoogle.signIn(this)
        }

        var loginBtn: Button = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener {
            val intent = Intent(this, BottomNavActivity::class.java)
            startActivity(intent)
        }
    }

    // 구글 로그인 결과 처리
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1000) {  // SignIn 로그인 요청 코드: 1000
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            loginGoogle.handleSignInResult(task)
        }
    }
}