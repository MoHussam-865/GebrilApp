package com.android_a865.gebril_app.feature_client.domain.model

import android.os.Parcelable
import android.provider.ContactsContract
import kotlinx.parcelize.Parcelize

@Parcelize
data class Client(
    val id: Int = 0,
    val name: String,
    val org: String? = null,
    val title: String? = null,
    val phone1: String? = null,
    val phone2: String? = null,
    val email: String? = null,
    val address: String? = null
): Parcelable