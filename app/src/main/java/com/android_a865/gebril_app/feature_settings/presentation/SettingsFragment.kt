package com.android_a865.gebril_app.feature_settings.presentation

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.databinding.FragmentSettingsBinding
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()
        val binding = FragmentSettingsBinding.bind(view)

        binding.apply {
            tvDateFormat.setOnClickListener {
                //viewModel.onDateFormatSelected(requireContext())
            }

            tvCurrency.setOnClickListener {
//                viewModel.onCurrencySelected(requireContext())
            }

            tvCompanyInfo.setOnClickListener {
                viewModel.onCompanyInfoSelected()
            }

            tvExport.setOnClickListener {
                //viewModel.onExportSelected()
            }

            tvImport.setOnClickListener {
                //viewModel.onImportSelected()

            }
        }

    }

}