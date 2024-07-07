package com.example.madcamp_week2

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

interface NaverAPI {
    @Headers(
        "X-Naver-Client-Id: jLwbpquhx5ezFWdh4ELn",
        "X-Naver-Client-Secret: A9UoONn6ns"
    )
    @GET("v1/search/book.json")
    fun searchBooks(
        @Query("query") query: String,
        @Query("start") start: Int,
        @Query("display") display: Int
    ): Call<BookSearchResponse>

    companion object {
        private const val BASE_URL = "https://openapi.naver.com/"

        fun create(): NaverAPI {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(NaverAPI::class.java)
        }
    }
}


data class BookSearchResponse(
    val lastBuildDate: String,
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<Book>
)

@Parcelize
data class Book(
    val title: String,
    val image: String,
    val author: String,
    val publisher: String,
    val pubdate: String,
    val isbn: String
) : Parcelable