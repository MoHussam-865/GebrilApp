package com.android_a865.gebril_app.feature_main.items_choose

import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.data.domain.Invoice
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.data.domain.ItemsRepository
import com.android_a865.gebril_app.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemsChooseViewModel @Inject constructor(
    state: SavedStateHandle,
    val repository: ItemsRepository
) : ViewModel() {

    private val invoice = state.get<Invoice>("invoice")

    val currentPath = MutableStateFlow(Path())

    private val itemsFlow = currentPath.flatMapLatest { path ->
        repository.getItems(path.path).map { items ->
            items.map { item ->
                if (item.isFolder) item else item.copy(discount = path.pathDiscount)
            }
        }
    }

    val selectedItems = MutableLiveData(invoice?.items ?: listOf())

    val itemsData = combine(itemsFlow, selectedItems.asFlow()) { items, selected ->
        Pair(items, selected)
    }.flatMapLatest { (items, selected) ->
        flowOf(items.include(selected))
    }

    // Handle variables

    private val itemsWindowEventsChannel = Channel<ItemsWindowEvents>()
    val itemsWindowEvents = itemsWindowEventsChannel.receiveAsFlow()

    init {
        if (!selectedItems.value.isNullOrEmpty()) {
            viewModelScope.launch {
                delay(300)
                onNextClicked()
            }
        }
    }

    fun onBackPress() {
        if (currentPath.value.isRoot) {
            // TODO set data lose warning
            goBack()
        } else {
            currentPath.value = currentPath.value.back()
        }
    }

    private fun goBack() = viewModelScope.launch {
        itemsWindowEventsChannel.send(ItemsWindowEvents.GoBack)
    }

    fun onItemClicked(item: InvoiceItem) {
        if (item.isFolder) {
            currentPath.value = currentPath.value.open(item)
        }
    }

    fun onAddItemClicked(item: InvoiceItem) {
        selectedItems.update0 { it?.addOneOf(item) }
    }

    fun onMinusItemClicked(item: InvoiceItem) {
        selectedItems.update0 { it?.removeOneOf(item) }
    }

    fun onItemRemoveClicked(item: InvoiceItem) {
        selectedItems.update0 {
            it?.removeAllOf(item)
        }
    }


    fun onQtySet(item: InvoiceItem, myQty: Double) {
        if (myQty > 0) {
            selectedItems.update0 {
                it?.setQtyTo(item, myQty)
            }
        } else {
            selectedItems.update0 { it?.removeAllOf(item) }
            viewModelScope.launch {
                showInvalidInputMessage("Quantity can't be less than 0")
            }
        }
    }

    //fun onInvoiceItemAdded(item: InvoiceItem) = selectedItems.update0 { it?.addOf(item) }

    private suspend fun showInvalidInputMessage(str: String) {
        itemsWindowEventsChannel.send(
            ItemsWindowEvents.InvalidInput(str)
        )
    }

    fun onNextClicked() = viewModelScope.launch {

        val myItems = selectedItems.value?.toTypedArray()

        if (!myItems.isNullOrEmpty()) {
            itemsWindowEventsChannel.send(
                ItemsWindowEvents.NavigateTo(
                    ItemsChooseFragmentDirections.actionItemsChooseFragmentToNewEstimateFragment(
                        myItems
                    )
                )
            )
        } else {
            showInvalidInputMessage("لم يتم اختيار اي صنف")
        }
    }


    fun onItemsSelected(chosenItems: List<InvoiceItem>?) = viewModelScope.launch {
        chosenItems?.let { items ->
            // this delay is to solve an unexpected bug
            delay(100)
            selectedItems.value = items
        }
    }

    sealed class ItemsWindowEvents {
        data class NavigateTo(val direction: NavDirections) : ItemsWindowEvents()
        object GoBack : ItemsWindowEvents()
        data class InvalidInput(val msg: String) : ItemsWindowEvents()
    }
}
