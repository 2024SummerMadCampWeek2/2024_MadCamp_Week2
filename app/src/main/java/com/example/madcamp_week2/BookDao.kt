package com.example.madcamp_week2

import androidx.room.*

@Dao
interface BookDao {
    @Query("SELECT * FROM local_books WHERE isbn IN (:isbns)")
    suspend fun getLocalBooks(isbns: List<String>): List<LocalBook>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalBooks(books: List<LocalBook>)

    @Query("SELECT * FROM local_books WHERE isbn = :isbn")
    suspend fun getLocalBook(isbn: String): LocalBook?
}