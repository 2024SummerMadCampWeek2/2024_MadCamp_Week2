package com.example.madcamp_week2.tab2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.madcamp_week2.RetrofitInstance
import com.example.madcamp_week2.Trend
import com.example.madcamp_week2.VideoItem
import com.example.madcamp_week2.ViewPagerAdapter
import com.example.madcamp_week2.YouTubeResponse
import com.example.madcamp_week2.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityTab2 : AppCompatActivity() {

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private val longVideos = mutableListOf<VideoItem>()
    private val keywordList = mutableListOf<Trend>()
    private val apiKey = "AIzaSyDzvTSzWtfStXajVhq2hupfpr0kzb5Dnbo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPagerAdapter = ViewPagerAdapter(longVideos, keywordList)
        viewPager.adapter = viewPagerAdapter

        fetchTrendingData()
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
