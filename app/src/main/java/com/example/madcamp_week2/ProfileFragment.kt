package com.example.madcamp_week2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.madcamp_week2.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepository: UserRepository
    private lateinit var readBooksAdapter: BookListAdapter
    private lateinit var toReadBooksAdapter: BookListAdapter
    private var pendingUserData: UserData? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userRepository = UserRepository(requireContext())
        setupRecyclerViews()
        setupEditButton()

        // 대기 중인 사용자 데이터가 있으면 업데이트
        pendingUserData?.let {
            updateUserData(it)
            pendingUserData = null
        }
    }

    fun updateUserData(userData: UserData) {
        if (_binding == null) {
            // 뷰가 아직 생성되지 않았다면 데이터를 임시 저장
            pendingUserData = userData
            return
        }

        binding.userNameTextView.text = userData.name
        binding.userBioTextView.text = userData.description ?: "한 줄 소개를 입력해주세요."
        userData.profileImage?.let { imageUri ->
            Glide.with(this).load(imageUri).into(binding.userProfileImageView)
        }
        readBooksAdapter.submitList(userData.read_books)
        toReadBooksAdapter.submitList(userData.reviewed_books.map { it.ISBN })
    }

    private fun setupRecyclerViews() {
        readBooksAdapter = BookListAdapter()
        binding.readBooksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = readBooksAdapter
        }

        toReadBooksAdapter = BookListAdapter()
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
        lifecycleScope.launch {
            val userData = userRepository.getUser(binding.userNameTextView.text.toString())
            userData?.let { updateUserData(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}