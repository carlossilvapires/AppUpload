package com.example.appupload

import com.google.gson.annotations.SerializedName

data class ServerResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)
