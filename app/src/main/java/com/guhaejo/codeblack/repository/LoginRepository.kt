package com.guhaejo.codeblack.repository

import android.util.Log
import com.guhaejo.codeblack.data.remote.ClientInformation
import com.guhaejo.codeblack.data.remote.api.LoginGoogleService
import com.guhaejo.codeblack.data.remote.model.LoginGoogleRequestModel
import com.guhaejo.codeblack.data.remote.model.LoginGoogleResponseModel
import com.guhaejo.codeblack.data.remote.model.SendAccessTokenModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepository {

    // Google API 서버 URL
    private val getAccessTokenBaseUrl = "https://www.googleapis.com/"
    // 로컬 서버 URL
    private val sendAccessTokenBaseUrl = "http://10.0.2.2:8080/"

    // 구글 OAuth 서버로부터 액세스 토큰 요청
    fun getAccessToken(authCode:String) {
        LoginGoogleService.loginRetrofit(getAccessTokenBaseUrl).getAccessToken(
            request = LoginGoogleRequestModel(
                grant_type = "authorization_code",
                client_id = ClientInformation.CLIENT_ID,
                client_secret = ClientInformation.CLIENT_SECRET,
                code = authCode.orEmpty()
            )
        ).enqueue(object : Callback<LoginGoogleResponseModel> {
            override fun onResponse(call: Call<LoginGoogleResponseModel>, response: Response<LoginGoogleResponseModel>) {
                if(response.isSuccessful) {
                    val accessToken = response.body()?.access_token.orEmpty()
                    Log.d(TAG, "accessToken: $accessToken")
                    sendAccessToken(accessToken)
                } else {
                    Log.e(TAG, "getAccessToken onResponse failed: ${response.errorBody()}")
                }
            }
            override fun onFailure(call: Call<LoginGoogleResponseModel>, t: Throwable) {
                Log.e(TAG, "getOnFailure: ",t.fillInStackTrace() )
            }
        })
    }

    //  액세스 토큰을 로컬 서버로 전송
    fun sendAccessToken(accessToken:String){
        Log.d(TAG, "Sending access token to server: $accessToken")
        LoginGoogleService.loginRetrofit(sendAccessTokenBaseUrl).sendAccessToken(
            accessToken = SendAccessTokenModel(accessToken)
        ).enqueue(object :Callback<String>{
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful){
                    Log.d(TAG, "sendOnResponse: ${response.body()}")
                } else {
                    Log.e(TAG, "sendOnResponse failed: ${response.errorBody()}")
                }
            }
            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "sendOnFailure: ${t.fillInStackTrace()}")
            }
        })
    }

    companion object {
        const val TAG = "LoginRepository"
    }
}
