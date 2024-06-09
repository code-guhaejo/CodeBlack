package com.guhaejo.codeblack.data.remote.loginlocal.api

import com.guhaejo.codeblack.data.remote.loginlocal.model.SignInRequest
import com.guhaejo.codeblack.data.remote.loginlocal.model.SignInResponse
import com.guhaejo.codeblack.data.remote.loginlocal.model.SignUpRequest
import com.guhaejo.codeblack.data.remote.loginlocal.model.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LoginLocalService {
    @Headers("Content-Type: application/json")
    @POST("signin")
    suspend fun signInUser(@Body signInRequest: SignInRequest): Response<SignInResponse>

    @Headers("Content-Type: application/json")
    @POST("signup")
    suspend fun signUpUser(@Body signUpRequest: SignUpRequest): Response<SignUpResponse>
}