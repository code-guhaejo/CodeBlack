package com.guhaejo.codeblack.data.remote.logingoogle

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.guhaejo.codeblack.data.remote.ClientInformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class LoginGoogle(private val context: Context, private val lifecycleOwner: LifecycleOwner) {
    // 구글 로그인 옵션 설정
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(ClientInformation.CLIENT_ID)
        .requestServerAuthCode(ClientInformation.CLIENT_ID)
        .requestEmail()
        .build()

    private val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso) // 클라이언트 생성

    // 로그인 결과 처리
    fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
//            val authCode: String? =
//                completedTask.getResult(ApiException::class.java)?.serverAuthCode // 서버 인증 코드
//            LoginRepository().getAccessToken(authCode!!) // 액세스 토큰 요청

            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account?.idToken
            if (idToken != null) {
                lifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                    sendAccessTokenToServer(idToken)
                }
            } else {
                Log.w(TAG, "ID Token is null")
            }
        } catch (e: ApiException) {
            Log.w(TAG, "handleSignInResult: error" + e.statusCode)
        }
    }

    // 로그인
    fun signIn(launcher: ActivityResultLauncher<Intent>) {
        try {
            val signInIntent = googleSignInClient.signInIntent
            Log.d(TAG, "Launching Google sign-in intent")
            launcher.launch(signInIntent)
            Log.d(TAG, "Google sign-in intent launched")
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
        return account != null
        // return if (account == null) false else (true) // 계정이 null인지 확인, 반환
    }

    private suspend fun sendAccessTokenToServer(accessToken: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL("http://10.0.2.2:8080/token")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.readTimeout = 15000
                connection.connectTimeout = 15000
                connection.doOutput = true

                val outputStream = DataOutputStream(connection.outputStream)
                outputStream.writeBytes("access_token=$accessToken")
                outputStream.flush()
                outputStream.close()

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val response = connection.inputStream.bufferedReader().use { it.readText() }
                    Log.d(TAG, "Server response: $response")
                } else {
                    Log.e(TAG, "Server error: $responseCode")
                }
            } catch (e: Exception) {
                Log.e(TAG, "sendOnFailure: ${e.message}")
            }
        }
    }

    companion object {
        const val TAG = "GoogleLoginService"
    }
}