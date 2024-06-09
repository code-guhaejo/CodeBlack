package com.guhaejo.codeblack.data.remote.loginlocal.model

import com.google.gson.annotations.SerializedName

data class SignInResponse(
    val success: Boolean,
    val message: String
)