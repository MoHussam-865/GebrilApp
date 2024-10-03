package com.android_a865.gebril_app.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Client(
    val name: String,
    val companyName: String = "",
    val phone: String,
    val email: String = "",
    val address: String,
    val location: String? = null
): Parcelable