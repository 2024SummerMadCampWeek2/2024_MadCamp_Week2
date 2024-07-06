package com.example.madcamp_week2

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp_week2.databinding.ItemBookBinding

class BookAdapter(private val onItemClick: (Book) -> Unit) : ListAdapter<Book, BookAdapter.BookViewHolder>(BookDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class BookViewHolder(private val binding: ItemBookBinding, private val onItemClick: (Book) -> Unit) : RecyclerView.ViewHolder(binding.root) {
        fun bind(book: Book) {
            Glide.with(binding.root.context)
                .load(book.image)
                .centerCrop()
                .into(binding.bookCover)

            itemView.setOnClickListener { onItemClick(book) }
        }
    }

    class BookDiffCallback : DiffUtil.ItemCallback<Book>() {
        override fun areItemsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem.isbn == newItem.isbn
        }

        override fun areContentsTheSame(oldItem: Book, newItem: Book): Boolean {
            return oldItem == newItem
        }
    }


}