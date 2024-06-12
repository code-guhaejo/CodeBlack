package com.guhaejo.codeblack.data.remote.loginlocal

import com.guhaejo.codeblack.data.remote.loginlocal.api.ChatService
import com.guhaejo.codeblack.data.remote.loginlocal.api.LoginLocalService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080"

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient) // 로그캣에서 패킷 내용을 모니터링 할 수 있음 (인터셉터)
            .build()
    }

    val loginLocalService: LoginLocalService by lazy {
        retrofit.create(LoginLocalService::class.java)
    }
    val chatService: ChatService by lazy {
        retrofit.create(ChatService::class.java)
    }
}