package com.android_a865.gebril_app.feature_reports.presentation.overview

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.android_a865.gebril_app.feature_reports.domain.model.FullReport
import com.android_a865.gebril_app.feature_reports.domain.model.ReportsTypes
import com.android_a865.gebril_app.feature_reports.domain.use_cases.FullReportUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val fullReport: FullReportUseCase
): ViewModel() {


    val report = MutableLiveData<FullReport?>()

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()


    init {
        viewModelScope.launch {
            report.value = fullReport()
        }
    }


    sealed class WindowEvents {
        data class NavigateTo(val direction: NavDirections): WindowEvents()

    }

}