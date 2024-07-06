package com.example.madcamp_week2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.madcamp_week2.databinding.FragmentProfileBinding
import com.kakao.sdk.user.UserApiClient
import android.content.Intent
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var readBooksAdapter: BookAdapter
    private lateinit var toReadBooksAdapter: BookAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                Log.e("ProfileFragment", "Failed to get user info", error)
            } else if (user != null) {
                val userName = user.kakaoAccount?.profile?.nickname ?: return@me
                MyApplication.serverAPI.getUserData(userName).enqueue(object : Callback<UserData> {
                    override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                        if (response.isSuccessful) {
                            val userData = response.body()
                            updateUI(userData)
                        } else {
                            Log.e("ProfileFragment", "Failed to get user data: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<UserData>, t: Throwable) {
                        Log.e("ProfileFragment", "Network error", t)
                    }
                })
            }
        }
    }

    private fun updateUI(userData: UserData?) {
        userData?.let {
            binding.userNameTextView.text = it.name
            binding.userBioTextView.text = it.description
            Glide.with(this).load(it.profileImage).into(binding.userProfileImageView)

            val recentReadBooks = it.reviewedBooks.takeLast(8)
            readBooksAdapter.submitList(recentReadBooks.map { book ->
                Book(book.isbn, "", "", "", "", book.isbn)
            })

            val recentToReadBooks = it.readBooks.takeLast(4)
            toReadBooksAdapter.submitList(recentToReadBooks.map { isbn ->
                Book(isbn, "", "", "", "", isbn)
            })
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