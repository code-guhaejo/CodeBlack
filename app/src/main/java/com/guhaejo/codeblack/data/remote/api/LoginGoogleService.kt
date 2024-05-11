package com.guhaejo.codeblack.data.remote.api

import com.google.gson.GsonBuilder
import com.guhaejo.codeblack.data.remote.model.LoginGoogleRequestModel
import com.guhaejo.codeblack.data.remote.model.LoginGoogleResponseModel
import com.guhaejo.codeblack.data.remote.model.SendAccessTokenModel
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginGoogleService {
    // 구글 OAuth 2.0 토큰 요청
    @POST("oauth2/v4/token")
    fun getAccessToken(
        @Body request: LoginGoogleRequestModel
    ): Call<LoginGoogleResponseModel>

    // 액세스 토큰을 서버로 전송
    @POST("login")
    @Headers("content-type: application/json")
    fun sendAccessToken(
        @Body accessToken: SendAccessTokenModel
    ):Call<String>

    companion object {
        private val gson = GsonBuilder().setLenient().create()

        // Retrofit 인스턴스 생성 후 LoginGoogleService 반환
        fun loginRetrofit(baseUrl: String): LoginGoogleService {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(LoginGoogleService::class.java)
        }
    }
}