package com.android_a865.gebril_app.feature_main.presentation.invoices_view

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.common.adapters.InvoicesAdapter
import com.android_a865.gebril_app.databinding.FragmentInvoicesViewBinding
import com.android_a865.gebril_app.feature_main.domain.model.Invoice
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class InvoicesViewFragment : Fragment(R.layout.fragment_invoices_view),
    InvoicesAdapter.OnItemEventListener {

    private val viewModel by viewModels<InvoicesViewViewModel>()
    private val invoicesAdapter = InvoicesAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()
        val binding = FragmentInvoicesViewBinding.bind(view)

        binding.apply {
            invoicesList.apply {
                adapter = invoicesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            fab.setOnClickListener {
                viewModel.onNewInvoiceClicked()
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


        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.invoices_view_options, menu)

        viewModel.filterOptions.asLiveData().observe(viewLifecycleOwner) {
            when (it) {
                FilterOptions.All -> menu.findItem(R.id.all_invoices).isChecked = true
                FilterOptions.Draft -> menu.findItem(R.id.draft).isChecked = true
                FilterOptions.Estimate -> menu.findItem(R.id.estimate).isChecked = true
                FilterOptions.Invoice -> menu.findItem(R.id.invoice).isChecked = true
            }.exhaustive
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.all_invoices -> {
                viewModel.filterOptions.value = FilterOptions.All
                true
            }

            R.id.invoice -> {
                viewModel.filterOptions.value = FilterOptions.Invoice
                true
            }

            R.id.estimate -> {
                viewModel.filterOptions.value = FilterOptions.Estimate
                true
            }

            R.id.draft -> {
                viewModel.filterOptions.value = FilterOptions.Draft
                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onItemClicked(invoice: Invoice) {
        viewModel.onEditInvoiceClicked(invoice)
    }
}