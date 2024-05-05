package com.android_a865.gebril_app.feature_settings.presentation.settings

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.databinding.FragmentSettingsBinding
import com.android_a865.gebril_app.utils.AUTHORITY
import com.android_a865.gebril_app.utils.exhaustive
import com.android_a865.gebril_app.utils.setUpActionBarWithNavController
import com.android_a865.gebril_app.utils.showMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.io.*


const val REQUEST_CODE = 100

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpActionBarWithNavController()
        val binding = FragmentSettingsBinding.bind(view)

        binding.apply {
            tvDateFormat.setOnClickListener {
                viewModel.onDateFormatSelected(requireContext())
            }

            tvCurrency.setOnClickListener {
                viewModel.onCurrencySelected(requireContext())
            }

            tvCompanyInfo.setOnClickListener {
                viewModel.onCompanyInfoSelected()
            }

            tvExport.setOnClickListener {
                viewModel.onExportSelected()
            }

            tvImport.setOnClickListener {
                viewModel.onImportSelected()

            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.windowEvents.collect { event ->
                when (event) {
                    is SettingsViewModel.WindowEvents.Navigate -> {
                        findNavController().navigate(event.direction)
                    }
                    is SettingsViewModel.WindowEvents.Export -> {
                        export(event.data)
                    }
                    SettingsViewModel.WindowEvents.Import -> {
                        import()
                    }
                    is SettingsViewModel.WindowEvents.ImportState -> {
                        showMessage(event.msg)
                        val x = findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }

    }

    // import & export
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


    private fun export(data: String) {

        try {
            context?.let {
                val fileName = "${System.currentTimeMillis()}.txt"

                val file = File(it.filesDir, fileName)

                val fileWriter = OutputStreamWriter(FileOutputStream(file))

                fileWriter.write(data)
                fileWriter.close()


                val uri = FileProvider.getUriForFile(it, AUTHORITY, file)

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, file.name)
                intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                startActivity(intent)



                Log.d("ExportingError", "Working")
            }

        } catch (e: Exception) {
            showMessage("Error")
            Log.d("ExportingError", e.message.toString())
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

        AlertDialog.Builder(context)
            .setTitle(context.resources.getString(R.string.warning))
            .setMessage(context.resources.getString(R.string.importing_dialog))
            .setPositiveButton(context.resources.getString(R.string.proceed)) { dialog, _ ->


                try {

                    val inputStream = context.contentResolver?.openInputStream(uri)

                    inputStream?.let {
                        val isr = InputStreamReader(it)
                        val reader = BufferedReader(isr)
                        val data = reader.readText()
                        reader.close()

                        viewModel.saveData(data)
                        Log.d("Import Error", data)
                    }

                }
                catch (e: FileNotFoundException) {
                    showMessage("File Not Found")
                }
                catch (e: Exception) {
                    Log.d("ImportingError", e.message.toString())
                }


                dialog.dismiss()
            }
            .setNegativeButton(context.resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

    }

}