package com.android_a865.gebril_app.feature_main.main_page

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.common.adapters.InvoicesAdapter
import com.android_a865.gebril_app.data.domain.Invoice
import com.android_a865.gebril_app.databinding.FragmentMainBinding
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main),
    InvoicesAdapter.OnItemEventListener{

    private val viewModule by viewModels<MainFragmentViewModel>()
    private val invoicesAdapter = InvoicesAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()


        val binding = FragmentMainBinding.bind(view)

        binding.apply {

            estimate.setOnClickListener {
                viewModule.onNewEstimateClicked()
            }

            invoicesList.apply {
                adapter = invoicesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewModule.invoices.asLiveData().observe(viewLifecycleOwner) {
                invoicesAdapter.submitList(it)
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModule.windowEvents.collect { event ->
                when (event) {
                    is MainFragmentViewModel.WindowEvents.Navigate -> {
                        findNavController().navigate(event.direction)
                        true
                    }
                    is MainFragmentViewModel.WindowEvents.LoadingDone -> {
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

    override fun onItemClicked(invoice: Invoice) {
        viewModule.onEditInvoiceClicked(invoice)
    }
}