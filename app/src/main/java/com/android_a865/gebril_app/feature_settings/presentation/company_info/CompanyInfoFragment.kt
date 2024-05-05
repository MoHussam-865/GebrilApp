package com.android_a865.gebril_app.feature_settings.presentation.company_info

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.databinding.FragmentCompanyInfoBinding
import com.android_a865.gebril_app.feature_main.presentation.main_page.MainFragmentViewModel
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class CompanyInfoFragment : Fragment(R.layout.fragment_company_info) {

    private val viewModel by viewModels<CompanyInfoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpActionBarWithNavController()
        val binding = FragmentCompanyInfoBinding.bind(view)

        binding.apply {

            viewModel.appSettings.asLiveData().observe(viewLifecycleOwner) {
                etCompanyName.editText?.setText(it.companyName)
                etPersonName.editText?.setText(it.personName)
                etPhone.editText?.setText(it.phone)
                etEmail.editText?.setText(it.email)
                etAddress.editText?.setText(it.address)
            }

            etCompanyName.editText?.addTextChangedListener {
                viewModel.companyName = it.toString()
            }
            etPersonName.editText?.addTextChangedListener {
                viewModel.personName = it.toString()
            }
            etPhone.editText?.addTextChangedListener {
                viewModel.phone = it.toString()
            }
            etEmail.editText?.addTextChangedListener {
                viewModel.email = it.toString()
            }
            etAddress.editText?.addTextChangedListener {
                viewModel.address = it.toString()
            }

            fab.setOnClickListener {
                viewModel.onSaveClicked()
            }


        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.windowEvents.collect { event ->
                when (event) {
                    CompanyInfoViewModel.WindowEvents.NavigateBack -> {
                        findNavController().popBackStack()
                    }
                }.exhaustive

            }
        }

    }
}