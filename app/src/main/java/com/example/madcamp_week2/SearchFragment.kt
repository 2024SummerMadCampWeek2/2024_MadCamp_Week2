package com.example.madcamp_week2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import com.example.madcamp_week2.databinding.FragmentSearchBinding
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookAdapter: BookAdapter
    private var currentPage = 1
    private var isLoading = false
    private var currentQuery = ""
    private lateinit var bookRepository: BookRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookRepository = (requireActivity().application as MyApplication).bookRepository
        setupRecyclerView()
        setupSearchView()
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter { book -> showBookProfile(book) }
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

    private fun showBookProfile(book: Book) {
        val intent = Intent(requireContext(), BookProfileActivity::class.java)
        intent.putExtra("book", book)
        startActivity(intent)
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
        lifecycleScope.launch {
            try {
                val books = bookRepository.getBooks(query, start, 10)
                isLoading = false
                val currentList = bookAdapter.currentList.toMutableList()
                currentList.addAll(books)
                bookAdapter.submitList(currentList)
                currentPage++
            } catch (e: Exception) {
                isLoading = false
                Log.e("SearchFragment", "Error searching books", e)
            }
        }
    }

    private fun loadMoreBooks() {
        if (!isLoading) {
            searchBooks(currentQuery)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}