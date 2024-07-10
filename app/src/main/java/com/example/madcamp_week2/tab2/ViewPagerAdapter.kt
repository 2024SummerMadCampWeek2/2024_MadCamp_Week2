package com.example.madcamp_week2.tab2

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.madcamp_week2.R

class ViewPagerAdapter(
    private val longVideos: List<VideoItem>,
    private val keywordList: List<Trend>
) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.page_item, parent, false)
        return ViewPagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        // Return the number of full pages
        // Each page contains 2 videos, 2 keywords, and 1 more video
        return longVideos.size / 3 // Adjust this based on the number of videos per page
    }

    inner class ViewPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail1: ImageView = itemView.findViewById(R.id.thumbnail1)
        private val keyword1: TextView = itemView.findViewById(R.id.keyword1)
        private val thumbnail2: ImageView = itemView.findViewById(R.id.thumbnail2)
        private val keyword2: TextView = itemView.findViewById(R.id.keyword2)
        private val thumbnail3: ImageView = itemView.findViewById(R.id.thumbnail3)

        fun bind(position: Int) {
            val videoIndex1 = position * 3
            val videoIndex2 = videoIndex1 + 1
            val videoIndex3 = videoIndex2 + 1
            val keywordIndex1 = position * 2
            val keywordIndex2 = keywordIndex1 + 1

            // Set first video if available
            if (videoIndex1 < longVideos.size) {
                val video1 = longVideos[videoIndex1]
                Glide.with(itemView.context).load(video1.snippet.thumbnails.high.url).into(thumbnail1)
                thumbnail1.setOnClickListener {
                    val intent = Intent(itemView.context, YouTubePlayerActivity::class.java)
                    intent.putExtra(YouTubePlayerActivity.VIDEO_ID, video1.id)
                    itemView.context.startActivity(intent)
                }
            }

            // Set first keyword if available
            if (keywordIndex1 < keywordList.size) {
                val keyword1Text = keywordList[keywordIndex1].keyword
                keyword1.text = "#${keyword1Text}"
                keyword1.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$keyword1Text"))
                    itemView.context.startActivity(intent)
                }
            }

            // Set second video if available
            if (videoIndex2 < longVideos.size) {
                val video2 = longVideos[videoIndex2]
                Glide.with(itemView.context).load(video2.snippet.thumbnails.high.url).into(thumbnail2)
                thumbnail2.setOnClickListener {
                    val intent = Intent(itemView.context, YouTubePlayerActivity::class.java)
                    intent.putExtra(YouTubePlayerActivity.VIDEO_ID, video2.id)
                    itemView.context.startActivity(intent)
                }
            }

            // Set second keyword if available
            if (keywordIndex2 < keywordList.size) {
                val keyword2Text = keywordList[keywordIndex2].keyword
                keyword2.text = "#${keyword2Text}"
                keyword2.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$keyword2Text"))
                    itemView.context.startActivity(intent)
                }
            }

            // Set third video if available
            if (videoIndex3 < longVideos.size) {
                val video3 = longVideos[videoIndex3]
                Glide.with(itemView.context).load(video3.snippet.thumbnails.high.url).into(thumbnail3)
                thumbnail3.setOnClickListener {
                    val intent = Intent(itemView.context, YouTubePlayerActivity::class.java)
                    intent.putExtra(YouTubePlayerActivity.VIDEO_ID, video3.id)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }
}
