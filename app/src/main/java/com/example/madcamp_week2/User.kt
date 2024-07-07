package com.example.madcamp_week2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val name: String,
    val profileImage: String,
    val description: String,
    val reviewedBooks: List<ReviewedBook>,
    val readBooks: List<String>
) {
    fun toUserData(): UserData {
        return UserData(name, profileImage, description, reviewedBooks, readBooks)
    }

    companion object {
        fun fromUserData(userData: UserData): User {
            return User(
                userData.name,
                userData.profileImage,
                userData.description,
                userData.reviewedBooks,
                userData.readBooks
            )
        }
    }
}