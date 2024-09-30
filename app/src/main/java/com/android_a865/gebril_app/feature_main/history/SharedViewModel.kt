package com.android_a865.gebril_app.feature_main.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android_a865.gebril_app.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor() : ViewModel() {

    val selectedItem = MutableLiveData(R.id.navigation_home)

}