package com.android_a865.gebril_app.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.data.domain.CartRepo
import com.android_a865.gebril_app.data.domain.InvoiceItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
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
        cartRepo.addOne(item)
    }

    fun onOneItemRemoved(item: InvoiceItem) = viewModelScope.launch {
        cartRepo.removeOne(item)
    }

    fun onItemQtyChanged(item: InvoiceItem, qty: String) = viewModelScope.launch {
        try {

            val myQty = qty.toDouble()
            cartRepo.setQty(item, myQty)
        } catch (e: Exception) {
            showInvalidMessage("Invalid Quantity")
        }
    }


    private fun showInvalidMessage(message: String) = viewModelScope.launch {
        eventsChannel.send(WindowEvents.ShowMessage(message))
    }

    fun clearCartClicked() = viewModelScope.launch {
        cartRepo.clearCart()
    }


    sealed class WindowEvents {
        data class ShowMessage(val message: String) : WindowEvents()
        data class Navigate(val direction: NavDirections) : WindowEvents()
    }
}