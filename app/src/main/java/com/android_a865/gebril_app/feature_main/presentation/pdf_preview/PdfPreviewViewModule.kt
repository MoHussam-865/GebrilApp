package com.android_a865.gebril_app.feature_main.presentation.pdf_preview

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_a865.gebril_app.common.PdfMaker
import com.android_a865.gebril_app.feature_main.domain.model.Invoice
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfPreviewViewModule @Inject constructor(
    state: SavedStateHandle,
    private val repository: SettingsRepository
) : ViewModel() {

    val invoice = state.get<Invoice>("invoice")
    var fileName: String? = null

    private lateinit var appSettings: AppSettings

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            appSettings = repository.getAppSettings().first()
            eventsChannel.send(WindowEvents.SendContext)
        }
    }

    fun onStart(context: Context) = context.apply {
        invoice?.let { invoice0 ->
            fileName = PdfMaker().make(this, invoice0, appSettings)

            fileName?.let {
                viewModelScope.launch {
                    eventsChannel.send(WindowEvents.OpenPdf(it))
                }
            }
        }
    }


    fun onSendPdfClicked() = viewModelScope.launch {
        fileName?.let {
            eventsChannel.send(WindowEvents.SendPdf(it))
        }
    }

    fun onOpenPdfClicked() = viewModelScope.launch {
        fileName?.let {
            eventsChannel.send(WindowEvents.OpenPdfExternally(it))
        }
    }


    sealed class WindowEvents {
        data class OpenPdfExternally(val fileName: String) : WindowEvents()
        data class SendPdf(val fileName: String) : WindowEvents()
        data class OpenPdf(val fileName: String): WindowEvents()
        object SendContext : WindowEvents()
    }
}