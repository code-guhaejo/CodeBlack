package com.guhaejo.codeblack.data.remote.chat.model

import com.guhaejo.codeblack.widget.utils.Message

data class MessageRequest(
    val chatId: Int,
    val userId:Long,
    val sender: Message.Sender,
    val message: String,
)
