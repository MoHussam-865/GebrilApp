package com.android_a865.gebril_app.data.domain

import android.os.Parcelable
import com.android_a865.gebril_app.data.entities.InvoiceItemEntity
import kotlinx.parcelize.Parcelize
import java.util.*


/**
 * this only used to be sent
 */
@Parcelize
data class InvoiceHolder(
    val id: Int = 0,
    val date: Long, // = Calendar.getInstance().timeInMillis,

    val items: List<InvoiceItemEntity>,
    val total: Double,

    val notes: String = "",
    val client: String = "",
): Parcelable
