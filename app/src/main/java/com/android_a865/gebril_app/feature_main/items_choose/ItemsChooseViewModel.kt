package com.android_a865.gebril_app.feature_main.items_choose

import android.util.Log
import androidx.lifecycle.*
import androidx.navigation.NavDirections
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
    private val repository: ItemsRepository
) : ViewModel() {

    private val myInvoiceItems = state.get<Array<InvoiceItem>>("items")
    private val invoiceId = state.get<Int>("invoiceId")

    val currentPath = MutableStateFlow(Path())

    private val itemsFlow = currentPath.flatMapLatest { path ->
        Log.d("open folder", "path changed")
        repository.getItems(path.folderId).map { items ->
            items.map { item ->
                if (item.isFolder) {
                    item
                } else {
                    item.copy(
                        discount = path.folderDiscount,
                        fullName = path.fullName(item.name)
                    )
                }
            }
        }
    }

    val selectedItems = MutableLiveData(myInvoiceItems?.toList() ?: listOf())

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
                delay(200)
                onNextClicked()
            }
        }
    }

    fun onBackPress() {
        if (currentPath.value.isRoot) {
            // TODO set data lose warning
            goBack()
        } else {
            currentPath.value = currentPath.value.copy().back()
        }
    }

    private fun goBack() = viewModelScope.launch {
        itemsWindowEventsChannel.send(ItemsWindowEvents.GoBack)
    }

    fun onItemClicked(item: InvoiceItem) {
        if (item.isFolder) {
            Log.d("open folder", "folder opened ${item.discount}")
            val temp = currentPath.value.copy()
            val parents = temp.parents.toMutableList()
            parents.add(item)
            currentPath.value = Path(parents.toList())
        }
    }

    fun onAddItemClicked(item: InvoiceItem) {
        Log.d("folder", item.discount.toString())
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
                        items = myItems,
                        invoiceId = invoiceId ?: 0
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
