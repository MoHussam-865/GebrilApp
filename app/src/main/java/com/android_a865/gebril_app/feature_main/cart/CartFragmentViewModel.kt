package com.android_a865.gebril_app.feature_main.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.data.domain.CartRepo
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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class CartFragmentViewModel @Inject constructor(
    private val cartRepo: CartRepo
) : ViewModel() {

    val itemsFlow = cartRepo.getCart()

    private val eventsChannel = Channel<WindowEvents>()
    val invoiceWindowEvents = eventsChannel.receiveAsFlow()


    fun onItemRemoveClicked(item: InvoiceItem) = viewModelScope.launch {
        cartRepo.removeFromCart(item)
    }

    fun onOneItemAdded(item: InvoiceItem) = viewModelScope.launch {
        cartRepo.addToCart(item.copy(qty = item.qty + 1))
    }

    fun onOneItemRemoved(item: InvoiceItem) = viewModelScope.launch {
        cartRepo.addToCart(item.copy(qty = item.qty - 1))
    }

    fun onItemQtyChanged(item: InvoiceItem, qty: String) = viewModelScope.launch {
        try {

            val myQty = qty.toDouble()
            if (myQty < 0) {
                cartRepo.addToCart(item.copy(qty = 0.0))
                showInvalidMessage("Quantity can't be less than 0")
            } else {
                cartRepo.addToCart(item.copy(qty = myQty))
            }
        } catch (e: Exception) {
            showInvalidMessage("Invalid Quantity")
        }
    }


    private fun showInvalidMessage(message: String) = viewModelScope.launch {
        eventsChannel.send(WindowEvents.ShowMessage(message))
    }

    /*fun onNextPressed() = viewModelScope.launch {
        val myItems = itemsFlow.value
        if (myItems.isNotEmpty()) {

            val invoice = Invoice(
                date = Calendar.getInstance().timeInMillis,
                items = myItems,
            )

            // save the changes

        }
    }*/

    sealed class WindowEvents {
        data class ShowMessage(val message: String) : WindowEvents()
        data class Navigate(val direction: NavDirections) : WindowEvents()
    }
}