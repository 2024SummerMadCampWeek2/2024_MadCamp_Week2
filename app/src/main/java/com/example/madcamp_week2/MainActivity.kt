package com.example.madcamp_week2

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.madcamp_week2.databinding.ActivityMainBinding
import com.example.madcamp_week2.ViewPagerAdapter
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)

        setupFullscreen()
        setupViewPager()
        loadUserData()
    }

    private fun setupFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        }
    }

    private fun setupViewPager() {
        viewPagerAdapter = ViewPagerAdapter(this)
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
    }

    private fun loadUserData() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                // 에러 처리
            } else if (user != null) {
                lifecycleScope.launch {
                    val userData = userRepository.getUser(user.id.toString())
                    if (userData == null) {
                        // 새 사용자 생성
                        val newUser = UserData(
                            name = user.kakaoAccount?.profile?.nickname ?: "Unknown",
                            profileImage = user.kakaoAccount?.profile?.thumbnailImageUrl,
                            description = null,
                            reviewed_books = emptyList(),
                            read_books = emptyList()
                        )
                        userRepository.createUser(newUser)
                        updateProfileFragment(newUser)
                    } else {
                        updateProfileFragment(userData)
                    }
                }
            }
        }
    }

    private fun updateProfileFragment(userData: UserData) {
        val profileFragment = supportFragmentManager.findFragmentByTag("f0") as? ProfileFragment
        profileFragment?.updateUserData(userData)
    }
}