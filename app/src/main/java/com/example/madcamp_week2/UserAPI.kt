package com.example.madcamp_week2

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserAPI {
    @GET("users/{username}")
    fun getUser(@Path("username") username: String): Call<UserData>

    @Multipart
    @PUT("users/{username}")
    fun updateUser(
        @Path("username") username: String,
        @Part("userData") userData: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): Call<UserData>

    @POST("users")
    fun createUser(@Body userData: UserData): Call<UserData>

    @PUT("users/{username}")
    fun updateUser(@Path("username") username: String, @Body userData: UserData): Call<UserData>
}

data class UserData(
    val name: String,
    val profileImage: String?, // Base64 encoded string
    val description: String,
    val reviewed_books: List<ReviewedBook>,
    val read_books: List<String>
)

data class ReviewedBook(
    val ISBN: String,
    val star: Int,
    val review: String,
    val review_date: String
)