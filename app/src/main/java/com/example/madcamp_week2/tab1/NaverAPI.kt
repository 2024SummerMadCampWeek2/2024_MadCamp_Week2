package com.example.madcamp_week2.tab1

import android.os.Parcelable
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import kotlinx.coroutines.delay

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
        private const val RATE_LIMIT_DELAY_MS = 6000 // 6초 지연을 통해 분당 10개 요청 제한 준수

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

        suspend fun getBookImageByISBN(isbn: String): String? {
            return withContext(Dispatchers.IO) {
                try {
                    // 요청 전 지연을 추가하여 속도 제한 초과를 방지
                    delay(67)
                    val response = create().searchBooks("$isbn", 1, 1).execute()
                    if (response.isSuccessful) {
                        response.body()?.items?.firstOrNull()?.image
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    Log.e("NaverAPI", "Error searching book image by ISBN", e)
                    null
                }
            }
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
    val isbn: String,
    var rating: Float = 0f
) : Parcelable
