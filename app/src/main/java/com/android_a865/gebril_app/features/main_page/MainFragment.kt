package com.android_a865.gebril_app.features.main_page

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.common.adapters.PostViewAdapter
import com.android_a865.gebril_app.databinding.FragmentMainBinding
import com.android_a865.gebril_app.utils.date
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {

    private val viewModule by viewModels<MainFragmentViewModel>()
    private val postViewAdapter = PostViewAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setUpActionBarWithNavController()

        val binding = FragmentMainBinding.bind(view)
        binding.apply {


            posts.apply {
                adapter = postViewAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            viewModule.posts.asLiveData().observe(viewLifecycleOwner) {
                postViewAdapter.submitList(it)
            }

            viewModule.appSettings.asLiveData().observe(viewLifecycleOwner) {
                val msg = "last update at ${it.lastUpdateDate.date()}"
                error.text = msg
            }
        }


        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModule.windowEvents.collect { event ->
                when (event) {

                    is MainFragmentViewModel.WindowEvents.LoadingDone -> {
                        if (event.message != null) {
                            Toast.makeText(requireContext(), event.message, Toast.LENGTH_LONG)
                                .show()
                        }
                        true
                    }
                }.exhaustive

            }
        }
    }

}