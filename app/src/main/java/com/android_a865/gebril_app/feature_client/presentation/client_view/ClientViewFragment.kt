package com.android_a865.gebril_app.feature_client.presentation.client_view

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.databinding.FragmentClientViewBinding
import com.android_a865.gebril_app.utils.appCompatActivity
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ClientViewFragment : Fragment(R.layout.fragment_client_view) {

    private val viewModel by viewModels<ClientViewModel>()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()
        val binding = FragmentClientViewBinding.bind(view)

        binding.apply {
            viewModel.client?.let { thisClient ->
                tvClientDetails.text = if (thisClient.org.isNullOrBlank()) {
                    thisClient.name
                } else {
                    "${thisClient.name} . ${thisClient.org}"
                }
                tvClientTitle.text = thisClient.title
                tvPhone1.text = thisClient.phone1
                tvPhone2.text = thisClient.phone2
                tvEmail.text = thisClient.email
                tvAddress.text = thisClient.address

                tvAddress.isVisible = !thisClient.address.isNullOrBlank()
                llEmail.isVisible = !thisClient.email.isNullOrBlank()
                llPhone1.isVisible = !thisClient.phone1.isNullOrBlank()
                llPhone2.isVisible = !thisClient.phone2.isNullOrBlank()

            }

            btnCall1.setOnClickListener {
                viewModel.onCall1Clicked()
            }

            btnCall2.setOnClickListener {
                viewModel.onCall2Clicked()
            }

            btnSendEmail.setOnClickListener {
                viewModel.onSendEmailClicked()
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.windowEvents.collect { event ->
                when (event) {
                    is ClientViewModel.WindowEvents.MakePhoneCall -> {
                        call(event.phone)
                        true
                    }
                    is ClientViewModel.WindowEvents.SendEmail -> {
                        mail(event.email)
                        true
                    }
                    ClientViewModel.WindowEvents.NavigateBack -> {
                        findNavController().popBackStack()
                        true
                    }
                }.exhaustive
            }
        }

        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.client_view_options, menu)

        viewModel.isDatabaseClient.asLiveData().observe(viewLifecycleOwner){
            menu.findItem(R.id.edit).isVisible = it
            menu.findItem(R.id.delete).isVisible = it
            menu.findItem(R.id.add).isVisible = !it
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.edit, R.id.add -> {
                findNavController().navigate(
                    ClientViewFragmentDirections.actionClientViewFragmentToAddEditClientFragment(
                        client = viewModel.client
                    )
                )
                true
            }

            R.id.delete -> {
                viewModel.onDeleteClientSelected(requireContext())
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun call(phoneNumber: String?) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
        startActivity(intent)
    }

    private fun mail(email: String?) {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email"))
        startActivity(intent)
    }




}