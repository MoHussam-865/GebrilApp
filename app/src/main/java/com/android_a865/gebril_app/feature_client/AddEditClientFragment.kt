package com.android_a865.gebril_app.feature_client//package com.android_a865.gebril_app.feature_client.presentation
//
//import android.os.Bundle
//import android.view.View
//import androidx.core.widget.addTextChangedListener
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import androidx.navigation.fragment.findNavController
//import com.android_a865.gebril_app.utils.exhaustive
//import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
//import com.google.android.material.snackbar.Snackbar
//import dagger.hilt.android.AndroidEntryPoint
//import gebril_app.R
//import gebril_app.databinding.FragmentAddEditClientBinding
//import kotlinx.coroutines.flow.collect
//
//@AndroidEntryPoint
//class AddEditClientFragment : Fragment(R.layout.fragment_add_edit_client) {
//
//    private val viewModel by viewModels<AddEditClientViewModel>()
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        setUpActionBarWithNavController()
//        val binding = FragmentAddEditClientBinding.bind(view)
//
//        binding.apply {
//            etClientName.editText?.setText(viewModel.clientName)
//            etOrganization.editText?.setText(viewModel.org)
//            etPhone1.editText?.setText(viewModel.phone1)
//
//            etClientName.editText?.addTextChangedListener {
//                viewModel.clientName = it.toString()
//            }
//            etOrganization.editText?.addTextChangedListener {
//                viewModel.org = it.toString()
//            }
//            etPhone1.editText?.addTextChangedListener {
//                viewModel.phone1 = it.toString()
//            }
//
//            fab.setOnClickListener {
//                viewModel.onSaveClicked()
//            }
//        }
//
//        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
//            viewModel.windowEvents.collect { event ->
//                when (event) {
//                    is AddEditClientViewModel.WindowEvents.NavigateBack -> {
//                        findNavController().popBackStack()
//                        true
//                    }
//                    is AddEditClientViewModel.WindowEvents.ShowInvalidInputMessage -> {
//                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
//                        true
//                    }
//                    is AddEditClientViewModel.WindowEvents.Navigate -> {
//                        findNavController().navigate(event.direction)
//                        true
//                    }
//                }.exhaustive
//            }
//        }
//
//    }
//}