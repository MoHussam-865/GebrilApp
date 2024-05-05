package com.android_a865.gebril_app.feature_main.presentation.new_item

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.databinding.FragmentNewItemBinding
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class NewItemFragment : Fragment(R.layout.fragment_new_item) {

    private val viewModel by viewModels<NewItemViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()
        val binding = FragmentNewItemBinding.bind(view)

        binding.apply {
            itemNameEt.editText?.setText(viewModel.itemName)
            itemPriceEt.editText?.setText(viewModel.itemPrice)
            itemNameEt.editText?.addTextChangedListener { text ->
                viewModel.itemName = text.toString()
            }

            itemPriceEt.editText?.addTextChangedListener { text ->
                viewModel.itemPrice = text.toString()
            }

            fab.setOnClickListener {
                viewModel.onSaveClicked()
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditItemEvent.collect { event ->
                when (event) {
                    is NewItemViewModel.AddEditItemEvent.NavigateBackWithResult -> {
                        Toast.makeText(
                            requireContext(),
                            "Item Saved",
                            Toast.LENGTH_SHORT
                        ).show()
                        findNavController().popBackStack()
                        true
                    }
                    is NewItemViewModel.AddEditItemEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                        true
                    }
                    NewItemViewModel.AddEditItemEvent.NavigateBackWithResult2 -> {
                        findNavController().navigate(
                            NewItemFragmentDirections.actionNewItemFragmentToMainFragment2(
                                    path = viewModel.path
                            )
                        )
                        true
                    }
                }.exhaustive
            }
        }
    }
}