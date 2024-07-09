package com.example.madcamp_week2.tab1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp_week2.R
import com.example.madcamp_week2.databinding.ItemReadBookGridBinding

class ReadBooksGridAdapter(private val onItemClick: (Book) -> Unit)
    : ListAdapter<Pair<String, String?>, ReadBooksGridAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemReadBookGridBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookViewHolder(
        private val binding: ItemReadBookGridBinding,
        private val onItemClick: (Book) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(bookPair: Pair<String, String?>) {
            val (isbn, imageUrl) = bookPair
            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.book_placeholder)
                .into(binding.bookImageView)

            binding.root.setOnClickListener {
                // Here you should fetch the full Book object using the ISBN
                // For now, we'll create a dummy Book object
                onItemClick(Book(isbn, imageUrl ?: "", "", "", "", isbn))
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