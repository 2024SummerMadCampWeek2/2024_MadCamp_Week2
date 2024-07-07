package com.example.madcamp_week2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.madcamp_week2.databinding.ActivityBookDetailBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)

        val book = intent.getParcelableExtra<Book>("book")
        book?.let { displayBookDetails(it) }

        setupRatingAndReview()
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
            val book = intent.getParcelableExtra<Book>("book")

            book?.let {
                GlobalScope.launch {
                    val userData = userRepository.getUser(getCurrentUsername())
                    userData?.let { user ->
                        val updatedReviewedBooks = user.reviewed_books.toMutableList()
                        updatedReviewedBooks.add(ReviewedBook(
                            ISBN = it.isbn,
                            star = rating.toInt(),
                            review = review,
                            review_date = java.time.LocalDate.now().toString()
                        ))
                        val updatedUserData = user.copy(reviewed_books = updatedReviewedBooks)
                        userRepository.updateUser(updatedUserData)
                    }
                }
            }
        }
    }

    private fun getCurrentUsername(): String {
        // 현재 로그인한 사용자의 이름을 반환하는 로직 구현
        // 예: SharedPreferences나 다른 저장소에서 가져오기
        return "CurrentUser"
    }
}