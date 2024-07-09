package com.example.madcamp_week2.tab1

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.madcamp_week2.R
import com.example.madcamp_week2.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.flexbox.AlignItems

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var userRepository: UserRepository
    private lateinit var readBooksAdapter: ReadBooksAdapter
    private lateinit var toReadBooksAdapter: ToReadBooksAdapter
    private lateinit var sessionManager: SessionManager
    private var profileImageData: String? = null

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
        setupOverlayButton()
        loadUserProfile()

        // Animate UI elements
        binding.userProfileImageView.alpha = 0f
        binding.userNameTextView.alpha = 0f
        binding.userBioTextView.alpha = 0f
        binding.readBooksRecyclerView.alpha = 0f
        binding.toReadBooksRecyclerView.alpha = 0f

        viewLifecycleOwner.lifecycleScope.launch {
            binding.userProfileImageView.animate().alpha(1f).setDuration(1000).start()
            binding.userNameTextView.animate().alpha(1f).setDuration(1000).start()
            binding.userBioTextView.animate().alpha(1f).setDuration(1000).start()
            binding.readBooksRecyclerView.animate().alpha(1f).setDuration(1000).start()
            binding.toReadBooksRecyclerView.animate().alpha(1f).setDuration(1000).start()
        }
    }

    private fun setupRecyclerViews() {
        readBooksAdapter = ReadBooksAdapter()
        binding.readBooksRecyclerView.apply {
            val flexboxLayoutManager = FlexboxLayoutManager(context).apply {
                justifyContent = JustifyContent.CENTER
                alignItems = AlignItems.CENTER
            }
            layoutManager = flexboxLayoutManager
            adapter = readBooksAdapter
        }

        toReadBooksAdapter = ToReadBooksAdapter { isbn ->
            // 책 프로필로 이동하는 로직
            navigateToBookDetail(isbn)
        }
        binding.toReadBooksRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = toReadBooksAdapter
            addItemDecoration(HorizontalSpaceItemDecoration(12))
        }
    }

    private fun navigateToBookDetail(isbn: String) {
        lifecycleScope.launch {
            val book = userRepository.getBookByISBN(isbn)
            book?.let {
                val intent = Intent(requireContext(), BookDetailActivity::class.java)
                intent.putExtra("book", it)
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
            }
        }
    }

    private fun setupEditButton() {
        binding.editProfileButton.setOnClickListener {
            val intent = Intent(activity, EditProfileActivity::class.java)
            startActivityForResult(intent, EDIT_PROFILE_REQUEST)
        }
    }

    private fun setupOverlayButton() {
        binding.overlayButton.setOnClickListener {
            val intent = Intent(requireContext(), ReadBooksActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            val username = sessionManager.getUserName()
            username?.let {
                val userData = userRepository.getLocalUser(it)
                userData?.let {
                    updateUserData(it)
                    if (profileImageData != it.profileImage) {
                        profileImageData = it.profileImage
                        updateProfileImage(profileImageData)
                    }
                }

                val readBooks = userRepository.getReadBooks(it)
                val toReadBooks = userRepository.getToReadBooks(it)

                withContext(Dispatchers.Main) {
                    readBooksAdapter.setBooks(toReadBooks)
                    toReadBooksAdapter.setBooks(readBooks)
                }
            }
        }
    }

    fun updateUserData(userData: UserData) {
        binding.userNameTextView.text = userData.name
        binding.userBioTextView.text = userData.description ?: "한 줄 소개를 입력해주세요."
        if (profileImageData != userData.profileImage) {
            profileImageData = userData.profileImage
            updateProfileImage(profileImageData)
        }
    }

    private fun updateProfileImage(imageData: String?) {
        imageData?.let { base64String ->
            val imageBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
            Glide.with(this)
                .load(imageBytes)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.userProfileImageView)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == EDIT_PROFILE_REQUEST && resultCode == Activity.RESULT_OK) {
            loadUserProfile()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        loadUserProfile()
    }

    companion object {
        private const val EDIT_PROFILE_REQUEST = 1
    }
}

class ReadBooksAdapter : RecyclerView.Adapter<ReadBooksAdapter.BookViewHolder>() {
    private var books: List<Pair<String, String?>> = listOf()

    fun setBooks(newBooks: List<Pair<String, String?>>) {
        books = newBooks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_read_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount() = books.size

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.bookImageView)

        fun bind(book: Pair<String, String?>) {
            Glide.with(itemView.context)
                .load(book.second)
                .placeholder(R.drawable.book_placeholder)
                .into(imageView)
        }
    }
}

class ToReadBooksAdapter(private val onItemClick: (String) -> Unit) : RecyclerView.Adapter<ToReadBooksAdapter.BookViewHolder>() {
    private var books: List<Pair<String, String?>> = listOf()

    fun setBooks(newBooks: List<Pair<String, String?>>) {
        books = newBooks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_to_read_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position])
    }

    override fun getItemCount() = books.size

    inner class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.bookImageView)

        fun bind(book: Pair<String, String?>) {
            Glide.with(itemView.context)
                .load(book.second)
                .placeholder(R.drawable.book_placeholder)
                .into(imageView)

            itemView.setOnClickListener {
                onItemClick(book.first)  // ISBN을 전달
            }
        }
    }
}

class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}

class HorizontalSpaceItemDecoration(private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.getChildAdapterPosition(view) != parent.adapter?.itemCount?.minus(1)) {
            outRect.right = space
        }
    }
}
