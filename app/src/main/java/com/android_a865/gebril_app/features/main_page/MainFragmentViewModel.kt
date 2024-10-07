package com.android_a865.gebril_app.features.main_page

import android.content.Context
import android.os.NetworkOnMainThreadException
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.data.domain.repo.ItemRepo
import com.android_a865.gebril_app.data.domain.Message
import com.android_a865.gebril_app.data.domain.repo.PostRepo
import com.android_a865.gebril_app.external_api.ItemsApi
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepo
import com.android_a865.gebril_app.utils.HandleApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    settingsRepo: SettingsRepo,
    postRepo: PostRepo,
    private val updateDatabase: HandleApi
) : ViewModel() {

    val posts = postRepo.getPosts()

    val appSettings = settingsRepo.getAppSettings()


    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                updateDatabase.updateDatabase()
            } catch (e: Exception) {
                loadingEnd(e.message)
            }
        }
    }


    private suspend fun loadingEnd(message: String? = null)  {
        eventsChannel.send(WindowEvents.LoadingDone(message))
    }


    sealed class WindowEvents {
        data class LoadingDone(val message: String?) : WindowEvents()
    }
}