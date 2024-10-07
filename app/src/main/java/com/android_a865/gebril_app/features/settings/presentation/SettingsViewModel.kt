package com.android_a865.gebril_app.feature_settings.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: SettingsRepo,
): ViewModel() {

    private val appSettingsFlow = repository.getAppSettings()
    private var dateFormat: String? = null
    private var currency: String? = null

    init {
        viewModelScope.launch {
            appSettingsFlow.collect {
                dateFormat = it.dateFormat
                currency = it.currency
            }
        }
    }


    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()


    fun onCompanyInfoSelected() = viewModelScope.launch {
//        eventsChannel.send(
//            WindowEvents.Navigate(
//                SettingsFragmentDirections.actionSettingsFragmentToCompanyInfoFragment()
//            )
//        )
    }

//    fun onDateFormatSelected(context: Context) {
//
//        val time = System.currentTimeMillis()
//        val array = DATE_FORMATS.map { time.date(it) }.toTypedArray()
//
//        AlertDialog.Builder(context)
//            .setTitle(context.resources.getString(R.string.choose_format))
//            .setSingleChoiceItems(
//                array,
//                DATE_FORMATS.indexOf(dateFormat)
//            ) { dialog, i ->
//
//                viewModelScope.launch {
//                    repository.updateDateFormat(DATE_FORMATS[i])
//                }
//
//                dialog.dismiss()
//            }.show()
//
//    }
//
//    fun onCurrencySelected(context: Context) {
//        val input = EditText(context)
//        input.setText(currency)
//
//        val builder = android.app.AlertDialog.Builder(context)
//        builder.setTitle(context.resources.getString(R.string.choose_currency))
//            .setView(input)
//            .setPositiveButton(context.resources.getString(R.string.ok)) { dialogInterface, _ ->
//                val currency = input.text.toString()
//
//                viewModelScope.launch {
//                    repository.updateCurrency(currency)
//                }
//                dialogInterface.dismiss()
//            }
//            .setNegativeButton(context.resources.getString(R.string.cancel)) { dialogInterface, _ ->
//                dialogInterface.dismiss()
//            }.show()
//
//        input.requestFocus()
//
//
//    }
//
//    fun onExportSelected() = viewModelScope.launch {
//        // TODO limit Export feature
//        /** uncomment */
//        if (isSubscribed || NO_AD) {
//            eventsChannel.send(
//                WindowEvents.Export(
//                    importExportUseCases.export()
//                )
//            )
//        } else acquireSubscription()
//
//        /** comment */
//        /*eventsChannel.send(
//            WindowEvents.Export(importExportUseCases.export())
//        )*/
//    }
//
//    fun onImportSelected() = viewModelScope.launch {
//        // TODO limit Import feature
//        /** uncomment */
//        if (isSubscribed || NO_AD) {
//            eventsChannel.send(WindowEvents.Import)
//        } else acquireSubscription()
//
//        /** comment */
//        //eventsChannel.send(WindowEvents.Import)
//    }
//
//    fun saveData(finalData: String) = viewModelScope.launch {
//        eventsChannel.send(
//            WindowEvents.ImportState(
//                importExportUseCases.import(finalData)
//            )
//        )
//    }
//

    sealed class WindowEvents {
        data class Navigate(val direction: NavDirections): WindowEvents()
        data class Export(val data: String): WindowEvents()
        object Import : WindowEvents()
        data class ImportState(val msg: String): WindowEvents()
    }

}