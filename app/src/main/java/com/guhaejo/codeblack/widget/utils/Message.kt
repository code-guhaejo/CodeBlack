package com.guhaejo.codeblack.widget.utils

import java.time.LocalDateTime

data class Message(
    val sender: Sender,
    val message: String
) {
    enum class Sender {
        USER, AI
    }
}