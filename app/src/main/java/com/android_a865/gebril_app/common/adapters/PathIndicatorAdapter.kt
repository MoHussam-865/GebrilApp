package com.android_a865.gebril_app.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android_a865.gebril_app.databinding.AdapterPathIndicatorBinding
import com.android_a865.gebril_app.utils.Path

class PathIndicatorAdapter : ListAdapter<String, PathIndicatorAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            AdapterPathIndicatorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))


    fun submitPath(path: Path) = submitList(path.folders)


    class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
            oldItem == newItem
    }

    class ViewHolder(private val binding: AdapterPathIndicatorBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String) {
            binding.folderName.text = name
        }
    }
}