package com.example.picproject.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.picproject.data.UnsplashPhoto
import com.example.picproject.databinding.PhotoItemBinding
import kotlin.math.roundToInt

class SavedPhotoAdapter(
    private val width: Int,
    private val height: Int,
    private val onClick: (photo: UnsplashPhoto) -> Unit
) :
    ListAdapter<UnsplashPhoto, SavedPhotoAdapter.PhotoViewHolder>(
        PHOTO_COMPARATOR
    ) {

    inner class PhotoViewHolder(private val binding: PhotoItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: UnsplashPhoto) {
            binding.apply {
                Glide.with(itemView)
                    .load(photo.urls.small)
                    .centerCrop()
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .into(imageview)

                textview.text = "By: ${photo.user.username}"

                itemView.setOnClickListener {
                    onClick.invoke(photo)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding =
            PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        val margin: Int = (width * 0.075).roundToInt()

        binding.root.layoutParams.width = width - margin
        binding.root.layoutParams.height = height

        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    companion object {
        private val PHOTO_COMPARATOR = object : DiffUtil.ItemCallback<UnsplashPhoto>() {
            override fun areItemsTheSame(oldItem: UnsplashPhoto, newItem: UnsplashPhoto): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: UnsplashPhoto,
                newItem: UnsplashPhoto
            ): Boolean {
                return oldItem == newItem
            }

        }
    }
}