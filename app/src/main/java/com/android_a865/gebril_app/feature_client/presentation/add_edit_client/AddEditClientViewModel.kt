package com.android_a865.gebril_app.feature_client.presentation.add_edit_client

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.feature_client.domain.model.Client
import com.android_a865.gebril_app.feature_client.domain.use_cases.ClientsUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditClientViewModel @Inject constructor(
    private val clientsUseCases: ClientsUseCases,
    state: SavedStateHandle
) : ViewModel() {
    private val client = state.get<Client>("client")

    var clientName = client?.name ?: ""
    var org = client?.org ?: ""
    var title = client?.title ?: ""
    var phone1 = client?.phone1 ?: ""
    var phone2 = client?.phone2 ?: ""
    var email = client?.email ?: ""
    var address = client?.address ?: ""
    //------------------------------------

    private val eventChannel = Channel<WindowEvents>()
    val windowEvents = eventChannel.receiveAsFlow()


    fun onSaveClicked() {
        val newClient = client?.copy(
            name = clientName,
            org = org,
            title = title,
            phone1 = phone1,
            phone2 = phone2,
            email = email,
            address = address
        ) ?: Client(
            name = clientName,
            org = org,
            title = title,
            phone1 = phone1,
            phone2 = phone2,
            email = email,
            address = address
        )

        viewModelScope.launch {

            if (newClient.name.isBlank()) {
                eventChannel.send(
                    WindowEvents.ShowInvalidInputMessage(
                        "Client Name can't be empty"
                    )
                )

            } else {
                clientsUseCases.addEditClient(newClient)
                if (client != null) {
                    eventChannel.send(WindowEvents.Navigate(
                        AddEditClientFragmentDirections.actionAddEditClientFragmentToClientViewFragment(
                            client = newClient
                        )
                    ))
                }else {
                    eventChannel.send(WindowEvents.NavigateBack)
                }

            }
        }
    }

    sealed class WindowEvents {
        data class ShowInvalidInputMessage(val msg: String) : WindowEvents()
        object NavigateBack : WindowEvents()
        data class Navigate(val direction: NavDirections): WindowEvents()
    }

}