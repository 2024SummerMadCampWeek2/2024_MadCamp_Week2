package com.example.madcamp_week2.tab2

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.madcamp_week2.MainActivity
import com.example.madcamp_week2.R
import com.example.madcamp_week2.ShakeDetector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityTab2 : AppCompatActivity(), ShakeDetector.OnShakeListener {

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val longVideos = mutableListOf<VideoItem>()
    private val keywordList = mutableListOf<Trend>()
    private val apiKey = "AIzaSyDzvTSzWtfStXajkzb5Dnbo"

    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tab2)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPagerAdapter = ViewPagerAdapter(longVideos, keywordList)
        viewPager.adapter = viewPagerAdapter

        fetchTrendingData()

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
        val alertDialog = AlertDialog.Builder(this, R.style.CustomAlertDialogTheme)
            .setMessage("그래그래")
            .setPositiveButton("예") { _, _ ->
                startActivity(Intent(this, MainActivity::class.java))
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
            .setNegativeButton("아니오", null)
            .create()

        alertDialog.show()

        // Apply custom font to the message text
        val messageTextView = alertDialog.findViewById<TextView>(android.R.id.message)
        val customFont = ResourcesCompat.getFont(this, R.font.kopu)
        messageTextView?.typeface = customFont
    }

    private fun fetchTrendingData() {
        fetchTrendingVideos(apiKey)
        fetchTrendingKeywords()
    }

    private fun fetchTrendingVideos(apiKey: String) {
        val call = RetrofitInstance.youtubeApi.getTrendingVideos(
            part = "snippet,contentDetails",
            chart = "mostPopular",
            regionCode = "KR",
            maxResults = 50,
            apiKey = apiKey
        )

        call.enqueue(object : Callback<YouTubeResponse> {
            override fun onResponse(call: Call<YouTubeResponse>, response: Response<YouTubeResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        it.items.forEach { video ->
                            val duration = video.contentDetails.duration
                            // Assuming ISO 8601 duration format: PT#M#S
                            val durationPattern = "PT(\\d+H)?(\\d+M)?(\\d+S)?".toRegex()
                            val matchResult = durationPattern.matchEntire(duration)
                            matchResult?.let { match ->
                                val hours = match.groupValues[1].removeSuffix("H").toIntOrNull() ?: 0
                                val minutes = match.groupValues[2].removeSuffix("M").toIntOrNull() ?: 0
                                val seconds = match.groupValues[3].removeSuffix("S").toIntOrNull() ?: 0
                                val totalSeconds = hours * 3600 + minutes * 60 + seconds
                                if (totalSeconds > 60) {
                                    longVideos.add(video)
                                }
                            }
                        }
                        Log.d("MainActivity", "Number of long videos: ${longVideos.size}")
                        longVideos.forEach { video ->
                            Log.d("MainActivity", "Long video: ${video.snippet.title}, Duration: ${video.contentDetails.duration}")
                        }
                        viewPagerAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("MainActivity", "Response failed")
                }
            }

            override fun onFailure(call: Call<YouTubeResponse>, t: Throwable) {
                Log.e("MainActivity", "API call failed", t)
            }
        })
    }

    private fun fetchTrendingKeywords() {
        val call = RetrofitInstance.api.getTrendingKeywords()

        call.enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        keywordList.addAll(it.map { keyword -> Trend(keyword) })
                        viewPagerAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("MainActivity", "Response failed")
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                if (t is java.net.SocketTimeoutException) {
                    Log.e("MainActivity", "Timeout error", t)
                } else {
                    Log.e("MainActivity", "API call failed", t)
                }            }
        })
    }
}
