package com.guhaejo.codeblack.data.remote.loginlocal.model


data class MessageResponse(
    val messageId: Int,
    val chatId: Int,
    val message: String,
    val sentBy: String,
    val timestamp: String
)
