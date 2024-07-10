package com.example.madcamp_week2.tab2

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.madcamp_week2.R

class YouTubePlayerActivity : AppCompatActivity() {

    companion object {
        const val VIDEO_ID = "VIDEO_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the activity to full-screen and landscape orientation
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        setContentView(R.layout.activity_youtube_player)

        val videoId = intent.getStringExtra(VIDEO_ID)
        val webView: WebView = findViewById(R.id.youtubeWebView)

        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true

        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://www.youtube.com/embed/$videoId?autoplay=1&vq=hd1080")
    }
}
