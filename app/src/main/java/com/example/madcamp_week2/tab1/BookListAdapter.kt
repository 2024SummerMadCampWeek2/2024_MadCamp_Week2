package com.example.madcamp_week2.tab1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp_week2.R
import com.example.madcamp_week2.databinding.ItemBookSmallBinding

class BookListAdapter(private val isReadBooks: Boolean) : ListAdapter<Pair<String, String?>, BookListAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookViewHolder(private val binding: ItemBookSmallBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bookData: Pair<String, String?>) {
            val (isbn, imageUrl) = bookData

            if (imageUrl != null) {
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .placeholder(R.drawable.book_placeholder) // 플레이스홀더 이미지 추가
                    .error(R.drawable.book_error) // 에러 이미지 추가
                    .into(binding.bookCoverImageView)
            } else {
                binding.bookCoverImageView.setImageResource(R.drawable.book_placeholder)
            }

            binding.bookTitleTextView.text = isbn // 임시로 ISBN 표시

            if (isReadBooks) {
                binding.bookRatingTextView.visibility = View.VISIBLE
                // 여기에 평점 표시 로직을 추가할 수 있습니다.
            } else {
                binding.bookRatingTextView.visibility = View.GONE
            }
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Pair<String, String?>>() {
        override fun areItemsTheSame(oldItem: Pair<String, String?>, newItem: Pair<String, String?>): Boolean {
            return oldItem.first == newItem.first
        }

        override fun areContentsTheSame(oldItem: Pair<String, String?>, newItem: Pair<String, String?>): Boolean {
            return oldItem == newItem
        }
    }
}
