package com.guhaejo.codeblack.data.remote.loginlocal.api

import com.guhaejo.codeblack.data.remote.loginlocal.model.ChatRequest
import com.guhaejo.codeblack.data.remote.loginlocal.model.MessageRequest
import com.guhaejo.codeblack.data.remote.loginlocal.model.MessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ChatService {
    @POST("/chat/addchat")
    suspend fun addChat(@Body chatRequest: ChatRequest): Response<Int>

    @POST("/addmessage")
    suspend fun addMessage(@Body messageRequest: MessageRequest): Response<com.guhaejo.codeblack.widget.utils.Message>

}
