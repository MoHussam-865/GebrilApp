package com.android_a865.gebril_app.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Invoice(
    val id: Int = 0,
    val date: Long, // = Calendar.getInstance().timeInMillis,

    val items: List<InvoiceItem>,
    val total: Double,

    val notes: String = "",
    val client: String = "",
): Parcelable

