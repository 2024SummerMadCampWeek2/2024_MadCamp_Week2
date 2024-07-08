package com.example.madcamp_week2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp_week2.tab2.MainActivityTab2

class SplashActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivityTab2::class.java))
            finish()
        }, 3000) // 3 seconds delay
    }
}
