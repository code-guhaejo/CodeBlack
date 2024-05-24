package com.guhaejo.codeblack.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.common.SignInButton
import com.guhaejo.codeblack.BottomNavActivity
import com.guhaejo.codeblack.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginBtn: Button = findViewById(R.id.login_btn)
        loginBtn.setOnClickListener {
            val intent = Intent(this, BottomNavActivity::class.java)
            startActivity(intent)
        }

        // Google 로그인 버튼 설정
        val customGoogleSignInBtn: Button = findViewById(R.id.custom_google_sign_in_button)
        customGoogleSignInBtn.setOnClickListener {
            val googleLoginIntent = Intent(this, LoginGoogleActivity::class.java)
            startActivity(googleLoginIntent)
        }


    }
}