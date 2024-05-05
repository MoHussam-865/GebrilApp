package com.android_a865.gebril_app.common.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android_a865.gebril_app.databinding.AdapterClientsViewBinding
import com.android_a865.gebril_app.feature_client.domain.model.Client

class ClientsAdapter(
    private val listener: OnItemEventListener
) : ListAdapter<Client, ClientsAdapter.ViewHolder>(ClientDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            AdapterClientsViewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))


    inner class ViewHolder(private val binding: AdapterClientsViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClicked(getItem(position))
                }
            }
        }


        fun bind(client: Client) {
            binding.apply {
                clientName.text = client.name
            }
        }
    }

    class ClientDiffCallback : DiffUtil.ItemCallback<Client>() {
        override fun areItemsTheSame(oldItem: Client, newItem: Client): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Client, newItem: Client): Boolean =
            oldItem == newItem
    }

    interface OnItemEventListener {
        fun onItemClicked(client: Client)
    }
}