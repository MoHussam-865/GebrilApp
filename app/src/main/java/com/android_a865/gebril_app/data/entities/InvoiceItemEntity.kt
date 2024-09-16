package com.android_a865.gebril_app.data.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import androidx.room.Entity
import androidx.room.PrimaryKey


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
