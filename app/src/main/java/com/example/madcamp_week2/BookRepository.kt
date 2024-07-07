package com.example.madcamp_week2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookRepository(private val bookDao: BookDao, private val naverAPI: NaverAPI) {
    suspend fun getBooks(query: String, start: Int, display: Int): List<Book> {
        return withContext(Dispatchers.IO) {
            try {
                val response = naverAPI.searchBooks(query, start, display).execute()
                if (response.isSuccessful) {
                    val books = response.body()?.items ?: emptyList()
                    // Convert Book to LocalBook and insert into local database
                    val localBooks = books.map { it.toLocalBook() }
                    bookDao.insertLocalBooks(localBooks)
                    books
                } else {
                    emptyList()
                }
            } catch (e: Exception) {
                // Handle network error
                emptyList()
            }
        }
    }

    suspend fun getBooksByISBN(isbns: List<String>): List<Book> {
        return withContext(Dispatchers.IO) {
            val localBooks = bookDao.getLocalBooks(isbns)
            localBooks.map { it.toBook() }
        }
    }

    suspend fun getLocalBooksByISBN(isbns: List<String>): List<LocalBook> {
        return withContext(Dispatchers.IO) {
            bookDao.getLocalBooks(isbns)
        }
    }

    private fun Book.toLocalBook(): LocalBook {
        return LocalBook(
            isbn = this.isbn,
            title = this.title,
            image = this.image,
            author = this.author,
            publisher = this.publisher,
            pubdate = this.pubdate
        )
    }
}