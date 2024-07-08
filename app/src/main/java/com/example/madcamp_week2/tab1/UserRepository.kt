package com.example.madcamp_week2.tab1

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserRepository(context: Context) {
    private val userDao = UserDatabase.getInstance(context).userDao()
    private val userAPI = Retrofit.Builder()
        .baseUrl("http://15.165.64.45/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(UserAPI::class.java)
    private val gson = Gson()

    suspend fun getUser(username: String): UserData? = withContext(Dispatchers.IO) {
        try {
            val response = userAPI.getUser(username).execute()
            if (response.isSuccessful) {
                val userData = response.body()
                Log.d("UserRepository", "User data fetched from server: $userData")
                userData?.let { saveUserLocally(it) }
                userData
            } else {
                Log.e("UserRepository", "Failed to fetch user data: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user data", e)
            null
        }
    }

    suspend fun updateUser(username: String, userData: UserData, imageByteArray: ByteArray?): Boolean = withContext(Dispatchers.IO) {
        try {
            // MultipartBody.Part로 이미지 파일 생성
            val imagePart = imageByteArray?.let { bytes ->
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
                MultipartBody.Part.createFormData("profileImage", "profile.jpg", requestFile)
            }

            // UserData를 JSON으로 변환
            val userDataJson = gson.toJson(userData)
            val userDataPart = RequestBody.create("application/json".toMediaTypeOrNull(), userDataJson)

            val response = userAPI.updateUser(username, userDataPart, imagePart).execute()
            if (response.isSuccessful) {
                Log.d("UserRepository", "User updated successfully")
                saveUserLocally(userData)
                true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UserRepository", "Failed to update user. Status Code: ${response.code()}, Error: $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user", e)
            false
        }
    }

    suspend fun createUser(userData: UserData): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("UserRepository", "Attempting to create user with data: $userData")
            val response = userAPI.createUser(userData).execute()
            if (response.isSuccessful) {
                Log.d("UserRepository", "User created successfully: $userData")
                saveUserLocally(userData)
                true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UserRepository", "Failed to create user. Status Code: ${response.code()}, Error: $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error creating user", e)
            false
        }
    }


    private fun saveUserLocally(userData: UserData) {
        val userEntity = UserEntity(
            name = userData.name,
            profileImage = userData.profileImage,
            description = userData.description,
            reviewedBooks = gson.toJson(userData.reviewed_books),
            readBooks = gson.toJson(userData.read_books)
        )
        userDao.insertUser(userEntity)
        Log.d("UserRepository", "User data saved locally: $userEntity")
    }

    suspend fun getLocalUser(username: String): UserData? = withContext(Dispatchers.IO) {
        val localUser = userDao.getUser(username)
        Log.d("UserRepository", "Local user data: $localUser")
        localUser?.let { convertEntityToUserData(it) }
    }

    private fun convertEntityToUserData(entity: UserEntity): UserData {
        return UserData(
            name = entity.name,
            profileImage = entity.profileImage,
            description = entity.description,
            reviewed_books = gson.fromJson(entity.reviewedBooks, Array<ReviewedBook>::class.java).toList(),
            read_books = gson.fromJson(entity.readBooks, Array<String>::class.java).toList()
        )
    }
}