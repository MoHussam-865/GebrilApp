package com.android_a865.gebril_app.feature_main.pdf_preview

import android.content.Context
import android.icu.util.Calendar
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.common.PdfMaker
import com.android_a865.gebril_app.data.domain.CartRepo
import com.android_a865.gebril_app.data.domain.InvoiceRepository
import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.data.mapper.toEntity
import com.android_a865.gebril_app.external_api.ItemsApi
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
    private val cartRepo: CartRepo
) : ViewModel() {

    var fileName: String? = null
    var invoice: Invoice? = null

    private lateinit var appSettings: AppSettings

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            appSettings = settingsRepository.getAppSettings().first()
            val items  = cartRepo.getCart().first()
            invoice = Invoice(date = Calendar.getInstance().timeInMillis, items = items)
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
                serverApi.sendOrder(invoice = invoice)
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
            } else {
                showMessage()
            }
        }
    }



    fun onSaveClicked() = viewModelScope.launch {
        invoice?.let {
            invoiceRepository.insertInvoice(it.toEntity())
            cartRepo.clearCart()
        }
        eventsChannel.send(WindowEvents.Finish)
    }


    private suspend fun showMessage(msg: String? = null) {
        eventsChannel.send(
            WindowEvents.ShowMessage("Error Try again later")
        )
    }

    sealed class WindowEvents {
        data class OpenPdf(val fileName: String): WindowEvents()
        data object Finish: WindowEvents()
        data class ShowMessage(val msg: String): WindowEvents()
        data object SendContext : WindowEvents()
    }
}