package com.android_a865.gebril_app.feature_main.presentation.invoices_view

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.feature_in_app.domain.use_cases.SubscriptionUseCase
import com.android_a865.gebril_app.feature_main.domain.model.Invoice
import com.android_a865.gebril_app.feature_main.domain.use_cases.invoice_use_cases.InvoiceUseCases
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoicesViewViewModel @Inject constructor(
    invoiceUseCases: InvoiceUseCases,
    private val repository: SettingsRepository,
    private val subscriptionUseCase: SubscriptionUseCase
) : ViewModel() {

    var filterOptions: MutableStateFlow<FilterOptions> = MutableStateFlow(FilterOptions.All)

    val invoices = filterOptions.flatMapLatest {
        invoiceUseCases.getInvoices(it)
    }

    private var hasAccess = false

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            eventsChannel.send(
                WindowEvents.SetAppSettings(
                    repository.getAppSettings().first()
                )
            )

            hasAccess = subscriptionUseCase()
            Log.d("Subscription", hasAccess.toString())
        }
    }


    fun onEditInvoiceClicked(invoice: Invoice) = viewModelScope.launch {
        eventsChannel.send(WindowEvents.NavigateTo(
            InvoicesViewFragmentDirections.actionInvoicesViewFragmentToNewEstimateFragment(
                invoice = invoice
            )
        ))
    }

    fun onNewInvoiceClicked() = viewModelScope.launch {
            eventsChannel.send(WindowEvents.NavigateTo(
                InvoicesViewFragmentDirections.actionInvoicesViewFragmentToNewEstimateFragment(
                    invoice = null
                )
            ))
    }

    sealed class WindowEvents {
        data class NavigateTo(val direction: NavDirections) : WindowEvents()
        data class SetAppSettings(val appSettings: AppSettings) : WindowEvents()
    }

}