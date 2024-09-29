package com.android_a865.gebril_app.feature_main.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.data.domain.InvoiceRepository
import com.android_a865.gebril_app.data.domain.ItemsRepository
import com.android_a865.gebril_app.data.entities.Invoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HistoryFragmentViewModel @Inject constructor(
    invoicesRepository: InvoiceRepository
) : ViewModel() {

    val invoices = invoicesRepository.getInvoices()

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()

    fun onEditInvoiceClicked(invoice: Invoice) = viewModelScope.launch {

        eventsChannel.send(
            WindowEvents.Navigate(
                HistoryFragmentDirections.actionHistoryFragmentToItemsChooseFragment(
                    items = invoice.items.toTypedArray(),
                    invoiceId = invoice.id

                )
            )
        )
    }

    sealed class WindowEvents {
        data class LoadingDone(val message: String?) : WindowEvents()
        data class Navigate(val direction: NavDirections) : WindowEvents()
    }
}