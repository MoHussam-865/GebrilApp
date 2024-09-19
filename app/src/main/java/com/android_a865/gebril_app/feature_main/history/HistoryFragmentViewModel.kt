package com.android_a865.gebril_app.feature_main.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.data.domain.InvoiceHolder
import com.android_a865.gebril_app.data.domain.InvoiceRepository
import com.android_a865.gebril_app.data.domain.ItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HistoryFragmentViewModel @Inject constructor(
    private val itemsRepository: ItemsRepository,
    invoicesRepository: InvoiceRepository
) : ViewModel() {

    val invoices = invoicesRepository.getInvoices()

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()

    fun onEditInvoiceClicked(invoice: InvoiceHolder) = viewModelScope.launch {

        val items = itemsRepository.getItemsById(invoice.items)

        eventsChannel.send(
            WindowEvents.Navigate(
                HistoryFragmentDirections.actionHistoryFragmentToItemsChooseFragment(
                    items = items.toTypedArray(),
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