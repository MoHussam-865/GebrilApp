package com.android_a865.gebril_app.common.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.utils.setQty
import gebril_app.databinding.AdapterChooseItemsListBinding

class ChooseInvoiceItemsAdapter(
    private val listener: OnItemEventListener,
) : ListAdapter<InvoiceItem, ChooseInvoiceItemsAdapter.ViewHolder>(InvoiceItemDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            AdapterChooseItemsListBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(private val binding: AdapterChooseItemsListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                folderView.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        listener.onItemClicked(item)
                    }
                }

                addFirst.setOnClickListener { add() }

                add.setOnClickListener { add() }

                minus.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val item = getItem(position)
                        listener.onMinusItemClicked(item)
                    }
                }

                userInput.addTextChangedListener {
                    val text = it.toString()
                    if (text.isNotBlank()) {
                        try {
                            val qty = text.toDouble()
                            val position = adapterPosition
                            if (position != RecyclerView.NO_POSITION) {
                                val item = getItem(position)
                                if (qty != item.qty) {
                                    listener.onQtySet(item, qty)
                                }
                            }
                        } catch (e: Exception) {
                            Log.d("QtyInputError", e.message.toString())
                        }
                    }
                }
            }
        }

        private fun add() {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val item = getItem(position)
                listener.onAddItemClicked(item)
            }
        }

        fun bind(item: InvoiceItem) {
            binding.apply {
                item.apply {
                    itemView.isVisible = !isFolder
                    folderView.isVisible = isFolder

                    // for items
                    itemName.text = name
                    itemUP.text = price.toString()

                    userInput.setQty(qty.toString())
                    userInput.setSelection(userInput.length())
                    addFirst.isVisible = item.qty == 0.0
                    console.isVisible = item.qty != 0.0


                    // for folders
                    folderName.text = name
                }
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
        fun onItemClicked(item: InvoiceItem)

        fun onRemoveItemClicked(item: InvoiceItem)
        fun onAddItemClicked(item: InvoiceItem)
        fun onMinusItemClicked(item: InvoiceItem)
        fun onQtySet(item: InvoiceItem, qty: Double)
    }

}