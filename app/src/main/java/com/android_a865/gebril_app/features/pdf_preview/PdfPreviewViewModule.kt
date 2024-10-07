package com.android_a865.gebril_app.features.pdf_preview

import android.content.Context
import android.icu.util.Calendar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.common.PdfMaker
import com.android_a865.gebril_app.data.domain.repo.CartRepo
import com.android_a865.gebril_app.data.domain.repo.InvoiceRepo
import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.data.mapper.toEntity
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PdfPreviewViewModule @Inject constructor(
    private val invoiceRepository: InvoiceRepo,
    private val settingsRepo: SettingsRepo,
    private val cartRepo: CartRepo
) : ViewModel() {

    var fileName: String? = null
    var invoice: Invoice? = null

    private lateinit var appSettings: AppSettings

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            appSettings = settingsRepo.getAppSettings().first()
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
        eventsChannel.send(
            WindowEvents.Navigate(
                ViewPdfFragmentDirections.actionViewPdfFragmentToClientInfoFragment(
                    invoice = invoice
                )
            )
        )
    }



    fun onSaveClicked() = viewModelScope.launch {
        invoice?.let {
            invoiceRepository.insertInvoice(it.toEntity())
            cartRepo.clearCart()
        }
        eventsChannel.send(WindowEvents.Finish)
    }

    sealed class WindowEvents {
        data class OpenPdf(val fileName: String): WindowEvents()
        data object Finish: WindowEvents()
        data class ShowMessage(val msg: String): WindowEvents()
        data object SendContext : WindowEvents()
        data class Navigate(val dir: NavDirections): WindowEvents()
    }
}