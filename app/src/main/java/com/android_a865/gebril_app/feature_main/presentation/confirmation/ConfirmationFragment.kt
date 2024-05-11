package com.android_a865.gebril_app.feature_main.presentation.confirmation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.common.adapters.InvoiceItemsAdapter
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import gebril_app.R
import gebril_app.databinding.FragmentConfirmBinding
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class ConfirmationFragment : Fragment(R.layout.fragment_confirm),
    InvoiceItemsAdapter.OnItemEventListener {

    private val viewModel by viewModels<ConfirmationFragmentViewModel>()
    private val itemsAdapter = InvoiceItemsAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()

        val binding = FragmentConfirmBinding.bind(view)
        binding.apply {

            listItem.apply {
                adapter = itemsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(false)
            }

            next.setOnClickListener {
                viewModel.onNextPressed()
            }

            previous.setOnClickListener {
                viewModel.onPreviousPressed()
            }


            viewModel.itemsFlow.asLiveData().observe(viewLifecycleOwner) {
                itemsAdapter.submitList(it)
            }
        }

//        val observed = "choose_invoice_items"
//        findNavController().currentBackStackEntry?.savedStateHandle?.apply {
//            viewModel.onItemsSelected(get(observed))
//            set(observed, null)
//        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.invoiceWindowEvents.collect { event ->
                    when (event) {
                        is ConfirmationFragmentViewModel.WindowEvents.NavigateBack -> {
                            findNavController().popBackStack()
                        }
                        is ConfirmationFragmentViewModel.WindowEvents.ShowMessage -> {
                            Snackbar.make(requireView(), event.message, Snackbar.LENGTH_LONG).show()
                        }
                        is ConfirmationFragmentViewModel.WindowEvents.Navigate -> {
                            findNavController().navigate(event.direction)
                        }
                    }
            }
        }

    }




//        setFragmentResultListener("chosen_client") { _, bundle ->
//            viewModel.onClientChosen(
//                bundle.getParcelable("client")
//            )
//        }
//
//        setHasOptionsMenu(true)


//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.estimate_options, menu)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return when (item.itemId) {
//            R.id.open_pdf -> {
//                viewModel.onOpenPdfClicked()
//                true
//            }
//
//            R.id.save_invoice -> {
//                viewModel.onSaveClicked()
//                true
//            }
//
//            else -> super.onOptionsItemSelected(item)
//        }
//    }


    override fun onItemRemoveClicked(item: InvoiceItem) = viewModel.onItemRemoveClicked(item)

    override fun onPlusClicked(item: InvoiceItem) = viewModel.onOneItemAdded(item)

    override fun onMinusClicked(item: InvoiceItem) = viewModel.onOneItemRemoved(item)

    override fun onQtyChanged(item: InvoiceItem, text: String) =
        viewModel.onItemQtyChanged(item, text)

}