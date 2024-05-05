package com.android_a865.gebril_app.feature_client.presentation.clients_view

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.feature_client.domain.model.Client
import com.android_a865.gebril_app.feature_client.domain.use_cases.ClientsUseCases
import com.android_a865.gebril_app.feature_main.presentation.main_page.MainFragmentViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientsViewModel @Inject constructor(
    clientsUseCases: ClientsUseCases
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val clients = searchQuery.flatMapLatest {
        clientsUseCases.getClients(it)
    }

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()

    fun onItemClicked(client: Client) = viewModelScope.launch {
        eventsChannel.send(
            WindowEvents.Navigate(
                ClientsFragmentDirections.actionClientsFragmentToClientViewFragment(
                    client = client
                )
            )
        )
    }

    fun onFabClicked() = viewModelScope.launch {
        eventsChannel.send(
            WindowEvents.Navigate(
                ClientsFragmentDirections.actionClientsFragmentToAddEditClientFragment()
            )
        )
    }

    sealed class WindowEvents {
        data class Navigate(val direction: NavDirections) : WindowEvents()
    }
}

