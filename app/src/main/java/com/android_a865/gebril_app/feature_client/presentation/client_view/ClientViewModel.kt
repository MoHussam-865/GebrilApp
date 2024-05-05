package com.android_a865.gebril_app.feature_client.presentation.client_view

import android.app.AlertDialog
import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.feature_client.domain.model.Client
import com.android_a865.gebril_app.feature_client.domain.use_cases.ClientsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientsUseCases: ClientsUseCases,
    state: SavedStateHandle
): ViewModel() {

    val client = state.get<Client>("client")
    val isDatabaseClient = clientsUseCases.getClient(client?.id ?: 0).map { it!=null }

    private val eventChannel = Channel<WindowEvents>()
    val windowEvents = eventChannel.receiveAsFlow()


    fun onCall1Clicked() = viewModelScope.launch {
        eventChannel.send(WindowEvents.MakePhoneCall(
            client?.phone1
        ))
    }

    fun onCall2Clicked() = viewModelScope.launch {
        eventChannel.send(WindowEvents.MakePhoneCall(
            client?.phone2
        ))
    }

    fun onSendEmailClicked() = viewModelScope.launch {
        eventChannel.send(WindowEvents.SendEmail(
            client?.email
        ))
    }

    fun onDeleteClientSelected(context: Context){
        AlertDialog.Builder(context)
            .setTitle(context.resources.getString(R.string.delete_client))
            .setMessage(context.resources.getString(R.string.delete_client_dialog))
            .setPositiveButton(context.resources.getString(R.string.delete)) { dialog, _ ->
                deleteClient()
                dialog.dismiss()
            }
            .setNegativeButton(context.resources.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun deleteClient() = viewModelScope.launch {
        clientsUseCases.deleteClient(client)
        eventChannel.send(WindowEvents.NavigateBack)
    }

    sealed class WindowEvents {
        data class MakePhoneCall(val phone: String?): WindowEvents()
        data class SendEmail(val email: String?): WindowEvents()
        object NavigateBack: WindowEvents()
    }

}