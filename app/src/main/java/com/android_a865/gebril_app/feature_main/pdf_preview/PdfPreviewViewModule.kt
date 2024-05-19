package com.android_a865.gebril_app.feature_main.pdf_preview

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.common.PdfMaker
import com.android_a865.gebril_app.data.domain.Invoice
import com.android_a865.gebril_app.data.domain.InvoiceHolder
import com.android_a865.gebril_app.data.domain.Message
import com.android_a865.gebril_app.data.mapper.toEntity
import com.android_a865.gebril_app.external_api.ItemsApi
import com.android_a865.gebril_app.data.domain.InvoiceRepository
import com.android_a865.gebril_app.data.mapper.toInvoice
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class PdfPreviewViewModule @Inject constructor(
    state: SavedStateHandle,
    private val invoiceRepository: InvoiceRepository,
    private val settingsRepository: SettingsRepository,
    private val serverApi: ItemsApi,
) : ViewModel() {

    val invoice = state.get<Invoice>("invoice")
    var fileName: String? = null

    private lateinit var appSettings: AppSettings

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            appSettings = settingsRepository.getAppSettings().first()
            eventsChannel.send(WindowEvents.SendContext)
        }
    }

    fun onStart(context: Context) = context.apply {
        invoice?.let { myInvoice ->
            fileName = PdfMaker().make(this, myInvoice, appSettings)

            fileName?.let {
                viewModelScope.launch {
                    eventsChannel.send(WindowEvents.OpenPdf(it))
                }
            }
        }
    }


    fun onSendPdfClicked() = viewModelScope.launch {
        val response = try {
            invoice?.let { invoice ->
                val total = invoice.items.sumOf { it.total }
                val myInvoice = invoice.toEntity(total).toInvoice()
                serverApi.getItems(Message(invoice = myInvoice))
            }

        } catch (e: IOException) {
            showMessage()
            return@launch
        } catch (e: HttpException) {
            showMessage()
            return@launch
        }

        Log.d("my error", response.toString())
        response?.let {
            if (it.isSuccessful && it.body() != null) {
                onSaveClicked()
                finish()
            } else {
                showMessage()
            }
        }
    }

//    fun onOpenPdfClicked() = viewModelScope.launch {
//        fileName?.let {
//            eventsChannel.send(WindowEvents.OpenPdfExternally(it))
//        }
//    }

    fun onSaveClicked() = viewModelScope.launch {
        invoice?.let {
            val total = invoice.items.sumOf { it.total }
            invoiceRepository.insertInvoice(invoice.toEntity(total))
        }
        finish()
    }

    private suspend fun finish() {
        eventsChannel.send(
            WindowEvents.Finish(
                ViewPdfFragmentDirections.actionViewPdfFragmentToMainFragment3()
            )
        )
    }

    private suspend fun showMessage(msg: String? = null) {
        eventsChannel.send(
            WindowEvents.ShowMessage("Error Try again later")
        )
    }

    sealed class WindowEvents {
        //data class OpenPdfExternally(val fileName: String) : WindowEvents()
        //data class SendPdf(val fileName: String) : WindowEvents()
        data class OpenPdf(val fileName: String): WindowEvents()
        data class Finish(val direction: NavDirections): WindowEvents()
        data class ShowMessage(val msg: String): WindowEvents()
        object SendContext : WindowEvents()
    }
}