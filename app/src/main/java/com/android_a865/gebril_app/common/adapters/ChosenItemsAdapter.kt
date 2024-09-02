package com.android_a865.gebril_app.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.databinding.AdapterChosenItemsBinding

class ChosenItemsAdapter(
    private val listener: OnItemEventListener
) : ListAdapter<InvoiceItem, ChosenItemsAdapter.ViewHolder>(InvoiceItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            AdapterChosenItemsBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))


    inner class ViewHolder(private val binding: AdapterChosenItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.close.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onRemoveItemClicked(getItem(position))
                }
            }
        }


        fun bind(item: InvoiceItem) {
            binding.apply {
                itemName.text = item.name
                itemQty.text = item.qty.toString()
            }
        }
    }

    class InvoiceItemDiffCallback : DiffUtil.ItemCallback<InvoiceItem>() {
        override fun areItemsTheSame(oldItem: InvoiceItem, newItem: InvoiceItem) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: InvoiceItem, newItem: InvoiceItem) =
            oldItem == newItem
    }

    interface OnItemEventListener {
        fun onRemoveItemClicked(item: InvoiceItem)
    }
}