package com.example.madcamp_week2

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.madcamp_week2.databinding.ActivityMainBinding
import com.example.madcamp_week2.tab1.ProfileFragment
import com.example.madcamp_week2.tab1.SessionManager
import com.example.madcamp_week2.tab1.UserData
import com.example.madcamp_week2.tab1.UserRepository
import com.example.madcamp_week2.tab1.ViewPagerAdapter
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity(), ShakeDetector.OnShakeListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager

    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)
        sessionManager = SessionManager(this)

        setupFullscreen()
        setupViewPager()
        loadUserData()

        // Initialize ShakeDetector
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        shakeDetector = ShakeDetector(this)
        sensorManager.registerListener(shakeDetector, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(shakeDetector)
    }

    override fun onShake() {
        AlertDialog.Builder(this)
            .setMessage("진짜로?")
            .setPositiveButton("예") { _, _ ->
                startActivity(Intent(this, SplashActivity2::class.java))
            }
            .setNegativeButton("아니요", null)
            .show()
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
    private suspend fun downloadImageAsByteArray(imageUrl: String): ByteArray {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(imageUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.doInput = true
                connection.connect()

                val inputStream = connection.inputStream
                val byteArrayOutputStream = ByteArrayOutputStream()
                inputStream.use { input ->
                    byteArrayOutputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                byteArrayOutputStream.toByteArray()
            } catch (e: Exception) {
                Log.e("MainActivity", "Error downloading image", e)
                ByteArray(0)
            }
        }
    }
    private fun loadUserData() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("MainActivity", "Failed to get Kakao user info", error)
            } else if (user != null) {
                Log.d("MainActivity", "Kakao user info: ${user.id}, ${user.kakaoAccount?.profile?.nickname}")

                val username = user.kakaoAccount?.profile?.nickname ?: "Unknown"
                val profileImage = user.kakaoAccount?.profile?.thumbnailImageUrl ?: "https://example.com/default-profile.jpg" // 기본 이미지 URL
                sessionManager.saveUserName(username)

                lifecycleScope.launch {
                    val userData = userRepository.getUser(username)
                    if (userData == null) {
                        // 새 사용자 생성
                        val newUser = UserData(
                            name = username,
                            profileImage = profileImage,  // 기본 이미지 URL 사용
                            description = "",  // 빈 문자열 사용
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
