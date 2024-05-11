package com.android_a865.gebril_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "InvoiceItems",
    primaryKeys = ["invoiceId", "itemId"]
)
data class InvoiceItemEntity(
    val invoiceId: Int,
    val itemId: Int,
    val name: String,
    val qty: Double,
)
