package com.android_a865.gebril_app.features.shopping

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.common.GridSpacingItemDecoration
import com.android_a865.gebril_app.common.adapters.ChooseInvoiceItemsAdapter
import com.android_a865.gebril_app.common.adapters.PathIndicatorAdapter
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.databinding.FragmentShoppingBinding
import com.android_a865.gebril_app.features.history.SharedViewModel
import com.android_a865.gebril_app.utils.Path
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.scrollToEnd
import com.android_a865.gebril_app.utils.showMessage
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ShoppingFragment : Fragment(R.layout.fragment_shopping),
    ChooseInvoiceItemsAdapter.OnItemEventListener, PathIndicatorAdapter.OnItemEventListener {

    private val viewModel by viewModels<ItemsChooseViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private val itemsAdapter = ChooseInvoiceItemsAdapter(this)
    private val pathIndicator = PathIndicatorAdapter(this)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setUpActionBarWithNavController()

        val binding = FragmentShoppingBinding.bind(view)
        binding.apply {

            val spacingInPixel = resources.getDimensionPixelSize(R.dimen.grid_spacing)
            itemsList.apply {
                adapter = itemsAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
                addItemDecoration(
                    GridSpacingItemDecoration(
                        2, spacingInPixel, true
                    )
                )
                setHasFixedSize(true)
            }

            pathList.apply {
                adapter = pathIndicator
                setHasFixedSize(true)
            }

            viewModel.currentPath.asLiveData().observe(viewLifecycleOwner) {
                pathIndicator.submitPath(it)
                pathList.scrollToEnd()
            }

            searchView.addTextChangedListener { txt ->
                viewModel.onSearchChanged(txt.toString())
            }

            order.setOnClickListener {
                sharedViewModel.selectedItem.value = R.id.navigation_cart
            }

            viewModel.selectedItems.asLiveData().observe(viewLifecycleOwner) {
                order.text = it.size.toString()
                order.isVisible = it.isNotEmpty()
            }

            viewModel.itemsData.asLiveData().observe(viewLifecycleOwner) {
                itemsAdapter.submitList(it)
            }
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModel.onBackPress()
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.itemsWindowEvents.collect { event ->
                when (event) {
                    ItemsChooseViewModel.ItemsWindowEvents.GoBack -> {
                        callback.remove()
                        findNavController().popBackStack()
                        Log.d("ItemsChooseFragment", "this fragment is popped")
                        true
                    }

                    is ItemsChooseViewModel.ItemsWindowEvents.NavigateTo -> {
                        findNavController().navigate(event.direction)
                        true
                    }

                    is ItemsChooseViewModel.ItemsWindowEvents.InvalidInput -> {
                        showMessage(event.msg)
                        true
                    }
                }.exhaustive
            }
        }

    }


    override fun onItemClicked(item: InvoiceItem) = viewModel.onItemClicked(item)

    override fun onAddItemClicked(item: InvoiceItem) {
        viewModel.onAddItemClicked(item)
    }

    override fun onMinusItemClicked(item: InvoiceItem) {
        viewModel.onMinusItemClicked(item)
    }

    override fun onRemoveItemClicked(item: InvoiceItem) {
        viewModel.onItemRemoveClicked(item)
    }

    override fun onQtySet(item: InvoiceItem, qty: Double) {
        viewModel.onQtySet(item, qty)
    }

    override fun pathChangeRequest(item: InvoiceItem) {
        viewModel.onPathChangeRequested(item)
    }


}