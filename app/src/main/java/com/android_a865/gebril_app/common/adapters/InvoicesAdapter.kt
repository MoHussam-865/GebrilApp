package com.android_a865.gebril_app.common.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android_a865.gebril_app.data.domain.InvoiceHolder
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.utils.DATE_FORMATS
import com.android_a865.gebril_app.utils.date
import gebril_app.databinding.AdapterInvoiceViewBinding

class InvoicesAdapter(
    private val listener: OnItemEventListener,
) : ListAdapter<InvoiceHolder, InvoicesAdapter.ViewHolder>(InvoiceDiffCallback()) {

    private lateinit var context: Context
    private var appSettings: AppSettings? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(
            AdapterInvoiceViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    fun setAppSettings(appSettings: AppSettings) {
        this.appSettings = appSettings
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))

    inner class ViewHolder(private val binding: AdapterInvoiceViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnEdit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    listener.onItemClicked(item)
                }
            }

        }

        fun bind(invoice: InvoiceHolder) {
            binding.apply {
                tvInvoiceDate.text = invoice.date.date(appSettings?.dateFormat ?: DATE_FORMATS[0])

                val itemsAdapter = InvoiceItemsViewAdapter().apply {
                    submitList(invoice.items)
                }
                itemsList.apply {
                    adapter = itemsAdapter
                    layoutManager = LinearLayoutManager(context)
                    setHasFixedSize(false)
                }

            }
        }
    }

    class InvoiceDiffCallback : DiffUtil.ItemCallback<InvoiceHolder>() {
        override fun areItemsTheSame(oldItem: InvoiceHolder, newItem: InvoiceHolder): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: InvoiceHolder, newItem: InvoiceHolder): Boolean =
            oldItem == newItem
    }

    interface OnItemEventListener {
        fun onItemClicked(invoice: InvoiceHolder)
    }
}