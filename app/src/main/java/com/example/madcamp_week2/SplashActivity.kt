package com.example.madcamp_week2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.user.UserApiClient

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                // User is not logged in, go to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                // User is already logged in, go to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }
    }
}