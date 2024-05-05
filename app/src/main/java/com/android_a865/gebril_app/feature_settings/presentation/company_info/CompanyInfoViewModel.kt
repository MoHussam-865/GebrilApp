package com.android_a865.gebril_app.feature_settings.presentation.company_info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android_a865.gebril_app.feature_settings.domain.models.Company
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyInfoViewModel @Inject constructor(
    private val repository: SettingsRepository
) : ViewModel() {
    val appSettings = repository.getAppSettings().map { it.company }
    var companyName = ""
    var personName = ""
    var phone = ""
    var email = ""
    var address = ""

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()


    fun onSaveClicked() = viewModelScope.launch {
        repository.updateCompanyInfo(
            Company(
                companyName = companyName,
                personName = personName,
                phone = phone,
                email = email,
                address = address
            )
        )

        eventsChannel.send(WindowEvents.NavigateBack)
    }

    sealed class WindowEvents {
        object NavigateBack: WindowEvents()
    }
}