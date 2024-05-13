package com.android_a865.gebril_app.feature_main.main_page

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.data.domain.Invoice
import com.android_a865.gebril_app.data.domain.Message
import com.android_a865.gebril_app.external_api.ItemsApi
import com.android_a865.gebril_app.data.domain.InvoiceRepository
import com.android_a865.gebril_app.data.domain.ItemsRepository
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
    private val itemsApi: ItemsApi,
    private val settings: SettingsRepository,
    private val itemsRepository: ItemsRepository,
    val invoicesRepository: InvoiceRepository,
) : ViewModel() {

    val invoices = invoicesRepository.getInvoices()

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
                Log.d("my error", e.message.toString())
                loadingEnd()
                return@launch
            } catch (e: HttpException) {
                Log.d("my error", e.message.toString())
                loadingEnd()
                return@launch
            }

            Log.d("my error", response.toString())
            if (response.isSuccessful && response.body() != null) {
                handleResponse(response.body()!!, mySettings)
            } else {
                loadingEnd("Server Response Error Estimates might be inaccurate")
                // use existing database
            }

        }
    }

    private suspend fun handleResponse(message: Message, mySettings: AppSettings) {
        // add items to database
        Log.d("my error", message.items?.size.toString())

        message.items?.let { items ->

            if (items.isNotEmpty()) {
                items.forEach {
                    itemsRepository.insertItem(it)
                }

                // update last_update & last_update_date
                val lastUpdate = items.maxOf { it.last_update }
                settings.updateSettings(
                    mySettings.copy(
                        lastUpdate = lastUpdate,
                        lastUpdateDate = System.currentTimeMillis()
                    )
                )
            }
        }

        loadingEnd("Successfully Updated")
    }

    fun onEditInvoiceClicked(invoice: Invoice) = viewModelScope.launch {
        eventsChannel.send(
            WindowEvents.Navigate(
                MainFragmentDirections.actionMainFragment3ToItemsChooseFragment(invoice)
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

    private fun loadingEnd(message: String? = null) = viewModelScope.launch {
        eventsChannel.send(WindowEvents.LoadingDone(message))
    }


    sealed class WindowEvents {
        data class LoadingDone(val message: String?) : WindowEvents()
        data class Navigate(val direction: NavDirections) : WindowEvents()
    }
}