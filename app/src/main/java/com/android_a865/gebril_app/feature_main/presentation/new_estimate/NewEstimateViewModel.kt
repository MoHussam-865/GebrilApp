package com.android_a865.gebril_app.feature_main.presentation.new_estimate

import android.content.Context
import android.text.InputType
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.feature_client.domain.model.Client
import com.android_a865.gebril_app.feature_client.domain.use_cases.ClientsUseCases
import com.android_a865.gebril_app.feature_main.domain.model.Invoice
import com.android_a865.gebril_app.feature_main.domain.model.InvoiceItem
import com.android_a865.gebril_app.feature_main.domain.model.InvoiceTypes
import com.android_a865.gebril_app.feature_main.domain.use_cases.invoice_use_cases.InvoiceUseCases
import com.android_a865.gebril_app.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NewEstimateViewModel @Inject constructor(
    private val invoiceUseCases: InvoiceUseCases,
    private val clientsUseCases: ClientsUseCases,
    state: SavedStateHandle
) : ViewModel() {

    private val invoice = state.get<Invoice>("invoice")

    val invoiceType = MutableStateFlow(invoice?.type ?: InvoiceTypes.Estimate)
    private val clientId = MutableStateFlow(invoice?.client?.id ?: 0)

    private val invoiceClient = clientId.flatMapLatest {
        if (it != 0) {
            clientsUseCases.getClient(it).map { thisClient ->
                thisClient ?: invoice?.client
            }
        } else {
            flowOf(null)
        }
    }

    val client = invoiceClient.asLiveData()

    val itemsFlow = MutableStateFlow(invoice?.items ?: listOf())

    val totalFlow = MutableStateFlow(invoice?.total ?: 0.0)

    private val eventsChannel = Channel<WindowEvents>()
    val invoiceWindowEvents = eventsChannel.receiveAsFlow()

    /** in app ads */
    init {
        if (!NO_AD) {
            viewModelScope.launch {
                eventsChannel.send(
                    WindowEvents.ShowAd
                )
            }
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

    fun onItemsSelected(chosenItems: List<InvoiceItem>?) = viewModelScope.launch {
        chosenItems?.let { items ->
            // this delay is to solve an unexpected bug
            delay(100)
            itemsFlow.value = items
        }
    }

    fun onOpenPdfClicked() {

        if (itemsFlow.value.filtered.isEmpty()) {
            showInvalidMessage("Add Items first to your invoice")
        } else {
            viewModelScope.launch {

                val invoice = getInvoice()
                eventsChannel.send(WindowEvents.OpenPdf(invoice))
            }
        }
    }

    fun onSaveClicked() {
        if (itemsFlow.value.filtered.isEmpty()) {
            showInvalidMessage("Add Items first")
        } else {
            viewModelScope.launch {

                val myInvoice = getInvoice()
                if (invoice != null) {
                    invoiceUseCases.updateInvoice(myInvoice)
                } else {
                    invoiceUseCases.addInvoice(myInvoice)
                }

                eventsChannel.send(
                    WindowEvents.NavigateBack(
                        "${invoiceType.value.name} Saved"
                    )
                )
            }
        }
    }

    private fun getInvoice(): Invoice {
        return invoice?.copy(
            type = invoiceType.value,
            date = Calendar.getInstance().timeInMillis,
            client = client.value,
            items = itemsFlow.value
        ) ?: Invoice(
            type = invoiceType.value,
            client = client.value,
            items = itemsFlow.value
        )
    }

    private fun showInvalidMessage(message: String) = viewModelScope.launch {
        eventsChannel.send(WindowEvents.ShowMessage(message))
    }

    fun onInvoiceTypeSelected(context: Context) {

        val types = InvoiceTypes.values().map { it.name }.toTypedArray()

        AlertDialog.Builder(context)
            .setTitle("Choose Type")
            .setSingleChoiceItems(
                types,
                invoiceType.value.ordinal
            ) { dialog, i ->
                invoiceType.value = InvoiceTypes.valueOf(types[i])
                dialog.dismiss()
            }.show()
    }

    fun onClientChosen(client: Client?) {
        client?.let {
            clientId.value = it.id
        }
    }

    fun onViewClientClicked() = viewModelScope.launch {
        eventsChannel.send(
            WindowEvents.Navigate(
                NewEstimateFragmentDirections.actionNewEstimateFragmentToClientViewFragment(
                    client.value!!
                )
            )
        )
    }

    fun onChooseClientSelected() = viewModelScope.launch {
        eventsChannel.send(
            WindowEvents.Navigate(
                NewEstimateFragmentDirections.actionNewEstimateFragmentToChooseClientFragment()
            )
        )
    }

    fun onItemHold(context: Context, invoiceItem: InvoiceItem) {

        if (!SPECIAL) return

        val editText = EditText(context)
        editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        editText.setText(invoiceItem.discount.toString())
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Enter Discount")
        builder.setView(editText)
        builder.setPositiveButton("Apply") { _, _ ->
            viewModelScope.launch {
                applyDiscount(invoiceItem, editText.text.toString(), false)
            }
        }
        builder.setNeutralButton("Apply to All") { _, _ ->
            viewModelScope.launch {
                applyDiscount(invoiceItem, editText.text.toString(), true)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        builder.show()

    }

    private suspend fun applyDiscount(
        item: InvoiceItem,
        txt: String,
        applyToAll: Boolean
    ) {
        try {
            val discount = txt.toDouble()
            itemsFlow.value = itemsFlow.value
                .setQtyTo(item.copy(discount = discount), item.qty)

            if (applyToAll) {
                val friends = invoiceUseCases.applyDiscountUseCase(item)

                itemsFlow.value
                    .filter { it.id in friends }
                    .forEach { friend ->
                        itemsFlow.value = itemsFlow.value.setQtyTo(
                            friend.copy(discount = discount),
                            friend.qty
                        )
                    }
            }

        } catch (e: Exception) {
            eventsChannel.send(WindowEvents.ShowMessage("Invalid Discount"))
        }

    }


    sealed class WindowEvents {
        data class OpenPdf(val invoice: Invoice) : WindowEvents()
        data class ShowMessage(val message: String) : WindowEvents()
        object ShowAd : WindowEvents()
        data class NavigateBack(val message: String) : WindowEvents()
        data class Navigate(val direction: NavDirections) : WindowEvents()
    }
}