package com.android_a865.gebril_app.feature_main.presentation.invoices_view

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.common.adapters.InvoicesAdapter
import com.android_a865.gebril_app.data.domain.Invoice
import com.android_a865.gebril_app.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint
import gebril_app.R
import gebril_app.databinding.FragmentInvoicesViewBinding
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class InvoicesViewFragment : Fragment(R.layout.fragment_invoices_view),
    InvoicesAdapter.OnItemEventListener {

    private val viewModel by viewModels<InvoicesViewViewModel>()
    private val invoicesAdapter = InvoicesAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setUpActionBarWithNavController()
        val binding = FragmentInvoicesViewBinding.bind(view)

        binding.apply {
            invoicesList.apply {
                adapter = invoicesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewModel.invoices.asLiveData().observe(viewLifecycleOwner) {
                invoicesAdapter.submitList(it)
                tvEmpty.isVisible = it.isEmpty()
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.windowEvents.collect { event ->
                when (event) {
                    is InvoicesViewViewModel.WindowEvents.NavigateTo -> {
                        findNavController().navigate(event.direction)
                    }
                    is InvoicesViewViewModel.WindowEvents.SetAppSettings -> {
                        invoicesAdapter.setAppSettings(event.appSettings)
                    }
                }.exhaustive

            }
        }

    }

    override fun onItemClicked(invoice: Invoice) {
        viewModel.onEditInvoiceClicked(invoice)
    }
}