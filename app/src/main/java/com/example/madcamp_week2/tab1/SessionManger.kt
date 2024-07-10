package com.example.madcamp_week2.tab1

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)

    fun saveUserName(userName: String) {
        sharedPreferences.edit().putString("USER_NAME", userName).apply()
    }

    fun getUserName(): String? {
        return sharedPreferences.getString("USER_NAME", null)
    }
}