package com.android_a865.gebril_app.features.shopping

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.data.domain.CartRepo
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.data.domain.ItemsRepository
import com.android_a865.gebril_app.utils.Path
import com.android_a865.gebril_app.utils.include
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ItemsChooseViewModel @Inject constructor(
    private val itemsRepo: ItemsRepository,
    private val cartRepo: CartRepo
) : ViewModel() {

    val currentPath = MutableStateFlow(Path())

    private val itemsFlow = currentPath.flatMapLatest { path ->

        itemsRepo.getItems(path.folderId).map { items ->
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

    val selectedItems = cartRepo.getCart()

    val itemsData = combine(itemsFlow, selectedItems) { items, selected ->
        Pair(items, selected)
    }.flatMapLatest { (items, selected) ->
        flowOf(items.include(selected))
    }

    // Handle variables

    private val itemsWindowEventsChannel = Channel<ItemsWindowEvents>()
    val itemsWindowEvents = itemsWindowEventsChannel.receiveAsFlow()

    fun onBackPress() {
        if (currentPath.value.isRoot) {
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

    fun onAddItemClicked(item: InvoiceItem) = viewModelScope.launch {
        Log.d("folder", item.discount.toString())
        cartRepo.addOne(item)
    }

    fun onMinusItemClicked(item: InvoiceItem) = viewModelScope.launch {
        cartRepo.removeOne(item)
    }

    fun onItemRemoveClicked(item: InvoiceItem) = viewModelScope.launch {
        cartRepo.removeFromCart(item)
    }

    fun onQtySet(item: InvoiceItem, myQty: Double) = viewModelScope.launch {
        cartRepo.setQty(item, myQty)
    }

    fun onSearchChanged(txt: String) {
        // TODO Search items
    }

    fun onPathChangeRequested(item: InvoiceItem) {
        var path = currentPath.value.copy()
        while (!path.isRoot && path.folderId != item.id) {
            path = path.back()
        }
        currentPath.value = path
    }

    sealed class ItemsWindowEvents {
        data class NavigateTo(val direction: NavDirections) : ItemsWindowEvents()
        data object GoBack : ItemsWindowEvents()
        data class InvalidInput(val msg: String) : ItemsWindowEvents()
    }
}
