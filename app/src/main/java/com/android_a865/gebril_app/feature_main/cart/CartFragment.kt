package com.android_a865.gebril_app.feature_main.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
import com.android_a865.gebril_app.feature_main.ActivityFeature2
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

            (requireActivity() as AppCompatActivity).setSupportActionBar(mainToolBar)

            listItem.apply {
                adapter = itemsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(false)
            }

            order.setOnClickListener {

                val intent  = Intent(requireActivity(), ActivityFeature2::class.java)
                startActivity(intent)
                //viewModel.viewPrices()
                //viewModel.onNextPressed()
            }



            viewModel.itemsFlow.asLiveData().observe(viewLifecycleOwner) {
                itemsAdapter.submitList(it)
                val isEmpty = it.isEmpty()
                visible.isVisible = !isEmpty
                empty.isVisible = isEmpty

            }
        }



        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.invoiceWindowEvents.collect { event ->
                    when (event) {
                        is CartFragmentViewModel.WindowEvents.ShowMessage -> {
                            Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                        }
                        is CartFragmentViewModel.WindowEvents.Navigate -> {
                            findNavController().navigate(event.direction)
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