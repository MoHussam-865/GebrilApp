package com.android_a865.gebril_app.feature_main.presentation.selecting

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.common.adapters.ItemsAdapter
import com.android_a865.gebril_app.common.adapters.PathIndicatorAdapter
import com.android_a865.gebril_app.databinding.FragmentSelectingBinding
import com.android_a865.gebril_app.feature_main.domain.model.Item
import com.android_a865.gebril_app.utils.appCompatActivity
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SelectingFragment : Fragment(R.layout.fragment_selecting),
ItemsAdapter.OnItemEventListener {

    private val viewModule by viewModels<ItemsSelectViewModel>()
    private val itemsAdapter = ItemsAdapter(this,true)
    private val pathIndicator = PathIndicatorAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()
        val binding = FragmentSelectingBinding.bind(view)

        binding.apply {
            itemsList.apply {
                adapter = itemsAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            pathList.apply {
                adapter = pathIndicator
                setHasFixedSize(true)
            }
        }

        pathIndicator.submitPath(viewModule.currentPath)


        viewModule.itemsData.observe(viewLifecycleOwner) {
            itemsAdapter.submitList(it)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModule.itemsWindowEvents.collect { event ->
                when (event) {
                    is ItemsSelectViewModel.ItemsWindowEvents.Navigate -> {
                        findNavController().navigate(event.directions)
                        true
                    }
                    is ItemsSelectViewModel.ItemsWindowEvents.NotifyAdapter -> {
                        itemsAdapter.notifyItemChanged(event.position)
                        true
                    }
                    ItemsSelectViewModel.ItemsWindowEvents.NavigateBack -> {
                        findNavController().popBackStack()
                        true
                    }
                }.exhaustive
            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.select_items_options, menu)

        viewModule.numSelected.asLiveData().observe(viewLifecycleOwner){
            appCompatActivity.supportActionBar?.title = it.toString()
            menu.findItem(R.id.edit).isVisible = it == 1
            menu.findItem(R.id.delete).isVisible = it != 0
            menu.findItem(R.id.copy).isVisible = it == 1
            menu.findItem(R.id.selectAll).isVisible = it < viewModule.all
            menu.findItem(R.id.deselectAll).isVisible = it == viewModule.all
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.edit -> {
                viewModule.onEditOptionSelected()
                true
            }

            R.id.delete -> {
                viewModule.onDeleteOptionSelected(requireContext())
                true
            }

            R.id.selectAll -> {
                viewModule.onSelectAllChanged(true)
                true
            }

            R.id.deselectAll -> {
                viewModule.onSelectAllChanged(false)
                true
            }

            R.id.copy -> {
                viewModule.onCopyItemSelected()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onItemClicked(item: Item, position: Int) =
        viewModule.onItemClicked(item, position)
    override fun onSelectionChange(item: Item, position: Int, b:Boolean) =
        viewModule.onSelectChanged(item, position, b)
    override fun onItemLongClick(item: Item) { }

}