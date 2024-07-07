package com.example.madcamp_week2

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
        val localUser = userDao.getUser(username)
        if (localUser != null) {
            convertEntityToUserData(localUser)
        } else {
            try {
                val response = userAPI.getUser(username).execute()
                if (response.isSuccessful) {
                    val userData = response.body()
                    userData?.let { saveUserLocally(it) }
                    userData
                } else null
            } catch (e: Exception) {
                null
            }
        }
    }

    suspend fun createUser(userData: UserData) = withContext(Dispatchers.IO) {
        try {
            val response = userAPI.createUser(userData).execute()
            if (response.isSuccessful) {
                saveUserLocally(userData)
                true
            } else false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateUser(userData: UserData) = withContext(Dispatchers.IO) {
        try {
            val response = userAPI.updateUser(userData.name, userData).execute()
            if (response.isSuccessful) {
                saveUserLocally(userData)
                true
            } else false
        } catch (e: Exception) {
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