package com.example.madcamp_week2.tab1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.example.madcamp_week2.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookAdapter: BookAdapter
    private var currentPage = 1
    private var isLoading = false
    private var currentQuery = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter { book ->
            val intent = Intent(activity, BookDetailActivity::class.java)
            intent.putExtra("book", book)
            startActivity(intent)
        }
        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.apply {
            layoutManager = gridLayoutManager
            adapter = bookAdapter
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val visibleItemCount = gridLayoutManager.childCount
                    val totalItemCount = gridLayoutManager.itemCount
                    val firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition()

                    if (!isLoading && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                        && firstVisibleItemPosition >= 0 && totalItemCount >= 10) {
                        loadMoreBooks()
                    }
                }
            })
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    currentQuery = it
                    currentPage = 1
                    bookAdapter.submitList(null)
                    searchBooks(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean = false
        })
    }

    private fun searchBooks(query: String) {
        Log.d("SearchFragment", "Searching for: $query")
        isLoading = true
        val start = (currentPage - 1) * 10 + 1
        val call = NaverAPI.create().searchBooks(query, start, 10)
        call.enqueue(object : Callback<BookSearchResponse> {
            override fun onResponse(call: Call<BookSearchResponse>, response: Response<BookSearchResponse>) {
                activity?.runOnUiThread {
                    isLoading = false
                    if (response.isSuccessful) {
                        val books = response.body()?.items ?: emptyList()
                        Log.d("SearchFragment", "Received ${books.size} books")
                        val currentList = bookAdapter.currentList.toMutableList()
                        currentList.addAll(books)
                        bookAdapter.submitList(currentList)
                        currentPage++
                    } else {
                        Log.e("SearchFragment", "Error: ${response.code()}")
                    }
                }
            }

            override fun onFailure(call: Call<BookSearchResponse>, t: Throwable) {
                activity?.runOnUiThread {
                    isLoading = false
                    Log.e("SearchFragment", "Network error", t)
                }
            }
        })
    }

    private fun loadMoreBooks() {
        if (!isLoading) {
            searchBooks(currentQuery)
        }
    }
}