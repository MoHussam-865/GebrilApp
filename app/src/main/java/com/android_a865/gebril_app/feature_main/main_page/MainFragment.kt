package com.android_a865.gebril_app.feature_main.main_page

import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.Manifest
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.common.adapters.ChooseInvoiceItemsAdapter
import com.android_a865.gebril_app.common.adapters.InvoicesAdapter
import com.android_a865.gebril_app.common.adapters.PostViewAdapter
import com.android_a865.gebril_app.data.domain.InvoiceHolder
import com.android_a865.gebril_app.databinding.FragmentMainBinding
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModule by viewModels<MainFragmentViewModel>()
    private val itemsAdapter = PostViewAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()


        val binding = FragmentMainBinding.bind(view)
        binding.apply {

            posts.apply {
                adapter = itemsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            estimate.setOnClickListener {
                viewModule.onNewEstimateClicked()
            }

            history.setOnClickListener {
                viewModule.onHistoryClicked()
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
                    MainFragmentViewModel.WindowEvents.StartWithContext -> {
                        viewModule.startWithContext(requireContext())
                        true
                    }
                }.exhaustive

            }
        }
    }

}