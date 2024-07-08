package com.example.madcamp_week2.tab1

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.madcamp_week2.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import android.util.Base64
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepository: UserRepository
    private lateinit var readBooksAdapter: BookListAdapter
    private lateinit var toReadBooksAdapter: BookListAdapter
    private lateinit var sessionManager: SessionManager
    private var pendingUserData: UserData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userRepository = UserRepository(requireContext())
        sessionManager = SessionManager(requireContext())
        setupRecyclerViews()
        setupEditButton()
        loadUserProfile()
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            val username = sessionManager.getUserName()
            username?.let {
                val userData = userRepository.getLocalUser(it)
                userData?.let { updateUserData(it) }
            }
        }
    }


    fun updateUserData(userData: UserData) {
        if (_binding == null) {
            pendingUserData = userData
            return
        }

        binding.userNameTextView.text = userData.name
        binding.userBioTextView.text = userData.description ?: "한 줄 소개를 입력해주세요."
        userData.profileImage?.let { base64String ->
            try {
                val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                binding.userProfileImageView.setImageBitmap(bitmap)
            } catch (e: IllegalArgumentException) {
                Log.e("ProfileFragment", "Error decoding Base64 string", e)
            }
        }

        lifecycleScope.launch {
            val readBooks = userRepository.fetchBookImages(userData.read_books)
            val toReadBooks = userRepository.fetchBookImages(userData.reviewed_books.mapNotNull { it?.ISBN })

            withContext(Dispatchers.Main) {
                readBooksAdapter.submitList(readBooks)
                toReadBooksAdapter.submitList(toReadBooks)
            }
        }

        Log.d("ProfileFragment", "User data updated: $userData")
    }

    private fun setupRecyclerViews() {
        readBooksAdapter = BookListAdapter(true) // true for read books
        binding.readBooksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = readBooksAdapter
        }

        toReadBooksAdapter = BookListAdapter(false) // false for to-read books
        binding.toReadBooksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = toReadBooksAdapter
        }
    }

    private fun setupEditButton() {
        binding.editProfileButton.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
