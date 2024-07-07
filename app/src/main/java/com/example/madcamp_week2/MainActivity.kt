package com.example.madcamp_week2

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.madcamp_week2.databinding.ActivityMainBinding
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)
        sessionManager = SessionManager(this)

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
                Log.e("MainActivity", "Failed to get Kakao user info", error)
            } else if (user != null) {
                Log.d("MainActivity", "Kakao user info: ${user.id}, ${user.kakaoAccount?.profile?.nickname}")

                val username = user.kakaoAccount?.profile?.nickname ?: "Unknown"
                sessionManager.saveUserName(username)

                lifecycleScope.launch {
                    val userData = userRepository.getUser(username)
                    if (userData == null) {
                        val newUser = UserData(
                            name = username,
                            profileImage = user.kakaoAccount?.profile?.thumbnailImageUrl,
                            description = null,
                            reviewed_books = emptyList(),
                            read_books = emptyList()
                        )
                        val created = userRepository.createUser(newUser)
                        if (created) {
                            Log.d("MainActivity", "New user created: $newUser")
                            updateProfileFragment(newUser)
                        } else {
                            Log.e("MainActivity", "Failed to create new user")
                        }
                    } else {
                        Log.d("MainActivity", "Existing user data: $userData")
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