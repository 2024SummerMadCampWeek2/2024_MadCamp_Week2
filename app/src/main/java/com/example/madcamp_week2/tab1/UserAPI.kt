package com.example.madcamp_week2.tab1

import android.os.Parcel
import android.os.Parcelable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
    ): Call<ResponseBody>

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
    val star: Double,
    val review: String,
    val review_date: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(ISBN)
        parcel.writeDouble(star)
        parcel.writeString(review)
        parcel.writeString(review_date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReviewedBook> {
        override fun createFromParcel(parcel: Parcel): ReviewedBook {
            return ReviewedBook(parcel)
        }

        override fun newArray(size: Int): Array<ReviewedBook?> {
            return arrayOfNulls(size)
        }
    }
}