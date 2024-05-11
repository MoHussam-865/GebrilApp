package com.android_a865.gebril_app.feature_main.presentation.invoices_view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.data.domain.Invoice
import com.android_a865.gebril_app.feature_main.domain.repository.InvoiceRepository
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoicesViewViewModel @Inject constructor(
    val repository: InvoiceRepository
) : ViewModel() {

    val invoices = repository.getInvoices()

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()


    fun onEditInvoiceClicked(invoice: Invoice) = viewModelScope.launch {
        // TODO  navigate to Invoice View
    }


    sealed class WindowEvents {
        data class NavigateTo(val direction: NavDirections) : WindowEvents()
        data class SetAppSettings(val appSettings: AppSettings) : WindowEvents()
    }
}