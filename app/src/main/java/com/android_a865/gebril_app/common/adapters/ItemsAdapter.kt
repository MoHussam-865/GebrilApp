//package com.android_a865.gebril_app.common.adapters
//
//import android.annotation.SuppressLint
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import android.widget.CheckBox
//import androidx.core.view.isVisible
//import androidx.recyclerview.widget.DiffUtil
//import androidx.recyclerview.widget.ListAdapter
//import androidx.recyclerview.widget.RecyclerView
//import com.android_a865.gebril_app.databinding.AdapterItemsPageBinding
//import com.android_a865.gebril_app.feature_main.domain.model.Item
//import com.android_a865.gebril_app.utils.toFormattedString
//import gebril_app.databinding.AdapterItemsPageBinding
//
//class ItemsAdapter(
//    private val listener: OnItemEventListener,
//    private val selecting: Boolean = false
//) : ListAdapter<Item, ItemsAdapter.ViewHolder>(ItemDiffCallback()) {
//
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
//        ViewHolder(
//            AdapterItemsPageBinding.inflate(
//                LayoutInflater.from(parent.context), parent, false
//            )
//        )
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
//        holder.bind(getItem(position))
//
//    @SuppressLint("ClickableViewAccessibility")
//    inner class ViewHolder(private val binding: AdapterItemsPageBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//
//        init {
//            binding.apply {
//                root.apply {
//                    setOnClickListener {
//                        val position = adapterPosition
//                        if (position != RecyclerView.NO_POSITION) {
//                            val item = getItem(position)
//                            listener.onItemClicked(item, position)
//                        }
//                    }
//
//                    setOnLongClickListener {
//                        val position = adapterPosition
//                        if (position != RecyclerView.NO_POSITION) {
//                            val item = getItem(position)
//
//                            listener.onItemLongClick(item)
//                        }
//                        false
//                    }
//                }
//
//                selection.setOnClickListener {
//                    val position = adapterPosition
//                    val b = (it as CheckBox).isChecked
//                    if (position != RecyclerView.NO_POSITION) {
//                        val item = getItem(position)
//                        listener.onSelectionChange(item, position, b)
//                    }
//                }
//            }
//        }
//
//        fun bind(item: Item) {
//            binding.apply {
//                selection.isVisible = selecting
//                selection.isChecked = item.isSelected
//
//                if (item.isFolder) folderImage.setImageResource(R.drawable.folder)
//                else folderImage.setImageResource(R.drawable.item)
//                folderName.text = item.name
//                details.text = if (item.isFolder) "" else item.price.toFormattedString()
//                //details.text = if (item.isFolder) "${item.value}\n${item.value2}" else item.value.toString()
//            }
//        }
//    }
//
//    class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
//        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean =
//            oldItem == newItem
//    }
//
//    interface OnItemEventListener {
//        fun onItemClicked(item: Item, position: Int)
//        fun onItemLongClick(item: Item)
//        fun onSelectionChange(item: Item, position: Int, b: Boolean)
//    }
//}