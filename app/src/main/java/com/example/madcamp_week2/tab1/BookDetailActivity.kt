package com.example.madcamp_week2.tab1

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.madcamp_week2.R
import com.example.madcamp_week2.databinding.ActivityBookDetailBinding
import kotlinx.coroutines.launch
import java.time.LocalDate

class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager
    private var currentBook: Book? = null
    private var isBookSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)
        sessionManager = SessionManager(this)

        currentBook = intent.getParcelableExtra("book")
        currentBook?.let { displayBookDetails(it) }

        setupRatingAndReview()
        setupSaveButton()
        loadExistingReview()
    }

    private fun displayBookDetails(book: Book) {
        binding.bookTitleTextView.text = book.title
        binding.bookAuthorTextView.text = book.author
        binding.bookPublisherTextView.text = book.publisher
        binding.bookPubdateTextView.text = book.pubdate
        Glide.with(this).load(book.image).into(binding.bookCoverImageView)
    }

    private fun setupRatingAndReview() {
        binding.submitReviewButton.setOnClickListener {
            val rating = binding.ratingBar.rating
            val review = binding.reviewEditText.text.toString()
            currentBook?.let { book ->
                lifecycleScope.launch {
                    val username = sessionManager.getUserName()
                    username?.let { name ->
                        val userData = userRepository.getLocalUser(name)
                        userData?.let { user ->
                            val updatedReviewedBooks = user.reviewed_books.toMutableList()
                            val existingReviewIndex = updatedReviewedBooks.indexOfFirst { it?.ISBN == book.isbn }
                            val newReview = ReviewedBook(
                                ISBN = book.isbn,
                                star = rating.toInt(),
                                review = review,
                                review_date = LocalDate.now().toString()
                            )
                            if (existingReviewIndex != -1) {
                                updatedReviewedBooks[existingReviewIndex] = newReview
                            } else {
                                updatedReviewedBooks.add(newReview)
                            }
                            val updatedUserData = user.copy(reviewed_books = updatedReviewedBooks)
                            val updated = userRepository.updateUser(name, updatedUserData, null)
                            if (updated) {
                                Log.d("BookDetailActivity", "Review added/updated successfully")
                                userRepository.updateLocalUser(updatedUserData)
                            } else {
                                Log.e("BookDetailActivity", "Failed to add/update review")
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            lifecycleScope.launch {
                val username = sessionManager.getUserName()
                username?.let { name ->
                    val userData = userRepository.getLocalUser(name)
                    userData?.let { user ->
                        val updatedReadBooks = user.read_books.toMutableList()
                        currentBook?.let { book ->
                            if (isBookSaved) {
                                updatedReadBooks.remove(book.isbn)
                            } else {
                                updatedReadBooks.add(book.isbn)
                            }
                        }
                        val updatedUserData = user.copy(read_books = updatedReadBooks)
                        val updated = userRepository.updateUser(name, updatedUserData, null)
                        if (updated) {
                            isBookSaved = !isBookSaved
                            updateSaveButtonUI()
                            userRepository.updateLocalUser(updatedUserData)
                            Log.d("BookDetailActivity", "Book saved status updated: $isBookSaved")
                        } else {
                            Log.e("BookDetailActivity", "Failed to update book saved status")
                        }
                    }
                }
            }
        }
        updateSaveButtonUI()
    }

    private fun updateSaveButtonUI() {
        binding.saveButton.setImageResource(
            if (isBookSaved) R.drawable.saved else R.drawable.save
        )
    }

    private fun loadExistingReview() {
        lifecycleScope.launch {
            val username = sessionManager.getUserName()
            username?.let { name ->
                val userData = userRepository.getLocalUser(name)
                userData?.let { user ->
                    isBookSaved = user.read_books.contains(currentBook?.isbn)
                    updateSaveButtonUI()

                    currentBook?.let { book ->
                        val existingReview = user.reviewed_books.find { it?.ISBN == book.isbn }
                        existingReview?.let {
                            binding.ratingBar.rating = it.star?.toFloat() ?: 0f
                            binding.reviewEditText.setText(it.review ?: "")
                        }
                    }
                }
            }
        }
    }
}