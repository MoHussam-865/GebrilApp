package com.android_a865.gebril_app.features.client

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_a865.gebril_app.data.domain.CartRepo
import com.android_a865.gebril_app.data.domain.Client
import com.android_a865.gebril_app.data.domain.InvoiceRepository
import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.data.mapper.toEntity
import com.android_a865.gebril_app.external_api.ItemsApi
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepository
import com.android_a865.gebril_app.utils.toClient
import com.android_a865.gebril_app.utils.toJson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ClientInfoViewModel @Inject constructor(
    private val serverApi: ItemsApi,
    private val invoiceRepo: InvoiceRepository,
    private val cartRepo: CartRepo,
    savedStateHandle: SavedStateHandle,
    private val settingsRepo: SettingsRepository
) : ViewModel() {

    private val invoice = savedStateHandle.get<Invoice>("invoice")
    private var appSettings: AppSettings? = null
    val client: MutableLiveData<Client?> = MutableLiveData()

    var name = ""
    var companyName = ""
    var phone = ""
    var email = ""
    var address = ""

    val nameError = MutableLiveData("")
    val phoneError = MutableLiveData("")
    val addressError = MutableLiveData("")

    private val eventChannel = Channel<WindowEvents>()
    val windowEvents = eventChannel.receiveAsFlow()

    init {
        viewModelScope.launch {
            appSettings = settingsRepo.getAppSetting()
            val clientInfo = appSettings?.clientInfo
            try {
                client.value = clientInfo?.toClient()
            }
            catch (_: Exception) {
                Log.d("client info", "Couldn't convert to client $clientInfo")
            }
        }
    }

    fun onSendOrder() = viewModelScope.launch {

        if (!check()) {
            return@launch
        }

        saveClient()

        val response = try {
            invoice?.let { invoice ->
                serverApi.sendOrder(invoice = invoice)
            }

        } catch (e: IOException) {
            showMessage("Error Not Found")
            return@launch
        } catch (e: HttpException) {
            showMessage("Connection Error")
            return@launch
        }

        Log.d("my error", response.toString())

        response?.let {
            if (it.isSuccessful && it.body() != null) {
                saveInvoice()
            } else {
                showMessage("Server Error")
            }
        }
    }

    private fun saveClient() = viewModelScope.launch {
        val myClient = Client(
            name = name,
            companyName = companyName,
            phone = phone,
            email = email,
            address = address
        )
        val c =  myClient.toJson()
        Log.d("client info", c)
        // save the client info
        appSettings?.let {
            settingsRepo.updateSettings(
                it.copy(
                    clientInfo = c
                )
            )
        }
    }

    private fun check(): Boolean {
        if (name.isBlank()) {
            nameError.value = "Please Enter The Name"
            return false
        } else if (phone.isBlank()) {
            phoneError.value = "Please Enter The Phone Number"
            return false
        } else if (address.isBlank()) {
            addressError.value = "Please Enter The Address"
            return false
        }
        return true
    }

    private fun saveInvoice() = viewModelScope.launch {

        invoice?.let {
            invoiceRepo.insertInvoice(it.toEntity())
            cartRepo.clearCart()
        }
        eventChannel.send(WindowEvents.NavigateBack)
    }

    private fun showMessage(msg: String) = viewModelScope.launch {
        eventChannel.send(
            WindowEvents.ShowInvalidInputMessage(msg)
        )
    }

    sealed class WindowEvents {
        data class ShowInvalidInputMessage(val msg: String) : WindowEvents()
        data object NavigateBack : WindowEvents()
    }

}