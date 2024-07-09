package com.example.madcamp_week2.tab1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.madcamp_week2.databinding.ActivityReadBooksBinding
import kotlinx.coroutines.launch

class ReadBooksActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReadBooksBinding
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager
    private lateinit var readBooksAdapter: ReadBooksGridAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadBooksBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)
        sessionManager = SessionManager(this)
        setupRecyclerView()
        loadReadBooks()
    }

    private fun setupRecyclerView() {
        readBooksAdapter = ReadBooksGridAdapter { book ->
            val intent = Intent(this, BookDetailActivity::class.java)
            intent.putExtra("book", book)
            startActivity(intent)
        }
        binding.readBooksRecyclerView.apply {
            layoutManager = GridLayoutManager(this@ReadBooksActivity, 4)
            adapter = readBooksAdapter
        }
    }

    private fun loadReadBooks() {
        lifecycleScope.launch {
            val username = sessionManager.getUserName() ?: return@launch
            val readBooks = userRepository.getAllReadBooks(username)
            readBooksAdapter.submitList(readBooks)
        }
    }
}