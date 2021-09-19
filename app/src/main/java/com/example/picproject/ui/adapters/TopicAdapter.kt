package com.example.picproject.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.picproject.data.Topic
import com.example.picproject.databinding.TopicItemBinding
import kotlin.math.roundToInt

class TopicAdapter(
    private val width: Int,
    private val height: Int,
    private val onClick: (topic: Topic) -> Unit
) :
    ListAdapter<Topic, TopicAdapter.TopicViewHolder>(
        TOPIC_COMPARATOR
    ) {

    inner class TopicViewHolder(private val binding: TopicItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(topic: Topic) {
            binding.apply {
                Glide
                    .with(itemView)
                    .load(topic.cover_photo.urls.small)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(topicImageview)

                topicTextview.text = topic.title

                binding.root.setOnClickListener {
                    onClick.invoke(topic)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val binding = TopicItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val margin: Int = (width * 0.04).roundToInt()

        binding.root.layoutParams.width = width - margin
        binding.root.layoutParams.height = height

        return TopicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val currentTopic = getItem(position)
        holder.bind(currentTopic)
    }

    companion object {
        private val TOPIC_COMPARATOR = object : DiffUtil.ItemCallback<Topic>() {
            override fun areItemsTheSame(oldItem: Topic, newItem: Topic): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Topic, newItem: Topic): Boolean {
                return oldItem == newItem
            }

        }
    }
}