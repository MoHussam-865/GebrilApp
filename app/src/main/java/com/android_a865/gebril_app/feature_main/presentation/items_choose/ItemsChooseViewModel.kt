package com.android_a865.gebril_app.feature_main.presentation.items_choose

import androidx.lifecycle.*
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.feature_main.domain.repository.ItemsRepository
import com.android_a865.gebril_app.utils.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemsChooseViewModel @Inject constructor(
    state: SavedStateHandle,
    val repository: ItemsRepository
) : ViewModel() {

    val currentPath = MutableStateFlow(state.get<Path>("path") ?: Path())

    private val itemsFlow = currentPath.flatMapLatest { path ->
        repository.getItems(path.path)
    }

    val selectedItems = MutableLiveData(
        state.get<Array<InvoiceItem>>("items")?.toList() ?: listOf()
    )
    val itemsData = combine(itemsFlow, selectedItems.asFlow()) { items, selected ->
        Pair(items, selected)
    }.flatMapLatest { (items, selected) ->
        flowOf(items.include(selected))
    }

    // Handle variables

    private val itemsWindowEventsChannel = Channel<ItemsWindowEvents>()
    val itemsWindowEvents = itemsWindowEventsChannel.receiveAsFlow()


    fun onBackPress() {
        if (currentPath.value.isRoot) goBack()
        else currentPath.value = currentPath.value.back()
    }

    private fun goBack() = viewModelScope.launch {
        itemsWindowEventsChannel.send(ItemsWindowEvents.GoBack)
    }

    fun onItemClicked(item: InvoiceItem) {
        if (item.isFolder) {
            viewModelScope.launch {
                currentPath.value = Path(item.path).open(item.name)
            }
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

    sealed class ItemsWindowEvents {
        data class NavigateTo(val direction: NavDirections) : ItemsWindowEvents()
        object GoBack : ItemsWindowEvents()
        data class InvalidInput(val msg: String) : ItemsWindowEvents()
    }
}
