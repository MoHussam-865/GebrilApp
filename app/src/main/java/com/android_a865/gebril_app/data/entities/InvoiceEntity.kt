package com.android_a865.gebril_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: Long,
    val total: Double,
    val notes: String = "",
    val client: String = "",
    val is_sent: Int = 0,
)
