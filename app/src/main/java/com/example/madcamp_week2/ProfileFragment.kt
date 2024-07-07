package com.example.madcamp_week2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.madcamp_week2.databinding.FragmentProfileBinding
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var readBooksAdapter: BookAdapter
    private lateinit var toReadBooksAdapter: BookAdapter
    private lateinit var userRepository: UserRepository
    private lateinit var bookRepository: BookRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userRepository = (requireActivity().application as MyApplication).userRepository
        bookRepository = (requireActivity().application as MyApplication).bookRepository
        setupRecyclerViews()
        loadUserProfile()
        setupEditButton()
    }

    private fun setupRecyclerViews() {
        readBooksAdapter = BookAdapter { book -> showBookProfile(book) }
        toReadBooksAdapter = BookAdapter { book -> showBookProfile(book) }

        binding.readBooksRecyclerView.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = readBooksAdapter
        }

        binding.toReadBooksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = toReadBooksAdapter
        }
    }

    private fun loadUserProfile() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                // Handle error
            } else if (user != null) {
                lifecycleScope.launch {
                    val userName = user.kakaoAccount?.profile?.nickname ?: return@launch
                    val userData = userRepository.getUser(userName)
                    userData?.let { updateUI(it) }
                }
            }
        }
    }

    private fun updateUI(userData: User) {
        binding.userNameTextView.text = userData.name
        binding.userBioTextView.text = userData.description
        Glide.with(this).load(userData.profileImage).into(binding.userProfileImageView)

        lifecycleScope.launch {
            val recentReadBooks = bookRepository.getBooksByISBN(userData.reviewedBooks.map { it.isbn }.takeLast(8))
            readBooksAdapter.submitList(recentReadBooks)

            val recentToReadBooks = bookRepository.getBooksByISBN(userData.readBooks.takeLast(4))
            toReadBooksAdapter.submitList(recentToReadBooks)
        }
    }

    private fun showBookProfile(book: Book) {
        val intent = Intent(requireContext(), BookProfileActivity::class.java)
        intent.putExtra("book", book)
        startActivity(intent)
    }

    private fun setupEditButton() {
        binding.editProfileButton.setOnClickListener {
            val intent = Intent(requireContext(), EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}