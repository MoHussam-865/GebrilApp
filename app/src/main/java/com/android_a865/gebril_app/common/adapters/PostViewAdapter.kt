package com.android_a865.gebril_app.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android_a865.gebril_app.data.entities.Post
import com.android_a865.gebril_app.databinding.AdapterChosenItemsBinding
import com.android_a865.gebril_app.databinding.AdapterPostViewBinding
import com.android_a865.gebril_app.utils.getTheImage

class PostViewAdapter  : ListAdapter<Post, PostViewAdapter.ViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            AdapterPostViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))


    inner class ViewHolder(private val binding: AdapterPostViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(post: Post) {
            binding.apply {
                title.text = post.title
                content.text = post.content

                post.imageAbsolutePath?.let {
                    postImage.setImageBitmap(getTheImage(it))
                }
            }
        }
    }

    class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Post, newItem: Post) =
            oldItem == newItem
    }

}