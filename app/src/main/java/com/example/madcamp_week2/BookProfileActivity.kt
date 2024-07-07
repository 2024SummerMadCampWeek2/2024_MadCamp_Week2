package com.example.madcamp_week2

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.madcamp_week2.databinding.ActivityBookProfileBinding
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class BookProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookProfileBinding
    private var isBookmarked = false
    private lateinit var userRepository: UserRepository
    private lateinit var bookRepository: BookRepository
    private var currentBook: Book? = null
    private var currentUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = (application as MyApplication).userRepository
        bookRepository = (application as MyApplication).bookRepository

        val book = intent.getParcelableExtra<Book>("book")
        book?.let {
            currentBook = it
            displayBookInfo(it)
            checkIfBookmarked()
        }

        binding.bookmarkButton.setOnClickListener { toggleBookmark() }
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ -> updateRating(rating) }
        binding.reviewEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateReview(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        loadUserData()
    }

    private fun displayBookInfo(book: Book) {
        Glide.with(this).load(book.image).into(binding.bookCoverImageView)
        binding.bookTitleTextView.text = book.title
        binding.bookAuthorTextView.text = book.author
        binding.bookPublisherTextView.text = book.publisher
        binding.bookPublishDateTextView.text = book.pubdate
    }

    private fun loadUserData() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                // Handle error
            } else if (user != null) {
                lifecycleScope.launch {
                    val userName = user.kakaoAccount?.profile?.nickname ?: return@launch
                    currentUser = userRepository.getUser(userName)
                    currentUser?.let {
                        checkIfBookmarked()
                        loadReviewAndRating()
                    }
                }
            }
        }
    }

    private fun checkIfBookmarked() {
        currentBook?.let { book ->
            currentUser?.let { user ->
                isBookmarked = user.readBooks.contains(book.isbn)
                updateBookmarkUI()
            }
        }
    }

    private fun loadReviewAndRating() {
        currentBook?.let { book ->
            currentUser?.let { user ->
                val reviewedBook = user.reviewedBooks.find { it.isbn == book.isbn }
                reviewedBook?.let {
                    binding.ratingBar.rating = it.rating
                    binding.reviewEditText.setText(it.review)
                }
            }
        }
    }

    private fun toggleBookmark() {
        isBookmarked = !isBookmarked
        updateBookmarkUI()
        updateUserData()
    }

    private fun updateBookmarkUI() {
        binding.bookmarkButton.setImageResource(if (isBookmarked) R.drawable.saved else R.drawable.save)
    }

    private fun updateRating(rating: Float) {
        currentUser?.let { user ->
            currentBook?.let { book ->
                val updatedReviewedBooks = user.reviewedBooks.toMutableList()
                val index = updatedReviewedBooks.indexOfFirst { it.isbn == book.isbn }
                if (index != -1) {
                    updatedReviewedBooks[index] = updatedReviewedBooks[index].copy(rating = rating)
                } else {
                    updatedReviewedBooks.add(ReviewedBook(book.isbn, "", rating, ""))
                }
                currentUser = user.copy(reviewedBooks = updatedReviewedBooks)
                updateUserData()
            }
        }
    }

    private fun updateReview(review: String) {
        currentUser?.let { user ->
            currentBook?.let { book ->
                val updatedReviewedBooks = user.reviewedBooks.toMutableList()
                val index = updatedReviewedBooks.indexOfFirst { it.isbn == book.isbn }
                if (index != -1) {
                    updatedReviewedBooks[index] = updatedReviewedBooks[index].copy(review = review)
                } else {
                    updatedReviewedBooks.add(ReviewedBook(book.isbn, review, 0f, ""))
                }
                currentUser = user.copy(reviewedBooks = updatedReviewedBooks)
                updateUserData()
            }
        }
    }

    private fun updateUserData() {
        currentUser?.let { user ->
            currentBook?.let { book ->
                val updatedReadBooks = if (isBookmarked) {
                    user.readBooks + book.isbn
                } else {
                    user.readBooks - book.isbn
                }.distinct()

                val updatedUser = user.copy(readBooks = updatedReadBooks)
                lifecycleScope.launch {
                    userRepository.updateUser(updatedUser)
                }
            }
        }
    }
}