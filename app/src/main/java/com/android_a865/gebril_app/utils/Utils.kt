package com.android_a865.gebril_app.utils

import androidx.lifecycle.MutableLiveData
import com.android_a865.gebril_app.feature_reports.domain.model.SmartList
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow

val <T> T.exhaustive: T get() = this


inline fun <T> MutableLiveData<T>.update0(fun0: (T?) -> T) {
    value = fun0(value)
}

inline fun <T> SmartList<T>.update0(fun0: (List<T>) -> List<T>) {
    value = fun0(value)
}

/** transform from and to Json */

fun <T> T.toJson(): String = Gson().toJson(this)

inline fun <reified T> String.toObject(): T =
    Gson().fromJson(this, object : TypeToken<T>() {}.type)


