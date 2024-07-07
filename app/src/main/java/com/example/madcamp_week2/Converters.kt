package com.example.madcamp_week2

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.room.Entity




class Converters {
    @TypeConverter
    fun fromReviewedBookList(value: List<ReviewedBook>): String {
        val gson = Gson()
        val type = object : TypeToken<List<ReviewedBook>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toReviewedBookList(value: String): List<ReviewedBook> {
        val gson = Gson()
        val type = object : TypeToken<List<ReviewedBook>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return value.split(",")
    }
}




