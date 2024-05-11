package com.android_a865.gebril_app.feature_main.presentation.main_page

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import gebril_app.R
import gebril_app.databinding.FragmentMainBinding
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModule by viewModels<MainFragmentViewModel>()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()


        val binding = FragmentMainBinding.bind(view)

        binding.apply {

            savings.setOnClickListener {
                viewModule.onSavingsCalled()
            }

            estimate.setOnClickListener {
                viewModule.onNewEstimateClicked()
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModule.windowEvents.collect { event ->
                when (event) {
                    is MainFragmentViewModel.WindowEvents.Navigate -> {
                        findNavController().navigate(event.direction)
                        true
                    }
                    MainFragmentViewModel.WindowEvents.LoadingDone -> {
                        binding.progress.isVisible = false
                        binding.navbtn.isVisible = true
                        true
                    }
                    is MainFragmentViewModel.WindowEvents.Message -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG).show()
                        true
                    }
                }.exhaustive

            }
        }
    }
}