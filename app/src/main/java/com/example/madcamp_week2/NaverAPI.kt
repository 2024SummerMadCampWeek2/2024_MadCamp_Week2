package com.example.madcamp_week2

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NaverAPI {
    @Headers(
        "X-Naver-Client-Id: jLwbpquhx5ezFWdh4ELn",
        "X-Naver-Client-Secret: A9UoONn6ns"
    )
    @GET("v1/search/book.json")
    fun searchBooks(@Query("query") query: String): Call<BookSearchResponse>

    companion object {
        private const val BASE_URL = "https://openapi.naver.com/"

        fun create(): NaverAPI {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(NaverAPI::class.java)
        }
    }
}

data class BookSearchResponse(
    val items: List<Book>
)

data class Book(
    val title: String,
    val image: String,
    val author: String,
    val isbn: String,
    val publisher: String,
    val pubdate: String
)