package com.android_a865.gebril_app.feature_main.presentation.new_folder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_a865.gebril_app.feature_main.domain.model.Item
import com.android_a865.gebril_app.feature_main.domain.use_cases.items_use_cases.ItemsUseCases
import com.android_a865.gebril_app.utils.Path
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewFolderViewModel @Inject constructor(

    private val itemsUseCases: ItemsUseCases,
    state: SavedStateHandle
): ViewModel() {

    private val folder = state.get<Item>("folder")
    val path = state.get<Path>("path") ?: Path(".")

    var folderName: String = folder?.name ?: ""



    private val addEditItemChannel = Channel<AddEditItemEvent>()
    val addEditItemEvent = addEditItemChannel.receiveAsFlow()

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditItemChannel.send(AddEditItemEvent.ShowInvalidInputMessage(text))
    }

    fun onSaveClicked() {
        if (folderName.isBlank()) {
            showInvalidInputMessage("fill the required fields")
            return
        }

        if (folder != null) {
            val updatedItem = folder.copy(
                name = folderName,
                path = path,
                isFolder = true
            )
            updateFolder(updatedItem)


        } else {
            val newItem = Item(
                name = folderName,
                path = path,
                isFolder = true
            )
            createFolder(newItem)
        }


    }


    private fun updateFolder(item: Item) = viewModelScope.launch {
        itemsUseCases.updateItem(item)
        addEditItemChannel.send(AddEditItemEvent.NavigateBackWithResult2)
    }

    private fun createFolder(item: Item) = viewModelScope.launch {
        itemsUseCases.addItem(item)
        addEditItemChannel.send(AddEditItemEvent.NavigateBackWithResult)
    }


    sealed class AddEditItemEvent {
        data class ShowInvalidInputMessage(val msg: String): AddEditItemEvent()
        object NavigateBackWithResult: AddEditItemEvent()
        object NavigateBackWithResult2: AddEditItemEvent()

    }


}