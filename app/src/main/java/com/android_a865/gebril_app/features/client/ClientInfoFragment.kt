package com.android_a865.gebril_app.features.client

import android.os.Bundle
import android.view.View
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.data.domain.Client
import com.android_a865.gebril_app.databinding.FragmentClientInfoBinding
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ClientInfoFragment : Fragment(R.layout.fragment_client_info) {

    private val viewModel by viewModels<ClientInfoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()
        val binding = FragmentClientInfoBinding.bind(view)

        binding.apply {
            fab.setOnClickListener {
                viewModel.onSendOrder()
            }

            etClientName.editText?.doOnTextChanged { text, _,_,_ ->
                viewModel.name = text.toString()
            }

            etCompanyName.editText?.doOnTextChanged { text, _,_,_ ->
                viewModel.companyName = text.toString()
            }

            etPhone.editText?.doOnTextChanged { text, _,_,_ ->
                viewModel.phone = text.toString()
            }

            etEmail.editText?.doOnTextChanged { text, _,_,_ ->
                viewModel.email = text.toString()
            }

            etAddress.editText?.doOnTextChanged { text, _,_,_ ->
                viewModel.address = text.toString()
            }

            viewModel.nameError.observe(viewLifecycleOwner) {
                etClientName.error = it
            }
            viewModel.phoneError.observe(viewLifecycleOwner) {
                etPhone.error = it
            }
            viewModel.addressError.observe(viewLifecycleOwner) {
                etAddress.error = it
            }

            viewModel.client.observe(viewLifecycleOwner) {

                it?.let { client: Client ->
                    etClientName.editText?.setText(client.name)
                    etCompanyName.editText?.setText(client.companyName)
                    etPhone.editText?.setText(client.phone)
                    etEmail.editText?.setText(client.email)
                    etAddress.editText?.setText(client.address)
                }

            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.windowEvents.collect { event ->
                when (event) {
                    is ClientInfoViewModel.WindowEvents.NavigateBack -> {
                        requireActivity().finish()
                        true
                    }
                    is ClientInfoViewModel.WindowEvents.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                        true
                    }
                }.exhaustive
            }
        }

    }


}