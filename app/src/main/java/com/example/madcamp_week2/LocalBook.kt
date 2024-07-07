package com.example.madcamp_week2

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "local_books")
data class LocalBook(
    @PrimaryKey val isbn: String,
    val title: String,
    val image: String,
    val author: String,
    val publisher: String,
    val pubdate: String
) {
    fun toBook(): Book {
        return Book(
            title = this.title,
            image = this.image,
            author = this.author,
            publisher = this.publisher,
            pubdate = this.pubdate,
            isbn = this.isbn
        )
    }
}
