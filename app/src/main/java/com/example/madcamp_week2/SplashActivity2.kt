package com.example.madcamp_week2

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.madcamp_week2.tab2.MainActivityTab2

class SplashActivity2 : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash2)
        setTheme(R.style.SplashTheme)

        val gifImageView: ImageView = findViewById(R.id.gifImageView)
        val image1: ImageView = findViewById(R.id.image1)
        val image2: ImageView = findViewById(R.id.image2)
        val image3: ImageView = findViewById(R.id.image3)
        val fireImage: ImageView = findViewById(R.id.fireImage)

        Glide.with(this)
            .asGif()
            .load(R.drawable.giff)
            .into(gifImageView)

        // 페이드 인 애니메이션 설정
        fun createFadeInAnimation(): Animation {
            return AlphaAnimation(0.0f, 1.0f).apply {
                duration = 700 // 0.7초
                fillAfter = true
            }
        }

        // 이미지 1, 2, 3 순차적으로 페이드 인 (1초 지연 추가)
        Handler(Looper.getMainLooper()).postDelayed({
            image1.visibility = View.VISIBLE
            image1.startAnimation(createFadeInAnimation())
        }, 1000)

        Handler(Looper.getMainLooper()).postDelayed({
            image2.visibility = View.VISIBLE
            image2.startAnimation(createFadeInAnimation())
        }, 1700)

        Handler(Looper.getMainLooper()).postDelayed({
            image3.visibility = View.VISIBLE
            image3.startAnimation(createFadeInAnimation())
        }, 2400)

        // fire.png 회전 및 이동 애니메이션 설정
        val rotate = RotateAnimation(0f, 720f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
            duration = 2000
            interpolator = LinearInterpolator()
        }

        val translate = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.6f,
            Animation.RELATIVE_TO_PARENT, -0.3f,
            Animation.RELATIVE_TO_PARENT, -1.5f,
            Animation.RELATIVE_TO_PARENT, 0.25f
        ).apply {
            duration = 2000
            interpolator = DecelerateInterpolator()
        }

        val animSet = AnimationSet(true).apply {
            addAnimation(rotate)
            addAnimation(translate)
            fillAfter = true
        }

        Handler(Looper.getMainLooper()).postDelayed({
            fireImage.visibility = View.VISIBLE
            fireImage.startAnimation(animSet)
        }, 3000) // 1초 지연 추가

        // 음악 재생
        mediaPlayer = MediaPlayer.create(this, R.raw.dd)
        mediaPlayer.start()

        // 스플래시 화면 종료
        Handler(Looper.getMainLooper()).postDelayed({
            mediaPlayer.release() // 음악 재생 종료 및 MediaPlayer 자원 해제
            startActivity(Intent(this, MainActivityTab2::class.java))
            finish()
            overridePendingTransition(0, 0)
        }, 6000) // 1초 지연 추가
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
    }
}