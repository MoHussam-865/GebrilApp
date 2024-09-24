package com.android_a865.gebril_app.feature_client

import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class ClientInfoViewModel @Inject constructor(

) : ViewModel() {


    private val eventChannel = Channel<WindowEvents>()
    val windowEvents = eventChannel.receiveAsFlow()



    sealed class WindowEvents {
        data class ShowInvalidInputMessage(val msg: String) : WindowEvents()
        object NavigateBack : WindowEvents()
        data class Navigate(val direction: NavDirections): WindowEvents()
    }

}