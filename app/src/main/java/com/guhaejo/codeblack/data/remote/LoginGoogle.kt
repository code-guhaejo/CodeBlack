package com.guhaejo.codeblack.data.remote

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
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
            val authCode: String? = completedTask.getResult(ApiException::class.java)?.serverAuthCode // 서버 인증 코드
            LoginRepository().getAccessToken(authCode!!) // 액세스 토큰 요청
        } catch (e: ApiException) {
            Log.w(TAG, "handleSignInResult: error" + e.statusCode)
        }
    }

    // 로그인
    fun signIn(activity: Activity) {
        val signInIntent: Intent = googleSignInClient.signInIntent
        activity.startActivityForResult(signInIntent, 1000)
    }

    // 로그아웃
    fun signOut(context: Context) {
        googleSignInClient.signOut().addOnCompleteListener {
                Toast.makeText(context, "로그아웃 되었습니다!", Toast.LENGTH_SHORT).show()
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