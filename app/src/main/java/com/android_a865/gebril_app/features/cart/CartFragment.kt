package com.android_a865.gebril_app.features.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.common.adapters.InvoiceItemsAdapter
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.databinding.FragmentCartBinding
import com.android_a865.gebril_app.features.pdf_preview.ActivityOrder
import com.android_a865.gebril_app.utils.toFormattedString
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart),
    InvoiceItemsAdapter.OnItemEventListener {

    private val viewModel by viewModels<CartFragmentViewModel>()
    private val itemsAdapter = InvoiceItemsAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setUpActionBarWithNavController()


        val binding = FragmentCartBinding.bind(view)
        binding.apply {

            listItem.apply {
                adapter = itemsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(false)
            }

            order.setOnClickListener {
                viewModel.onOrderClicked()
            }

            clearCart.setOnClickListener {
                viewModel.clearCartClicked()
            }


            viewModel.itemsFlow.asLiveData().observe(viewLifecycleOwner) {
                itemsAdapter.submitList(it)
                val isEmpty = it.isEmpty()
                visible.isVisible = !isEmpty
                empty.isVisible = isEmpty

                total.text = it.sumOf { i -> i.total }.toFormattedString()
            }

        }



        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.invoiceWindowEvents.collect { event ->
                    when (event) {
                        is CartFragmentViewModel.WindowEvents.ShowMessage -> {
                            Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                        }
                        CartFragmentViewModel.WindowEvents.ShowPdf -> {
                            val intent  = Intent(requireActivity(), ActivityOrder::class.java)
                            startActivity(intent)
                        }
                    }
            }
        }

    }



    override fun onItemRemoveClicked(item: InvoiceItem) {
        viewModel.onItemRemoveClicked(item)
    }
    override fun onPlusClicked(item: InvoiceItem) {
        viewModel.onOneItemAdded(item)
    }

    override fun onMinusClicked(item: InvoiceItem) {
        viewModel.onOneItemRemoved(item)
    }

    override fun onQtyChanged(item: InvoiceItem, text: String) {
        viewModel.onItemQtyChanged(item, text)
    }
}