package com.android_a865.gebril_app.feature_main.history

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.common.adapters.InvoicesAdapter
import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.databinding.FragmentHistoryBinding
import com.android_a865.gebril_app.feature_main.ActivityFeature2
import com.android_a865.gebril_app.feature_main.MainActivity
import com.android_a865.gebril_app.utils.exhaustive
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history) ,
    InvoicesAdapter.OnItemEventListener {

    private val viewModel by viewModels<HistoryFragmentViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()
    private val invoicesAdapter = InvoicesAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setUpActionBarWithNavController()

        val binding = FragmentHistoryBinding.bind(view)
        binding.apply {

            (requireActivity() as AppCompatActivity).setSupportActionBar(mainToolBar)


            invoicesList.apply {
                adapter = invoicesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewModel.invoices.asLiveData().observe(viewLifecycleOwner) {
                invoicesAdapter.submitList(it)
                val isEmpty = it.isEmpty()
                empty.isVisible = isEmpty
                visible.isVisible = !isEmpty

            }

        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.windowEvents.collect { event ->
                when (event) {
                    is HistoryFragmentViewModel.WindowEvents.Navigate -> {
                        sharedViewModel.selectedItem.value = R.id.navigation_cart
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


    override fun onItemClicked(invoice: Invoice) {
        viewModel.onEditInvoiceClicked(invoice)
    }

}