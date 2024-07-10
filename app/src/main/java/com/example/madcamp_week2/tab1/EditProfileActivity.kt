package com.example.madcamp_week2.tab1


import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.madcamp_week2.R
import com.example.madcamp_week2.databinding.ActivityEditProfileBinding
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    private lateinit var userRepository: UserRepository
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userRepository = UserRepository(this)
        sessionManager = SessionManager(this)

        loadCurrentProfile()
        setupImagePicker()
        setupSaveButton()
    }

    private fun loadCurrentProfile() {
        lifecycleScope.launch {
            val currentUsername = sessionManager.getUserName() ?: ""
            val userData = userRepository.getLocalUser(currentUsername)

            userData?.let { user ->
                binding.editNameEditText.setText(user.name)
                binding.editBioEditText.setText(user.description)
                user.profileImage?.let { imageBase64 ->
                    val imageBytes = android.util.Base64.decode(imageBase64, android.util.Base64.DEFAULT)
                    Glide.with(this@EditProfileActivity)
                        .load(imageBytes)
                        .into(binding.editProfileImageView)
                }
            }
        }
    }

    private fun setupImagePicker() {
        binding.editProfileImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun setupSaveButton() {
        binding.saveButton.setOnClickListener {
            lifecycleScope.launch {
                val name = binding.editNameEditText.text.toString()
                val bio = binding.editBioEditText.text.toString()

                val imageByteArray = selectedImageUri?.let { uri ->
                    compressImage(uri)
                }

                val currentUsername = sessionManager.getUserName() ?: ""
                val currentUserData = userRepository.getLocalUser(currentUsername)

                val updatedUserData = currentUserData?.copy(
                    name = name,
                    description = bio,
                    profileImage = imageByteArray?.let {
                        android.util.Base64.encodeToString(it, android.util.Base64.DEFAULT)
                    } ?: currentUserData.profileImage,
                    reviewed_books = currentUserData.reviewed_books,
                    read_books = currentUserData.read_books
                ) ?: UserData(
                    name = name,
                    profileImage = null,
                    description = bio,
                    reviewed_books = emptyList(),
                    read_books = emptyList()
                )

                val updated = userRepository.updateUser(currentUsername, updatedUserData, imageByteArray)
                if (updated) {
                    Log.d("EditProfileActivity", "Profile updated successfully")
                    finish()
                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                } else {
                    Log.e("EditProfileActivity", "Failed to update profile")
                }
            }
        }
    }

    private fun compressImage(imageUri: Uri): ByteArray? {
        return try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            val outputStream = ByteArrayOutputStream()
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            outputStream.toByteArray()
        } catch (e: Exception) {
            Log.e("EditProfileActivity", "Error compressing image", e)
            null
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