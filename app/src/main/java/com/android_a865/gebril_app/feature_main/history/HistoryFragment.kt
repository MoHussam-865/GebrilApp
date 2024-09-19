package com.android_a865.gebril_app.feature_main.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.common.adapters.InvoiceItemsAdapter
import com.android_a865.gebril_app.common.adapters.InvoicesAdapter
import com.android_a865.gebril_app.data.domain.InvoiceHolder
import com.android_a865.gebril_app.databinding.FragmentConfirmBinding
import com.android_a865.gebril_app.databinding.FragmentHistoryBinding
import com.android_a865.gebril_app.databinding.FragmentItemsChooseBinding
import com.android_a865.gebril_app.feature_main.confirmation.ConfirmationFragmentViewModel
import com.android_a865.gebril_app.feature_main.main_page.MainFragmentViewModel
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import dagger.hilt.EntryPoint
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history) ,
    InvoicesAdapter.OnItemEventListener {

    private val viewModel by viewModels<HistoryFragmentViewModel>()
    private val invoicesAdapter = InvoicesAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()

        val binding = FragmentHistoryBinding.bind(view)
        binding.apply {

            invoicesList.apply {
                adapter = invoicesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }


        }

        viewModel.invoices.asLiveData().observe(viewLifecycleOwner) {
            invoicesAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.windowEvents.collect { event ->
                when (event) {
                    is HistoryFragmentViewModel.WindowEvents.Navigate -> {
                        findNavController().navigate(event.direction)
                        true
                    }

                    is HistoryFragmentViewModel.WindowEvents.LoadingDone -> {
                        if (event.message != null) {
                            Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG)
                                .show()
                        }
                        true
                    }
                }.exhaustive

            }
        }

    }

    override fun onItemClicked(invoice: InvoiceHolder) {
        viewModel.onEditInvoiceClicked(invoice)
    }

}