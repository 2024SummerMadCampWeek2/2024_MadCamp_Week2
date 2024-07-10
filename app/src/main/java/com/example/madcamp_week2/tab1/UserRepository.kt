package com.example.madcamp_week2.tab1

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserRepository(context: Context) {
    private val userDao = UserDatabase.getInstance(context).userDao()
    private val userAPI = Retrofit.Builder()
        .baseUrl("http://15.165.64.45/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(UserAPI::class.java)
    private val gson = Gson()

    suspend fun getUser(username: String): UserData? = withContext(Dispatchers.IO) {
        try {
            val response = userAPI.getUser(username).execute()
            if (response.isSuccessful) {
                val userData = response.body()
                Log.d("UserRepository", "User data fetched from server: $userData")
                userData?.let { saveUserLocally(it) }
                userData
            } else {
                Log.e("UserRepository", "Failed to fetch user data: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching user data", e)
            null
        }
    }

    suspend fun updateUser(username: String, userData: UserData, imageByteArray: ByteArray?): Boolean = withContext(Dispatchers.IO) {
        try {
            val userDataJson = JSONObject().apply {
                put("name", userData.name)
                put("description", userData.description)
                put("reviewed_books", JSONArray(userData.reviewed_books.map { reviewedBook ->
                    JSONObject().apply {
                        put("ISBN", reviewedBook?.ISBN)
                        put("star", reviewedBook?.star)
                        put("review", reviewedBook?.review)
                        put("review_date", reviewedBook?.review_date)
                    }
                }))
                put("read_books", JSONArray(userData.read_books))
            }.toString()

            val userDataPart = RequestBody.create("application/json".toMediaTypeOrNull(), userDataJson)

            val imagePart = imageByteArray?.let { bytes ->
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
                MultipartBody.Part.createFormData("profileImage", "profile.jpg", requestFile)
            }

            val response = userAPI.updateUser(username, userDataPart, imagePart).execute()
            if (response.isSuccessful) {
                Log.d("UserRepository", "User updated successfully on server")
                saveUserLocally(userData)
                true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UserRepository", "Failed to update user on server. Status Code: ${response.code()}, Error: $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error updating user", e)
            false
        }
    }
    suspend fun createUser(userData: UserData): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.d("UserRepository", "Attempting to create user with data: $userData")
            val response = userAPI.createUser(userData).execute()
            if (response.isSuccessful) {
                Log.d("UserRepository", "User created successfully: $userData")
                saveUserLocally(userData)
                true
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("UserRepository", "Failed to create user. Status Code: ${response.code()}, Error: $errorBody")
                false
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error creating user", e)
            false
        }
    }

    suspend fun fetchBookImages(isbnList: List<String>): List<Pair<String, String?>> = withContext(Dispatchers.IO) {
        isbnList.map { isbn ->
            val imageUrl = NaverAPI.getBookImageByISBN(isbn)
            isbn to imageUrl
        }
    }

    private fun saveUserLocally(userData: UserData) {
        val userEntity = UserEntity(
            name = userData.name,
            profileImage = userData.profileImage,
            description = userData.description,
            reviewedBooks = gson.toJson(userData.reviewed_books),
            readBooks = gson.toJson(userData.read_books)
        )
        userDao.insertUser(userEntity)
        Log.d("UserRepository", "User data saved locally: $userEntity")
    }

    suspend fun getLocalUser(username: String): UserData? = withContext(Dispatchers.IO) {
        val localUser = userDao.getUser(username)
        Log.d("UserRepository", "Local user data: $localUser")
        localUser?.let { convertEntityToUserData(it) }
    }

    private fun convertEntityToUserData(entity: UserEntity): UserData {
        return UserData(
            name = entity.name,
            profileImage = entity.profileImage,
            description = entity.description,
            reviewed_books = gson.fromJson(entity.reviewedBooks, Array<ReviewedBook>::class.java).toList(),
            read_books = gson.fromJson(entity.readBooks, Array<String>::class.java).toList()
        )
    }

    suspend fun updateLocalUser(userData: UserData) = withContext(Dispatchers.IO) {
        saveUserLocally(userData)
    }

    suspend fun getReadBooks(username: String): List<Pair<String, String?>> = withContext(Dispatchers.IO) {
        val userData = getLocalUser(username)
        val readBooks = userData?.read_books?.take(10)?.reversed() ?: emptyList()
        fetchBookImages(readBooks)
    }

    suspend fun getToReadBooks(username: String): List<Pair<String, String?>> = withContext(Dispatchers.IO) {
        val userData = getLocalUser(username)
        val toReadBooks = userData?.reviewed_books
            ?.sortedByDescending { it.review_date }?.reversed()
            // 리뷰 날짜로 정렬
            ?.take(6)

            ?.map { it.ISBN }
            ?: emptyList()
        fetchBookImages(toReadBooks)
    }

    suspend fun addReadBook(username: String, isbn: String): Boolean = withContext(Dispatchers.IO) {
        val userData = getLocalUser(username)
        userData?.let {
            val updatedReadBooks = listOf(isbn) + it.read_books
            val updatedUserData = it.copy(read_books = updatedReadBooks)
            updateUser(username, updatedUserData, null)
        } ?: false
    }

    suspend fun addToReadBook(username: String, reviewedBook: ReviewedBook): Boolean = withContext(Dispatchers.IO) {
        val userData = getLocalUser(username)
        userData?.let {
            val updatedReviewedBooks = listOf(reviewedBook) + it.reviewed_books
            val updatedUserData = it.copy(reviewed_books = updatedReviewedBooks)
            updateUser(username, updatedUserData, null)
        } ?: false
    }

    suspend fun removeReadBook(username: String, isbn: String): Boolean = withContext(Dispatchers.IO) {
        val userData = getLocalUser(username)
        userData?.let {
            val updatedReadBooks = it.read_books.filter { it != isbn }
            val updatedReviewedBooks = it.reviewed_books.filter { it.ISBN != isbn }
            val updatedUserData = it.copy(
                read_books = updatedReadBooks,
                reviewed_books = updatedReviewedBooks
            )
            val updated = updateUser(username, updatedUserData, null)
            if (updated) {
                updateLocalUser(updatedUserData)
            }
            updated
        } ?: false
    }

    suspend fun updateOrAddReviewedBook(username: String, reviewedBook: ReviewedBook): Boolean = withContext(Dispatchers.IO) {
        val userData = getLocalUser(username)
        userData?.let {
            val updatedReviewedBooks = it.reviewed_books.filter { it.ISBN != reviewedBook.ISBN } + reviewedBook
            val updatedReadBooks = if (!it.read_books.contains(reviewedBook.ISBN)) {
                it.read_books + reviewedBook.ISBN
            } else {
                it.read_books
            }
            val updatedUserData = it.copy(
                reviewed_books = updatedReviewedBooks,
                read_books = updatedReadBooks
            )
            val updated = updateUser(username, updatedUserData, null)
            if (updated) {
                updateLocalUser(updatedUserData)
            }
            updated
        } ?: false
    }

    suspend fun refreshLocalUser(username: String) = withContext(Dispatchers.IO) {
        val updatedUserData = getUser(username)
        updatedUserData?.let { updateLocalUser(it) }
    }

    suspend fun getBookByISBN(isbn: String): Book? = withContext(Dispatchers.IO) {
        try {
            val response = NaverAPI.create().searchBooks(isbn, 1, 1).execute()
            if (response.isSuccessful) {
                response.body()?.items?.firstOrNull()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "Error fetching book by ISBN", e)
            null
        }
    }

    suspend fun getAllReviewedBooks(username: String): List<Pair<String, String?>> = withContext(Dispatchers.IO) {
        val userData = getLocalUser(username)
        val reviewedBooks = userData?.reviewed_books?.map { it.ISBN } ?: emptyList()
        fetchBookImages(reviewedBooks)
    }
}


