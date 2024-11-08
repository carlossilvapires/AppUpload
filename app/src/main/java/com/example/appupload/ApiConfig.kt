package com.example.appupload

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiConfig {

    @Multipart
    @POST("upload.php")
    fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("arquivo") name: RequestBody
    ): Call<ServerResponse>
}
