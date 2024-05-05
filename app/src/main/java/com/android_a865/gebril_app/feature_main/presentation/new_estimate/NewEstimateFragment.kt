package com.android_a865.gebril_app.feature_main.presentation.new_estimate

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.common.adapters.InvoiceItemsAdapter
import com.android_a865.gebril_app.databinding.FragmentNewEstimateBinding
import com.android_a865.gebril_app.feature_in_app.presentation.main.SharedViewModel
import com.android_a865.gebril_app.feature_main.domain.model.InvoiceItem
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.filtered
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import com.android_a865.gebril_app.utils.toFormattedString
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


const val TAG = "AdModDebug"

@AndroidEntryPoint
class NewEstimateFragment : Fragment(R.layout.fragment_new_estimate),
    InvoiceItemsAdapter.OnItemEventListener {

    private val viewModel by viewModels<NewEstimateViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private val itemsAdapter = InvoiceItemsAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()

        val binding = FragmentNewEstimateBinding.bind(view)
        binding.apply {

            listItem.apply {
                adapter = itemsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(false)
            }

            addItems.setOnClickListener {
                findNavController().navigate(
                    NewEstimateFragmentDirections.actionNewEstimateFragmentToItemsChooseFragment(
                        viewModel.itemsFlow.value.filtered.toTypedArray()
                    )
                )
            }


            tvInvoiceTypes.setOnClickListener {
                viewModel.onInvoiceTypeSelected(requireContext())
            }

            tvViewClient.setOnClickListener {
                viewModel.onViewClientClicked()
            }

            tvClientName.setOnClickListener {
                viewModel.onChooseClientSelected()
            }

            viewModel.client.observe(viewLifecycleOwner) {
                tvClientName.text = it?.name
                tvViewClient.isVisible = it != null
            }

            viewModel.invoiceType.asLiveData().observe(viewLifecycleOwner) {
                tvInvoiceTypes.text = it.name
            }

            viewModel.itemsFlow.asLiveData().observe(viewLifecycleOwner) {
                itemsAdapter.submitList(it)
                itemsTotal.text = it.sumOf { item -> item.total }.toFormattedString()
            }

        }

        val observed = "choose_invoice_items"
        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
            viewModel.onItemsSelected(get(observed))
            set(observed, null)
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.invoiceWindowEvents.collect { event ->
                when (event) {
                    is NewEstimateViewModel.WindowEvents.NavigateBack -> {
                        Toast.makeText(
                            requireContext(),
                            event.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                        true
                    }
                    is NewEstimateViewModel.WindowEvents.OpenPdf -> {
                        findNavController().navigate(
                            NewEstimateFragmentDirections.actionNewEstimateFragmentToViewPdfFragment(
                                invoice = event.invoice
                            )
                        )
                        true
                    }
                    is NewEstimateViewModel.WindowEvents.ShowMessage -> {
                        Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                        true
                    }
                    is NewEstimateViewModel.WindowEvents.Navigate -> {
                        findNavController().navigate(event.direction)
                        true
                    }
                    is NewEstimateViewModel.WindowEvents.ShowAd -> {
                        if (sharedViewModel.isSubscribed.value != true) {
                            Log.d(TAG, "showing ad")
                            sharedViewModel.myAd?.show(requireActivity())
                        }
                        true
                    }
                }.exhaustive
            }
        }

        setFragmentResultListener("chosen_client") { _, bundle ->
            viewModel.onClientChosen(
                bundle.getParcelable("client")
            )
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.estimate_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.open_pdf -> {
                viewModel.onOpenPdfClicked()
                true
            }

            R.id.save_invoice -> {
                viewModel.onSaveClicked()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onItemRemoveClicked(item: InvoiceItem) = viewModel.onItemRemoveClicked(item)

    override fun onItemRemoveHold(item: InvoiceItem) = viewModel.onItemHold(requireContext(), item)

    override fun onPlusClicked(item: InvoiceItem) = viewModel.onOneItemAdded(item)

    override fun onMinusClicked(item: InvoiceItem) = viewModel.onOneItemRemoved(item)

    override fun onQtyChanged(item: InvoiceItem, text: String) =
        viewModel.onItemQtyChanged(item, text)


}