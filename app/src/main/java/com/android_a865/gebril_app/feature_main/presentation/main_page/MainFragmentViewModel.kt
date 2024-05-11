package com.android_a865.gebril_app.feature_main.presentation.main_page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.data.domain.Message
import com.android_a865.gebril_app.external_api.ItemsApi
import com.android_a865.gebril_app.feature_main.domain.repository.ItemsRepository
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    val itemsApi: ItemsApi,
    val settings: SettingsRepository,
    val repository: ItemsRepository,
) : ViewModel() {

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            val mySettings = settings.getAppSetting()
            val message = Message(
                last_update = mySettings.lastUpdate
            )
            val response = try {
                itemsApi.getItems(message)
            } catch (e: IOException) {
                showMessage(e.message.toString())
                loadingEnd()
                return@launch
            } catch (e: HttpException) {
                showMessage(e.message.toString())
                loadingEnd()
                return@launch
            }

            if (response.isSuccessful && response.body() != null) {
                handleResponse(response.body()!!, mySettings)
            } else {
                showMessage("Server Response Error Estimates might be inaccurate")
                // use existing database
            }

        }
    }

    private suspend fun handleResponse(message: Message, mySettings: AppSettings) {
        // add items to database
        message.items?.forEach {
            when (it.status) {
                1 -> repository.insertItem(it)
                0 -> repository.deleteItem(it)
            }
        }
        // update last_update & last_update_date
        settings.updateSettings(
            mySettings.copy(
                lastUpdate = message.last_update,
                lastUpdateDate = System.currentTimeMillis()
            )
        )
        showMessage("Successfully Updated")
    }


    fun onSavingsCalled() = viewModelScope.launch {
        eventsChannel.send(
            WindowEvents.Navigate(
                MainFragmentDirections.actionMainFragment3ToInvoicesViewFragment()
            )
        )
    }

    fun onNewEstimateClicked() = viewModelScope.launch {
        eventsChannel.send(
            WindowEvents.Navigate(
                MainFragmentDirections.actionMainFragment3ToItemsChooseFragment()
            )
        )
    }

    private fun loadingEnd() = viewModelScope.launch {
        eventsChannel.send(WindowEvents.LoadingDone)
    }

    private fun showMessage(message: String) = viewModelScope.launch {
        eventsChannel.send(WindowEvents.Message(message))
    }


    sealed class WindowEvents {
        object LoadingDone : WindowEvents()
        data class Navigate(val direction: NavDirections) : WindowEvents()
        data class Message(val message: String) : WindowEvents()
    }
}