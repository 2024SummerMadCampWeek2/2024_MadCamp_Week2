package com.example.madcamp_week2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.madcamp_week2.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = (application as MyApplication).userRepository

        binding.kakaoLoginButton.setOnClickListener {
            loginWithKakao()
        }
    }

    private fun loginWithKakao() {
        UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
            if (error != null) {
                if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                    // 사용자가 카카오톡 로그인 취소
                } else if (error is ClientError && error.reason == ClientErrorCause.Unknown) {
                    // 카카오톡이 설치되지 않은 경우
                    loginWithKakaoAccount()
                } else {
                    // 기타 오류 처리
                }
            } else if (token != null) {
                getUserInfo()
            }
        }
    }

    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            if (error != null) {
                // 로그인 오류 처리
            } else if (token != null) {
                getUserInfo()
            }
        }
    }

    private fun getUserInfo() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                // 사용자 정보 요청 실패
            } else if (user != null) {
                lifecycleScope.launch {
                    val userName = user.kakaoAccount?.profile?.nickname ?: return@launch
                    var userData = userRepository.getUser(userName)
                    if (userData == null) {
                        // 새 사용자 등록
                        userData = User(
                            name = userName,
                            profileImage = user.kakaoAccount?.profile?.thumbnailImageUrl ?: "",
                            description = "",
                            reviewedBooks = emptyList(),
                            readBooks = emptyList()
                        )
                        userRepository.updateUser(userData)
                    }
                    startMainActivity()
                }
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}