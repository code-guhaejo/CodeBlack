package com.guhaejo.codeblack.data.remote.model

import com.google.gson.annotations.SerializedName

data class LoginGoogleResponseModel(
    @SerializedName("access_token") var access_token: String,
)