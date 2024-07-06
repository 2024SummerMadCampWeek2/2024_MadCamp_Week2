package com.example.madcamp_week2

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.madcamp_week2.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadCurrentProfile()
        setupImagePicker()
        setupSaveButton()
    }

    private fun loadCurrentProfile() {
        val sharedPref = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
        binding.editNameEditText.setText(sharedPref.getString("user_name", ""))
        binding.editBioEditText.setText(sharedPref.getString("user_bio", ""))
        val imageUri = sharedPref.getString("profile_image", null)
        if (imageUri != null) {
            Glide.with(this)
                .load(Uri.parse(imageUri))
                .into(binding.editProfileImageView)
            selectedImageUri = Uri.parse(imageUri)
        }
    }

    private fun setupImagePicker() {
        binding.changeImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            val sharedPref = getSharedPreferences("UserProfile", Context.MODE_PRIVATE)
            with (sharedPref.edit()) {
                putString("user_name", binding.editNameEditText.text.toString())
                putString("user_bio", binding.editBioEditText.text.toString())
                selectedImageUri?.let { uri ->
                    putString("profile_image", uri.toString())
                }
                apply()
            }
            finish()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.data
            Glide.with(this)
                .load(selectedImageUri)
                .into(binding.editProfileImageView)
        }
    }
}