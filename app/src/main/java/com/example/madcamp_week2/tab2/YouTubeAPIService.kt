package com.example.madcamp_week2

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface YouTubeApiService {
    @GET("videos")
    fun getTrendingVideos(
        @Query("part") part: String,
        @Query("chart") chart: String,
        @Query("regionCode") regionCode: String,
        @Query("maxResults") maxResults: Int,
        @Query("key") apiKey: String
    ): Call<YouTubeResponse>
}

interface GoogleTrendsApiService {
    @GET("trends")
    fun getTrendingKeywords(
        @Query("geo") geo: String,
        @Query("hl") hl: String
    ): Call<GoogleTrendsResponse>
}

interface BackendApiService {
    @GET("trending_keywords")
    fun getTrendingKeywords(): Call<List<String>>
}

object RetrofitInstance {
    private const val YOUTUBE_BASE_URL = "https://www.googleapis.com/youtube/v3/"
    private const val GOOGLE_TRENDS_BASE_URL = "https://trends.google.com/"
    private const val BASE_URL = "http://15.165.64.45/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // Set your desired connect timeout
        .readTimeout(30, TimeUnit.SECONDS) // Set your desired read timeout
        .writeTimeout(30, TimeUnit.SECONDS) // Set your desired write timeout
        .build()

    val youtubeApi: YouTubeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(YOUTUBE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YouTubeApiService::class.java)
    }

    val googleTrendsApi: GoogleTrendsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(GOOGLE_TRENDS_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GoogleTrendsApiService::class.java)
    }

    val api: BackendApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApiService::class.java)
    }

}

data class VideoItem(
    val id: String,
    val snippet: Snippet,
    val contentDetails: ContentDetails
)

data class Snippet(
    val title: String,
    val description: String,
    val thumbnails: Thumbnails
)

data class Thumbnails(
    val default: Thumbnail,
    val high: Thumbnail
)

data class Thumbnail(
    val url: String
)

data class ContentDetails(
    val duration: String
)

data class YouTubeResponse(
    val items: List<VideoItem>
)

data class Trend(
    val keyword: String
)

data class GoogleTrendsResponse(
    val trends: List<Trend>
)
