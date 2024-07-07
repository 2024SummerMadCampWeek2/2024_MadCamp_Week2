package com.example.madcamp_week2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp_week2.databinding.ItemBookSmallBinding

class BookListAdapter : ListAdapter<String, BookListAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookViewHolder(private val binding: ItemBookSmallBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(isbn: String) {
            // ISBN을 사용하여 책 정보를 가져오는 로직 구현 필요
            // 예: API 호출 또는 로컬 데이터베이스에서 조회
            // 여기서는 간단히 ISBN만 표시
            binding.bookTitleTextView.text = isbn
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }
    }
}