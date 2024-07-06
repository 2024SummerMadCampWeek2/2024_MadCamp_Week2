package com.example.madcamp_week2

import retrofit2.Call
import retrofit2.http.*

interface ServerAPI {
    @GET("users/{name}")
    fun getUserData(@Path("name") name: String): Call<UserData>

    @PUT("users/{name}")
    fun updateUserData(@Path("name") name: String, @Body userData: UserData): Call<UserData>
}

data class UserData(
    val name: String,
    val profileImage: String,
    val description: String,
    val reviewedBooks: List<ReviewedBook>,
    val readBooks: List<String>
)

data class ReviewedBook(
    val isbn: String,
    val review: String,
    val rating: Float,
    val reviewDate: String
)