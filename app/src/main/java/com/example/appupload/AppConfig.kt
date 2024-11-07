package com.example.appupload

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppConfig {

    companion object {
        private const val BASE_URL = "https://linuxjf.com/ava/api/"

        fun getRetrofit(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
    }
}
