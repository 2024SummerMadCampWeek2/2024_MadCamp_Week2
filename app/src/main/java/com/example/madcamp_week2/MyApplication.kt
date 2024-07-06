package com.example.madcamp_week2

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

class MyApplication : Application() {
    companion object {
        lateinit var serverAPI: ServerAPI
    }

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "9e167b564736611eeda3fac0e447d065")

        val retrofit = Retrofit.Builder()
            .baseUrl("http://15.165.64.45/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        serverAPI = retrofit.create(ServerAPI::class.java)
    }
}
