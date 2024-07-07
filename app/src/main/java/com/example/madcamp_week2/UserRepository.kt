package com.example.madcamp_week2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao, private val serverAPI: ServerAPI) {
    suspend fun getUser(name: String): User? {
        return withContext(Dispatchers.IO) {
            var user = userDao.getUser(name)
            if (user == null) {
                try {
                    val response = serverAPI.getUserData(name).execute()
                    if (response.isSuccessful) {
                        val userData = response.body()
                        user = userData?.let { User.fromUserData(it) }
                        user?.let { userDao.insertUser(it) }
                    }
                } catch (e: Exception) {
                    // Handle network error
                }
            }
            user
        }
    }

    suspend fun updateUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.updateUser(user)
            try {
                serverAPI.updateUserData(user.name, user.toUserData()).execute()
            } catch (e: Exception) {
                // Handle network error
            }
        }
    }
}