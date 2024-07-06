package com.example.madcamp_week2

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Kakao SDK
        KakaoSdk.init(this, "9e167b564736611eeda3fac0e447d065")
    }
}
