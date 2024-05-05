package com.android_a865.gebril_app.feature_in_app.presentation.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.databinding.FragmentSubscribeBinding
import com.android_a865.gebril_app.utils.MONTHLY_SUBSCRIPTION
import com.android_a865.gebril_app.utils.YEARLY_SUBSCRIPTION
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SubscribeFragment : Fragment(R.layout.fragment_subscribe) {

    private val viewModel by activityViewModels<SharedViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()
        val binding = FragmentSubscribeBinding.bind(view)

        binding.apply {
            btnMonthlySubscription.setOnClickListener {
                viewModel.subscribe(MONTHLY_SUBSCRIPTION)
            }

            btnYearlySubscription.setOnClickListener {
                viewModel.subscribe(YEARLY_SUBSCRIPTION)
            }


            viewModel.isSubscribed.observe(viewLifecycleOwner) {
                llCancel.isVisible = it
                llMonthly.isVisible = !it
                llYearly.isVisible = !it

                viewModel.getProductsData()
            }

            viewModel.products.asLiveData().observe(viewLifecycleOwner) {
                tvMonthlyPrice.text = it.monthly?.price
                tvYearlyPrice.text = it.yearly?.price
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.windowEvents.collect { event ->
                when (event) {
                    is SharedViewModel.WindowEvents.LaunchBillingFlow -> {
                        viewModel.launchBillingFlow(requireActivity(), event.flowParams)
                        true
                    }
                    is SharedViewModel.WindowEvents.ShowMessage -> {
                        Toast.makeText(requireContext(), event.msg, Toast.LENGTH_SHORT).show()
                        true
                    }
                }.exhaustive
            }
        }

    }
}