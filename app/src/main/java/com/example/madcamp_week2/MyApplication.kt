package com.example.madcamp_week2

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val userRepository by lazy { UserRepository(database.userDao(), serverAPI) }
    val bookRepository by lazy { BookRepository(database.bookDao(), NaverAPI.create()) }

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