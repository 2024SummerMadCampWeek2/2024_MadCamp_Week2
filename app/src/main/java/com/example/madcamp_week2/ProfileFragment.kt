package com.example.madcamp_week2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.madcamp_week2.databinding.FragmentProfileBinding
import com.kakao.sdk.user.UserApiClient
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadUserProfile()
        setupEditButton()
    }

    private fun loadUserProfile() {
        val sharedPref = activity?.getSharedPreferences("UserProfile", Context.MODE_PRIVATE) ?: return
        val name = sharedPref.getString("user_name", null)
        val imageUri = sharedPref.getString("profile_image", null)
        val bio = sharedPref.getString("user_bio", null)

        if (name != null) {
            binding.userNameTextView.text = name
        } else {
            // 저장된 이름이 없으면 카카오 계정에서 가져오기
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e("ProfileFragment", "Failed to get user info", error)
                } else if (user != null) {
                    binding.userNameTextView.text = user.kakaoAccount?.profile?.nickname
                }
            }
        }

        if (imageUri != null) {
            Glide.with(this)
                .load(Uri.parse(imageUri))
                .into(binding.userProfileImageView)
        } else {
            // 저장된 이미지가 없으면 카카오 프로필 이미지 사용
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    Log.e("ProfileFragment", "Failed to get user info", error)
                } else if (user != null) {
                    Glide.with(this)
                        .load(user.kakaoAccount?.profile?.thumbnailImageUrl)
                        .into(binding.userProfileImageView)
                }
            }
        }

        binding.userBioTextView.text = bio ?: "한 줄 소개를 입력해주세요."
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