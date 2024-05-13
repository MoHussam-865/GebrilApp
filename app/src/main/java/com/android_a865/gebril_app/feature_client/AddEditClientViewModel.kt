package com.android_a865.gebril_app.feature_client//package com.android_a865.gebril_app.feature_client.presentation
//
//import androidx.lifecycle.ViewModel
//import androidx.navigation.NavDirections
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.flow.receiveAsFlow
//import javax.inject.Inject
//
//@HiltViewModel
//class AddEditClientViewModel @Inject constructor(
//
//) : ViewModel() {
//
//    //private val client = state.get<Client>("client")
//
//    var clientName = ""// = client?.name ?: ""
//    var org = "" //=client?.org ?: ""
//    var title ="" //= client?.title ?: ""
//    var phone1 = "" // = client?.phone1 ?: ""
//    //------------------------------------
//
//    private val eventChannel = Channel<WindowEvents>()
//    val windowEvents = eventChannel.receiveAsFlow()
//
//
//    fun onSaveClicked() {
//
////        viewModelScope.launch {
////
////            if (newClient.name.isBlank()) {
////                eventChannel.send(
////                    WindowEvents.ShowInvalidInputMessage(
////                        "Client Name can't be empty"
////                    )
////                )
////
////            } else {
////                clientsUseCases.addEditClient(newClient)
////                if (client != null) {
////                    eventChannel.send(WindowEvents.Navigate(
////                        AddEditClientFragmentDirections.actionAddEditClientFragmentToClientViewFragment(
////                            client = newClient
////                        )
////                    ))
////                }else {
////                    eventChannel.send(WindowEvents.NavigateBack)
////                }
////
////            }
////        }
//    }
//
//    sealed class WindowEvents {
//        data class ShowInvalidInputMessage(val msg: String) : WindowEvents()
//        object NavigateBack : WindowEvents()
//        data class Navigate(val direction: NavDirections): WindowEvents()
//    }
//
//}