package com.android_a865.gebril_app.feature_main.confirmation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.utils.addOneOf
import com.android_a865.gebril_app.utils.removeAllOf
import com.android_a865.gebril_app.utils.removeOneOf
import com.android_a865.gebril_app.utils.setQtyTo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ConfirmationFragmentViewModel @Inject constructor(
    state: SavedStateHandle
) : ViewModel() {

    private val items = state.get<Array<InvoiceItem>>("items")
    private val invoiceId = state.get<Int>("invoiceId")

    // this used to determine weather it's being created or updating
    private val isCreated = state.get<Boolean>("is_created")

    val itemsFlow = MutableStateFlow(items?.toList() ?: listOf())


    private val eventsChannel = Channel<WindowEvents>()
    val invoiceWindowEvents = eventsChannel.receiveAsFlow()


    fun onItemRemoveClicked(item: InvoiceItem) {
        itemsFlow.value = itemsFlow.value.removeAllOf(item)
        if (itemsFlow.value.isEmpty()) {
            onPreviousPressed()
        }
    }

    fun onOneItemAdded(item: InvoiceItem) {
        itemsFlow.value = itemsFlow.value.addOneOf(item)
    }

    fun onOneItemRemoved(item: InvoiceItem) {
        itemsFlow.value = itemsFlow.value.removeOneOf(item)
    }

    fun onItemQtyChanged(item: InvoiceItem, qty: String) {
        try {

            val myQty = qty.toDouble()

            if (myQty < 0) {
                itemsFlow.value = itemsFlow.value.setQtyTo(item, 0.0)
                showInvalidMessage("Quantity can't be less than 0")
            } else {
                itemsFlow.value = itemsFlow.value.setQtyTo(item, myQty)
            }

        } catch (e: Exception) {
            showInvalidMessage("Invalid Quantity")
            return
        }

    }

    fun onItemsSelected(chosenItems: List<InvoiceItem>?) = viewModelScope.launch {
        chosenItems?.let { items ->
            // this delay is to solve an unexpected bug
            delay(100)
            itemsFlow.value = items
        }
    }


    private fun showInvalidMessage(message: String) = viewModelScope.launch {
        eventsChannel.send(WindowEvents.ShowMessage(message))
    }

    fun onNextPressed() = viewModelScope.launch {
        val myItems = itemsFlow.value
        if (myItems.isNotEmpty()) {

            val invoice = Invoice(
                id = invoiceId ?: 0,
                date = Calendar.getInstance().timeInMillis,
                items = myItems,
                total = myItems.sumOf { it.total },
            )

            eventsChannel.send(
                WindowEvents.Navigate(
                    ConfirmationFragmentDirections
                        .actionNewEstimateFragmentToViewPdfFragment(
                            invoice
                        )
                )
            )
        }
    }

    fun onPreviousPressed() = viewModelScope.launch {
        eventsChannel.send(WindowEvents.NavigateBack)
    }


    sealed class WindowEvents {
        data class ShowMessage(val message: String) : WindowEvents()
        object NavigateBack : WindowEvents()
        data class Navigate(val direction: NavDirections) : WindowEvents()
    }
}