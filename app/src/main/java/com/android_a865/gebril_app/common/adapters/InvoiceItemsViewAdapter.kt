package com.android_a865.gebril_app.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.databinding.AdapterInvoiceItemsViewBinding
import com.android_a865.gebril_app.utils.toFormattedString

class InvoiceItemsViewAdapter : ListAdapter<InvoiceItem, InvoiceItemsViewAdapter.ViewHolder>(ItemDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        AdapterInvoiceItemsViewBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(private val binding: AdapterInvoiceItemsViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: InvoiceItem) {
            binding.apply {
                itemName.text = item.fullName
                itemQty.text = item.qty.toFormattedString()
            }
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<InvoiceItem>() {
        override fun areItemsTheSame(oldItem: InvoiceItem, newItem: InvoiceItem): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: InvoiceItem, newItem: InvoiceItem): Boolean =
            oldItem == newItem
    }

}