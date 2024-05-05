package com.android_a865.gebril_app.feature_main.presentation.add_edit_invoice_item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_a865.gebril_app.feature_main.domain.model.InvoiceItem
import com.android_a865.gebril_app.feature_main.domain.model.Item
import com.android_a865.gebril_app.feature_main.domain.use_cases.items_use_cases.ItemsUseCases
import com.android_a865.gebril_app.utils.Path
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditInvoiceItemViewModel @Inject constructor(
    state: SavedStateHandle,
    private val itemsUseCases: ItemsUseCases
) : ViewModel() {

    private val path = state.get<Path>("path") ?: Path()
    private val myItem = state.get<InvoiceItem>("item")

    val isNew = myItem == null

    var itemName = myItem?.name ?: ""
    var itemPrice = (myItem?.price ?: 0.0).toString()
    var itemQty = (myItem?.qty ?: 1.0).toString()


    private val eventsChannel = Channel<AddEditItemEvent>()
    val windowEvents = eventsChannel.receiveAsFlow()

    fun onSaveClicked() {
        val price = itemPrice.toDoubleOrNull()
        val qty = itemQty.toDoubleOrNull()

        if (itemName.isBlank()) {
            showInvalidInputMessage("Name can't be blank")
            return
        }

        if (price == null || price <= 0.0) {
            showInvalidInputMessage("Invalid price")
            return
        }

        if (qty == null || qty <= 0.0) {
            showInvalidInputMessage("Invalid Quantity")
            return
        }

        viewModelScope.launch {

            // this function will insert or update (if id != 0)
            val itemId = itemsUseCases.addItem(
                Item(
                    id = myItem?.id ?: 0,
                    name = itemName,
                    price = price,
                    path = path
                )
            )





            eventsChannel.send(
                AddEditItemEvent.NavigateBackWithResult(
                    InvoiceItem(
                        id = itemId,
                        name = itemName,
                        fullName = path.fullName(itemName),
                        price = price,
                        qty = qty
                    )
                )
            )
        }


    }

    private fun showInvalidInputMessage(msg: String) = viewModelScope.launch {
        eventsChannel.send(AddEditItemEvent.ShowInvalidInputMessage(msg))
    }


    sealed class AddEditItemEvent {
        data class ShowInvalidInputMessage(val msg: String) : AddEditItemEvent()
        data class NavigateBackWithResult(val item: InvoiceItem) : AddEditItemEvent()
    }
}