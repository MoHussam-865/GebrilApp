package com.android_a865.gebril_app.common.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.databinding.AdapterNewEstimateBinding
import com.android_a865.gebril_app.utils.setQty
import com.android_a865.gebril_app.utils.toFormattedString

class InvoiceItemsAdapter(
    private val listener: OnItemEventListener,
) : ListAdapter<InvoiceItem, InvoiceItemsAdapter.ViewHolder>(ItemDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        AdapterNewEstimateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(private val binding: AdapterNewEstimateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                itemRemove.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        listener.onItemRemoveClicked(item)
                    }
                }

                plus.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        listener.onPlusClicked(item)
                    }
                }

                minus.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        listener.onMinusClicked(item)
                    }
                }

                etQty.addTextChangedListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        val text = it.toString()
                        if (text.isNotBlank()) {
                            try {
                                val qty = text.toDouble()
                                if (qty != item.qty) {
                                    listener.onQtyChanged(item, qty.toString())
                                }
                            } catch (e: Exception) {
                                Log.d("QtyInputError", e.message.toString())
                            }
                        } else {
                            listener.onQtyChanged(item, "0")
                        }
                    }
                }


            }
        }

        fun bind(item: InvoiceItem) {
            binding.apply {
                itemName.text = item.fullName
                etQty.setQty(item.qty.toFormattedString())
                etQty.setSelection(etQty.length())
                total.text = item.total.toFormattedString()
            }
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<InvoiceItem>() {
        override fun areItemsTheSame(oldItem: InvoiceItem, newItem: InvoiceItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: InvoiceItem, newItem: InvoiceItem): Boolean =
            oldItem == newItem
    }


    interface OnItemEventListener {
        fun onItemRemoveClicked(item: InvoiceItem)
        fun onPlusClicked(item: InvoiceItem)
        fun onMinusClicked(item: InvoiceItem)
        fun onQtyChanged(item: InvoiceItem, text: String)
    }

}