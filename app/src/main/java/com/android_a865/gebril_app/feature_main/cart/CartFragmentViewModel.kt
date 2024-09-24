package com.android_a865.gebril_app.feature_main.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.data.domain.InvoiceRepository
import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.utils.addOneOf
import com.android_a865.gebril_app.utils.removeAllOf
import com.android_a865.gebril_app.utils.removeOneOf
import com.android_a865.gebril_app.utils.setQtyTo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CartFragmentViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository
) : ViewModel() {

    val itemsFlow = MutableStateFlow(emptyList<InvoiceItem>())

    private val eventsChannel = Channel<WindowEvents>()
    val invoiceWindowEvents = eventsChannel.receiveAsFlow()


    init {
        viewModelScope.launch {
            val items = invoiceRepository.getCart().first()
            itemsFlow.value = items
        }
    }

    fun onItemRemoveClicked(item: InvoiceItem) {
        itemsFlow.value = itemsFlow.value.removeAllOf(item)
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


    private fun showInvalidMessage(message: String) = viewModelScope.launch {
        eventsChannel.send(WindowEvents.ShowMessage(message))
    }

    fun onNextPressed() = viewModelScope.launch {
        val myItems = itemsFlow.value
        if (myItems.isNotEmpty()) {

            val invoice = Invoice(
                date = Calendar.getInstance().timeInMillis,
                items = myItems,
            )

            // save the changes

        }
    }

    fun save() {
        val myItems = itemsFlow.value

        if (myItems.isNotEmpty()) {

            val invoice = Invoice(
                date = Calendar.getInstance().timeInMillis,
                items = myItems,
            )

        }
    }

    sealed class WindowEvents {
        data class ShowMessage(val message: String) : WindowEvents()
        data class Navigate(val direction: NavDirections) : WindowEvents()
    }
}