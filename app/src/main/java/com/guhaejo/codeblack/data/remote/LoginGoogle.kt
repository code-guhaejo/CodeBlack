package com.guhaejo.codeblack.data.remote

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.guhaejo.codeblack.repository.LoginRepository

class LoginGoogle(context: Context) {
    // 구글 로그인 옵션 설정
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(ClientInformation.CLIENT_ID)
        .requestServerAuthCode(ClientInformation.CLIENT_ID)
        .requestEmail()
        .build()

    private val googleSignInClient = GoogleSignIn.getClient(context, gso) // 클라이언트 생성

    // 로그인 결과 처리
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val authCode: String? =
                completedTask.getResult(ApiException::class.java)?.serverAuthCode // 서버 인증 코드
            LoginRepository().getAccessToken(authCode!!) // 액세스 토큰 요청
        } catch (e: ApiException) {
            Log.w(TAG, "handleSignInResult: error" + e.statusCode)
        }
    }

    // 로그인
    fun signIn(launcher: ActivityResultLauncher<Intent>) {
        try {
            val signInIntent = googleSignInClient.signInIntent
            if (signInIntent != null) {
                Log.d(TAG, "Launching Google sign-in intent")
                launcher.launch(signInIntent)
                Log.d(TAG, "Google sign-in intent launched")
            } else {
                Log.d(TAG, "Failed to create sign-in intent")
            }
        } catch (e: Exception) {
            Log.e(TAG, "SignIn intent creation failed with exception: ${e.message}")
        }
    }

    // 로그아웃
    fun signOut(context: Context) {
        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(context, "로그아웃 되었습니다!", Toast.LENGTH_SHORT).show()
            Log.d(TAG, "User signed out")
        }
    }

    // 로그인 상태 확인
    fun isLogin(context: Context): Boolean {
        val account = GoogleSignIn.getLastSignedInAccount(context) // 마지막 로그인 계정을 가져온다
        return if (account == null) false else (true) // 계정이 null인지 확인, 반환
    }

    companion object {
        const val TAG = "GoogleLoginService"
    }
}