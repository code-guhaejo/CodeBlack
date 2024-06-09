package com.guhaejo.codeblack.data.remote.loginlocal.model

import com.google.gson.annotations.SerializedName

data class SignInResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String
)