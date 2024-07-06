package com.example.madcamp_week2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.user.UserApiClient
import com.example.madcamp_week2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val keyHash = Utility.getKeyHash(this)
        if (keyHash != null) {
            Log.d(TAG, "keyhash : $keyHash")
        } else {
            Log.e(TAG, "Failed to get keyhash, keyHash is null")
        }

        // Kakao Login Button Click Listener
        binding.kakaoLoginButton.setOnClickListener {
            loginWithKakao()
        }
    }

    private fun loginWithKakao() {
        // First try to login with KakaoTalk
        UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            if (error != null) {
                Log.e(TAG, "KakaoTalk login failed", error)
                // If KakaoTalk login fails, fallback to login with Kakao Account
                loginWithKakaoAccount()
            } else if (token != null) {
                Log.i(TAG, "KakaoTalk login succeeded. Token: ${token.accessToken}")
                getUserProfile()
            }
        }
    }

    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            if (error != null) {
                Log.e(TAG, "KakaoAccount login failed", error)
            } else if (token != null) {
                Log.i(TAG, "KakaoAccount login succeeded. Token: ${token.accessToken}")
                getUserProfile()
            }
        }
    }

    private fun getUserProfile() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "Failed to get user profile", error)
            } else if (user != null) {
                Log.i(TAG, "User profile: ${user.kakaoAccount?.profile?.nickname}, ${user.kakaoAccount?.profile?.thumbnailImageUrl}")

                // Display user profile
                binding.userNameTextView.text = user.kakaoAccount?.profile?.nickname
                Glide.with(this)
                    .load(user.kakaoAccount?.profile?.thumbnailImageUrl)
                    .into(binding.userProfileImageView)
            }
        }
    }
}
