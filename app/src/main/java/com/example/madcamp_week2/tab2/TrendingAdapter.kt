// TrendingAdapter.kt
package com.example.madcamp_week2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrendingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mixedList = mutableListOf<Any>()

    fun submitMixedList(list: List<Any>) {
        mixedList.clear()
        mixedList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_VIDEO -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_video, parent, false)
                VideoViewHolder(view)
            }
            VIEW_TYPE_KEYWORD -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_keyword, parent, false)
                KeywordViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VideoViewHolder -> holder.bind(mixedList[position] as VideoItem)
            is KeywordViewHolder -> holder.bind(mixedList[position] as Trend)
        }
    }

    override fun getItemCount(): Int {
        return mixedList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (mixedList[position]) {
            is VideoItem -> VIEW_TYPE_VIDEO
            is Trend -> VIEW_TYPE_KEYWORD
            else -> throw IllegalArgumentException("Invalid item type")
        }
    }

    companion object {
        private const val VIEW_TYPE_VIDEO = 0
        private const val VIEW_TYPE_KEYWORD = 1
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val thumbnail: ImageView = itemView.findViewById(R.id.thumbnail)

        fun bind(video: VideoItem) {
            title.text = video.snippet.title
            Glide.with(itemView.context).load(video.snippet.thumbnails.default.url).into(thumbnail)
        }
    }

    class KeywordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)

        fun bind(trend: Trend) {
            title.text = trend.keyword
        }
    }
}
