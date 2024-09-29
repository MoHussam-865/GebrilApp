package com.android_a865.gebril_app.data.entities

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(
    tableName = "InvoiceItems",
    primaryKeys = ["invoiceId", "itemId"]
)
data class InvoiceItemEntity(
    val invoiceId: Int,
    val itemId: Int,
    val fullName: String,
    val qty: Double,
): Parcelable
