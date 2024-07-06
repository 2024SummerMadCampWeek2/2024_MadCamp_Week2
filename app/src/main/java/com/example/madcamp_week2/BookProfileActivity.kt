package com.example.madcamp_week2

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.madcamp_week2.databinding.ActivityBookProfileBinding
import com.kakao.sdk.user.UserApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import android.text.Editable
import android.text.TextWatcher

class BookProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookProfileBinding
    private var isBookmarked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val book = intent.getParcelableExtra<Book>("book")
        book?.let { displayBookInfo(it) }

        binding.bookmarkButton.setOnClickListener { toggleBookmark(book) }
        binding.ratingBar.setOnRatingBarChangeListener { _, rating, _ -> updateRating(book, rating) }
        binding.reviewEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateReview(book, s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun displayBookInfo(book: Book) {
        Glide.with(this).load(book.image).into(binding.bookCoverImageView)
        binding.bookTitleTextView.text = book.title
        binding.bookAuthorTextView.text = book.author
        binding.bookPublisherTextView.text = book.publisher
        binding.bookPublishDateTextView.text = book.pubdate
    }

    private fun toggleBookmark(book: Book?) {
        isBookmarked = !isBookmarked
        binding.bookmarkButton.setImageResource(if (isBookmarked) R.drawable.saved else R.drawable.save)
        book?.let { updateToReadList(it) }
    }

    private fun updateToReadList(book: Book) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("BookProfileActivity", "Failed to get user info", error)
            } else if (user != null) {
                val userName = user.kakaoAccount?.profile?.nickname ?: return@me
                MyApplication.serverAPI.getUserData(userName).enqueue(object : Callback<UserData> {
                    override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                        if (response.isSuccessful) {
                            val userData = response.body()
                            userData?.let {
                                val updatedReadBooks = if (isBookmarked) {
                                    it.readBooks + book.isbn
                                } else {
                                    it.readBooks - book.isbn
                                }
                                val updatedUserData = it.copy(readBooks = updatedReadBooks)
                                updateUserData(userName, updatedUserData)
                            }
                        } else {
                            Log.e("BookProfileActivity", "Failed to get user data: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<UserData>, t: Throwable) {
                        Log.e("BookProfileActivity", "Network error", t)
                    }
                })
            }
        }
    }

    private fun updateRating(book: Book?, rating: Float) {
        book?.let { updateReviewedBooks(it, rating = rating) }
    }

    private fun updateReview(book: Book?, review: String) {
        book?.let { updateReviewedBooks(it, review = review) }
    }

    private fun updateReviewedBooks(book: Book, rating: Float? = null, review: String? = null) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("BookProfileActivity", "Failed to get user info", error)
            } else if (user != null) {
                val userName = user.kakaoAccount?.profile?.nickname ?: return@me
                MyApplication.serverAPI.getUserData(userName).enqueue(object : Callback<UserData> {
                    override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                        if (response.isSuccessful) {
                            val userData = response.body()
                            userData?.let {
                                val existingReview = it.reviewedBooks.find { it.isbn == book.isbn }
                                val updatedReview = existingReview?.copy(
                                    rating = rating ?: existingReview.rating,
                                    review = review ?: existingReview.review,
                                    reviewDate = LocalDate.now().toString()
                                ) ?: ReviewedBook(book.isbn, review ?: "", rating ?: 0f, LocalDate.now().toString())

                                val updatedReviewedBooks = it.reviewedBooks.filter { it.isbn != book.isbn } + updatedReview
                                val updatedUserData = it.copy(reviewedBooks = updatedReviewedBooks)
                                updateUserData(userName, updatedUserData)
                            }
                        } else {
                            Log.e("BookProfileActivity", "Failed to get user data: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<UserData>, t: Throwable) {
                        Log.e("BookProfileActivity", "Network error", t)
                    }
                })
            }
        }
    }

    private fun updateUserData(userName: String, userData: UserData) {
        MyApplication.serverAPI.updateUserData(userName, userData).enqueue(object : Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (!response.isSuccessful) {
                    Log.e("BookProfileActivity", "Failed to update user data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Log.e("BookProfileActivity", "Network error", t)
            }
        })
    }
}