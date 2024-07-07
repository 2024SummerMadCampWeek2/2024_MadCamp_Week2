package com.example.madcamp_week2

import androidx.room.*

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE name = :name")
    suspend fun getUser(name: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Update
    suspend fun updateUser(user: User)
}