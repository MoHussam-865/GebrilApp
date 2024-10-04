package com.android_a865.gebril_app.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.databinding.AdapterPathIndicatorBinding
import com.android_a865.gebril_app.utils.Path

class PathIndicatorAdapter(
    private val listener: OnItemEventListener,
) : ListAdapter<InvoiceItem, PathIndicatorAdapter.ViewHolder>(DiffCallback()) {

    private var currentPath: Path? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            AdapterPathIndicatorBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))


    fun submitPath(path: Path) {
        currentPath = path
        submitList(path.parents)
    }


    class DiffCallback : DiffUtil.ItemCallback<InvoiceItem>() {
        override fun areItemsTheSame(oldItem: InvoiceItem, newItem: InvoiceItem): Boolean =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: InvoiceItem, newItem: InvoiceItem): Boolean =
            oldItem == newItem
    }

    inner class ViewHolder(private val binding: AdapterPathIndicatorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {


                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        listener.pathChangeRequest(item)
                    }


            }
        }


        fun bind(item: InvoiceItem) {
            binding.folderName.text = item.name
        }
    }

    interface OnItemEventListener {
        fun pathChangeRequest(item: InvoiceItem)
    }
}