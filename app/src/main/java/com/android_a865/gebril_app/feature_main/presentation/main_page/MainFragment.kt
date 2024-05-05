package com.android_a865.gebril_app.feature_main.presentation.main_page

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.common.adapters.ItemsAdapter
import com.android_a865.gebril_app.common.adapters.PathIndicatorAdapter
import com.android_a865.gebril_app.databinding.FragmentMainBinding
import com.android_a865.gebril_app.feature_main.domain.model.Item
import com.android_a865.gebril_app.feature_settings.presentation.settings.REQUEST_CODE
import com.android_a865.gebril_app.utils.*
import com.google.android.material.badge.ExperimentalBadgeUtils
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.io.*

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main), ItemsAdapter.OnItemEventListener {

    private val viewModule by viewModels<MainFragmentViewModel>()

    private val itemsAdapter = ItemsAdapter(this)
    private val pathIndicator = PathIndicatorAdapter()
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()


        val binding = FragmentMainBinding.bind(view)

        binding.apply {

            toggle = ActionBarDrawerToggle(
                requireActivity(),
                drawerLayout,
                mainToolBar,
                R.string.open,
                R.string.close
            )
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            navView.setNavigationItemSelectedListener {
                drawerLayout.close()
                when (it.itemId) {
                    R.id.myEstimate -> {
                        viewModule.onMyEstimateSelected()
                    }

                    R.id.myClients -> {
                        viewModule.onMyClientsSelected()
                    }

                    R.id.settings -> {
                        viewModule.onSettingsSelected()
                    }

                    /** uncomment for in app ads*/
                    R.id.subscribe -> {
                        viewModule.onSubscribeSelected()
                    }

                    R.id.reports -> {
                        viewModule.onReportsSelected()
                    }
                }
                true
            }


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

        viewModule.itemsData.observe(viewLifecycleOwner) {
            itemsAdapter.submitList(it)
            binding.tvEmpty.isVisible = it.isEmpty()
        }

        viewModule.currentPath.asLiveData().observe(viewLifecycleOwner) {
            pathIndicator.submitPath(it)
            binding.pathList.scrollToEnd()
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            viewModule.onBackPressed()
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModule.windowEvents.collect { event ->
                when (event) {
                    is MainFragmentViewModel.WindowEvents.CloseTheApp -> {
                        callback.remove()
                        requireActivity().onBackPressed()
                        true
                    }
                    is MainFragmentViewModel.WindowEvents.Navigate -> {
                        findNavController().navigate(event.direction)
                        true
                    }
                    MainFragmentViewModel.WindowEvents.ImportData -> {
                        import()
                        true
                    }
                    is MainFragmentViewModel.WindowEvents.ShowMsg -> {
                        Snackbar.make(requireView(), event.msg, Snackbar.LENGTH_LONG).show()
                        true
                    }
                }.exhaustive

            }
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_page_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return when (item.itemId) {
            R.id.newItem -> {
                viewModule.onNewItemSelected()
                true
            }

            R.id.newFolder -> {
                viewModule.onNewFolderSelected()
                true
            }

            R.id.newEstimate -> {
                viewModule.onNewEstimateSelected()
                true
            }

            R.id.import_here -> {
                viewModule.onImportItemsSelected()
                true
            }


            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClicked(item: Item, position: Int) =
        viewModule.onItemClicked(item)

    override fun onItemLongClick(item: Item) {
        findNavController().navigate(
            MainFragmentDirections.actionMainFragment2ToSelectingFragment(
                path = viewModule.currentPath.value,
                id = item.id
            )
        )
        //itemsViewModule.onItemLongClick(item)
    }

    override fun onSelectionChange(item: Item, position: Int, b: Boolean) {}





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    // the file that has the data
                    data.data?.let { saveData(requireContext(), it) }
                } else showMessage("Empty")
            } else {
                showMessage("Canceled")
            }
        }
    }

    private fun import() {
        // Opens the storage
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "text/plain" // text file

        startActivityForResult(
            Intent.createChooser(intent, "Choose data file"),
            REQUEST_CODE
        )

    }

    private fun saveData(context: Context, uri: Uri) {


        try {

            val inputStream = context.contentResolver?.openInputStream(uri)

            inputStream?.let {
                val isr = InputStreamReader(it)
                val reader = BufferedReader(isr)
                val data = reader.readText()
                reader.close()

                viewModule.saveData(data)
                Log.d("Import Error", data)
            }

        } catch (e: FileNotFoundException) {
            Snackbar.make(requireView(), "File Not Found", Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            Log.d("ImportingError", e.message.toString())
        }

    }

}