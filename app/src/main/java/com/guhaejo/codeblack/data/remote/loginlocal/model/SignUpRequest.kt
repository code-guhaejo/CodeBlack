package com.guhaejo.codeblack.data.remote.loginlocal.model

data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String,
    val confirmPassword: String,
    val phone: String
)