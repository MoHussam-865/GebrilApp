package com.android_a865.gebril_app.feature_main.items_choose

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.common.adapters.ChooseInvoiceItemsAdapter
import com.android_a865.gebril_app.common.adapters.ChosenItemsAdapter
import com.android_a865.gebril_app.common.adapters.PathIndicatorAdapter
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.databinding.FragmentItemsChooseBinding
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.scrollToEnd
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import com.android_a865.gebril_app.utils.showMessage
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ItemsChooseFragment : Fragment(R.layout.fragment_items_choose),
    ChooseInvoiceItemsAdapter.OnItemEventListener,
    ChosenItemsAdapter.OnItemEventListener {

    private val viewModel by viewModels<ItemsChooseViewModel>()
    private val itemsAdapter = ChooseInvoiceItemsAdapter(this)
    private val chosenItemsAdapter = ChosenItemsAdapter(this)
    private val pathIndicator = PathIndicatorAdapter()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()
        val binding = FragmentItemsChooseBinding.bind(view)

        binding.apply {

            itemsList.apply {
                adapter = itemsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            pathList.apply {
                adapter = pathIndicator
                setHasFixedSize(true)
            }

            chosenItemsList.apply {
                adapter = chosenItemsAdapter
                setHasFixedSize(true)
            }

            next.setOnClickListener {
                viewModel.onNextClicked()
            }

            viewModel.currentPath.asLiveData().observe(viewLifecycleOwner) {
                pathIndicator.submitPath(it)
                pathList.scrollToEnd()
            }

            viewModel.selectedItems.observe(viewLifecycleOwner) {
                chosenItemsList.isVisible = it.isNotEmpty()
                chosenItemsAdapter.submitList(it)
                chosenItemsList.scrollToEnd()
                itemsCount.text = it.size.toString()
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

        // get items from the confirmation fragment
        val observed = "chosen_invoice_items"
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            viewModel.onItemsSelected(get(observed))
            set(observed, null)
        }

    }


    override fun onItemClicked(item: InvoiceItem) = viewModel.onItemClicked(item)

    override fun onAddItemClicked(item: InvoiceItem) = viewModel.onAddItemClicked(item)

    override fun onMinusItemClicked(item: InvoiceItem) = viewModel.onMinusItemClicked(item)

    override fun onRemoveItemClicked(item: InvoiceItem) = viewModel.onItemRemoveClicked(item)

    override fun onQtySet(item: InvoiceItem, qty: Double) = viewModel.onQtySet(item, qty)
}