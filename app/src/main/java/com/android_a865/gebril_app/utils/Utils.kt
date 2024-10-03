package com.android_a865.gebril_app.utils

import androidx.lifecycle.MutableLiveData
import com.android_a865.gebril_app.data.domain.Client
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

val <T> T.exhaustive: T get() = this


inline fun <T> MutableLiveData<T>.update0(fun0: (T?) -> T) {
    value = fun0(value)
}

fun <T> T.toJson(): String = Gson().toJson(this)

fun String.toClient(): Client = Gson().fromJson(this, Client::class.java)



