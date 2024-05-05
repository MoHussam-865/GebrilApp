package com.android_a865.gebril_app.feature_client.presentation.choose_client

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.feature_client.domain.model.Client
import com.android_a865.gebril_app.feature_client.domain.use_cases.ClientsUseCases
import com.android_a865.gebril_app.feature_client.presentation.clients_view.ClientsViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChooseClientViewModel @Inject constructor(
    clientsUseCases: ClientsUseCases
): ViewModel() {

    val clients = clientsUseCases.getClients()

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()


    fun onFabClicked() = viewModelScope.launch {
        eventsChannel.send(WindowEvents.Navigate(
            ChooseClientFragmentDirections.actionChooseClientFragmentToAddEditClientFragment()
        ))
    }

    fun onItemClicked(client: Client) = viewModelScope.launch {
        eventsChannel.send(WindowEvents.NavigateBackWithResult(client))
    }


    sealed class WindowEvents{
        data class Navigate(val direction: NavDirections): WindowEvents()
        data class NavigateBackWithResult(val client: Client): WindowEvents()

    }
}