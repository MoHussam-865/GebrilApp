package com.android_a865.gebril_app.feature_main.presentation.main_page

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.feature_in_app.domain.use_cases.SubscriptionUseCase
import com.android_a865.gebril_app.feature_main.data.mapper.toItem
import com.android_a865.gebril_app.feature_main.domain.model.Item
import com.android_a865.gebril_app.feature_main.domain.use_cases.items_use_cases.ItemsUseCases
import com.android_a865.gebril_app.feature_settings.domain.models.DatabaseHolder
import com.android_a865.gebril_app.feature_settings.domain.models.ItemsHolder
import com.android_a865.gebril_app.utils.NO_AD
import com.android_a865.gebril_app.utils.Path
import com.android_a865.gebril_app.utils.update0
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val itemsUseCases: ItemsUseCases,
    private val subscriptionUseCase: SubscriptionUseCase,
    state: SavedStateHandle
): ViewModel() {

    val path = state.get<Path>("path")
    val currentPath = MutableStateFlow(path ?: Path())

    private val itemsFlow: Flow<List<Item>> = currentPath.flatMapLatest { path ->
        itemsUseCases.getItems(path.path)
    }

    private var hasAccess = false

    val itemsData = itemsFlow.asLiveData()

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            hasAccess = subscriptionUseCase()
        }
    }


    fun onItemClicked(item: Item) {
        currentPath.value = item.open()
    }


    fun onBackPressed() {
        if (currentPath.value.isRoot) {
            viewModelScope.launch {
                eventsChannel.send(WindowEvents.CloseTheApp)
            }
        } else {
            currentPath.value =  currentPath.value.back()
        }
    }

    fun onNewItemSelected() = viewModelScope.launch {
        eventsChannel.send(WindowEvents.Navigate(
            MainFragmentDirections.actionMainFragment2ToNewItemFragment(
                path = currentPath.value
            )
        ))
    }

    fun onNewFolderSelected() = viewModelScope.launch {
        eventsChannel.send(WindowEvents.Navigate(
            MainFragmentDirections.actionMainFragment2ToNewFolderFragment(
                path = currentPath.value
            )
        ))
    }

    fun onNewEstimateSelected() = viewModelScope.launch {
            eventsChannel.send(WindowEvents.Navigate(
                MainFragmentDirections.actionMainFragment2ToNewEstimateFragment()
            ))
    }

    fun onMyEstimateSelected() = viewModelScope.launch {
        eventsChannel.send(WindowEvents.Navigate(
            MainFragmentDirections.actionMainFragment2ToInvoicesViewFragment()
        ))
    }

    fun onMyClientsSelected() = viewModelScope.launch {
        eventsChannel.send(WindowEvents.Navigate(
            MainFragmentDirections.actionMainFragment2ToClientsFragment()
        ))
    }

    fun onSettingsSelected() = viewModelScope.launch {
        eventsChannel.send(WindowEvents.Navigate(
            MainFragmentDirections.actionMainFragment2ToSettingsFragment()
        ))
    }

    /** TODO don't remove this method */
    fun onSubscribeSelected() = viewModelScope.launch {
        eventsChannel.send(WindowEvents.Navigate(
            MainFragmentDirections.actionMainFragment2ToSubscribeFragment()
        ))
    }

    fun onReportsSelected()  = viewModelScope.launch {
        // TODO block Reporting feature
        if (hasAccess || NO_AD) {
            eventsChannel.send(WindowEvents.Navigate(
                MainFragmentDirections.actionMainFragment2ToReportsMainFragment()
            ))
        } else {
            eventsChannel.send(WindowEvents.Navigate(
                MainFragmentDirections.actionMainFragment2ToSubscribeFragment()
            ))
        }

        /*eventsChannel.send(WindowEvents.Navigate(
            MainFragmentDirections.actionMainFragment2ToReportsMainFragment()
        ))*/

    }

    fun onImportItemsSelected() = viewModelScope.launch {
        eventsChannel.send(WindowEvents.ImportData)
    }

    fun saveData(data: String) = viewModelScope.launch {
        var status = try {

            val holder = Gson().fromJson(data, ItemsHolder::class.java)

            // items
            holder.items.forEach {
                itemsUseCases.addItem(it.toItem())
            }
            "Done"
        } catch (e: Exception) {
            Log.d("Importing Error", e.message.toString())
            "Error"
        }

        eventsChannel.send(WindowEvents.ShowMsg(status))
    }

    sealed class WindowEvents {
        object CloseTheApp: WindowEvents()
        object ImportData: WindowEvents()
        data class Navigate(val direction: NavDirections): WindowEvents()
        data class ShowMsg(val msg: String): WindowEvents()
    }



}