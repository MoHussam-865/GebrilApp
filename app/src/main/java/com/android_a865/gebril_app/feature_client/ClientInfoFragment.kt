package com.android_a865.gebril_app.feature_client

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android_a865.gebril_app.R
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

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.windowEvents.collect { event ->
                when (event) {
                    is ClientInfoViewModel.WindowEvents.NavigateBack -> {
                        findNavController().popBackStack()
                        true
                    }
                    is ClientInfoViewModel.WindowEvents.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_SHORT).show()
                        true
                    }
                    is ClientInfoViewModel.WindowEvents.Navigate -> {
                        findNavController().navigate(event.direction)
                        true
                    }
                }.exhaustive
            }
        }

    }


}