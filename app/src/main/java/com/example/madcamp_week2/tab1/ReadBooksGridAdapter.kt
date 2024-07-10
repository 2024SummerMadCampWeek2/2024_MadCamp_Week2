package com.example.madcamp_week2.tab1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp_week2.R
import com.example.madcamp_week2.databinding.ItemReadBookGridBinding

class ReadBooksGridAdapter(private val onItemClick: (String) -> Unit)
    : ListAdapter<Pair<String, String?>, ReadBooksGridAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemReadBookGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookViewHolder(private val binding: ItemReadBookGridBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Pair<String, String?>) {
            Glide.with(binding.root.context)
                .load(book.second)
                .placeholder(R.drawable.book_placeholder)
                .into(binding.bookImageView)

            binding.root.setOnClickListener { onItemClick(book.first) }
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