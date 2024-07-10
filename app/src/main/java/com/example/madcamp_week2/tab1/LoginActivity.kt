package com.example.madcamp_week2.tab1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp_week2.MainActivity
import com.example.madcamp_week2.databinding.ActivityLoginBinding
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 로그인 상태 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                // 로그인되지 않은 상태, 로그인 버튼 표시
                showLoginButton()
            } else {
                // 이미 로그인된 상태, MainActivity로 이동
                startMainActivity()
            }
        }
    }

    private fun showLoginButton() {
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
                startMainActivity()
            }
        }
    }

    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
            if (error != null) {
                // 로그인 오류 처리
            } else if (token != null) {
                startMainActivity()
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }
}