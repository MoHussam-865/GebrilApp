package com.android_a865.gebril_app.features.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_a865.gebril_app.data.domain.CartRepo
import com.android_a865.gebril_app.data.domain.InvoiceRepository
import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.data.mapper.toEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HistoryFragmentViewModel @Inject constructor(
    private val invoicesRepository: InvoiceRepository,
    private val cartRepo: CartRepo
) : ViewModel() {

    val invoices = invoicesRepository.getInvoices()

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()

    fun onEditInvoiceClicked(invoice: Invoice) = viewModelScope.launch {
        invoice.items.forEach {
            cartRepo.addToCart(it)
        }
        invoicesRepository.deleteInvoices(listOf(invoice.toEntity()))
        eventsChannel.send(WindowEvents.Navigate)
    }

    sealed class WindowEvents {
        data class LoadingDone(val message: String?) : WindowEvents()
        data object Navigate: WindowEvents()
    }
}