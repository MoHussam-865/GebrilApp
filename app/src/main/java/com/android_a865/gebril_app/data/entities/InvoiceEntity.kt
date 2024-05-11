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
    val isSent: Int = 0,
)
