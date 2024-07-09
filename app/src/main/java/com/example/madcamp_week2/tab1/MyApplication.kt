package com.example.madcamp_week2.tab1

import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk
import com.kakao.sdk.common.util.Utility

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Kakao SDK
        KakaoSdk.init(this, "9e167b564736611eeda3fac0e447d065")

        // Get and log the key hash
        val keyHash = Utility.getKeyHash(this)
        Log.d("MyApplication", "KeyHash: $keyHash")
    }
}
