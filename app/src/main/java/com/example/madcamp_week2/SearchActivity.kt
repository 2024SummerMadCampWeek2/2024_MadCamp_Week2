package com.example.madcamp_week2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.madcamp_week2.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var bookAdapter: BookAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter()
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@SearchActivity, 2)
            adapter = bookAdapter
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchBooks(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun searchBooks(query: String) {
        val call = NaverAPI.create().searchBooks(query)
        call.enqueue(object : Callback<BookSearchResponse> {
            override fun onResponse(call: Call<BookSearchResponse>, response: Response<BookSearchResponse>) {
                if (response.isSuccessful) {
                    val books = response.body()?.items ?: emptyList()
                    bookAdapter.submitList(books)
                }
            }

            override fun onFailure(call: Call<BookSearchResponse>, t: Throwable) {
                // 에러 처리
                t.printStackTrace()
            }
        })
    }
}