package com.android_a865.gebril_app.feature_client.presentation.clients_view

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.common.adapters.ClientsAdapter
import com.android_a865.gebril_app.databinding.FragmentClientsBinding
import com.android_a865.gebril_app.feature_client.domain.model.Client
import com.android_a865.gebril_app.feature_main.presentation.main_page.MainFragmentViewModel
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.onTextChanged
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class ClientsFragment : Fragment(R.layout.fragment_clients),
    ClientsAdapter.OnItemEventListener {

    private val viewModel by viewModels<ClientsViewModel>()
    private val clientsAdapter = ClientsAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()
        val binding = FragmentClientsBinding.bind(view)

        binding.apply {

            clientsList.apply {
                adapter = clientsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            fab.setOnClickListener {
                viewModel.onFabClicked()
            }

            viewModel.clients.asLiveData().observe(viewLifecycleOwner) {
                clientsAdapter.submitList(it)
                tvEmpty.isVisible = it.isEmpty()
            }
        }



        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.windowEvents.collect { event ->
                when (event) {
                    is ClientsViewModel.WindowEvents.Navigate -> {
                        findNavController().navigate(event.direction)
                    }
                }.exhaustive

            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.clients_view_options, menu)

        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.onTextChanged {
            viewModel.searchQuery.value = it
        }
    }


    override fun onItemClicked(client: Client) {
        viewModel.onItemClicked(client)
    }
}