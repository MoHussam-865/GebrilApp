package com.android_a865.gebril_app.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.android_a865.gebril_app.data.domain.InvoiceItem
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "Invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val date: Long,
    val total: Double,
    val notes: String = "",
    val client: String = "",
    val is_sent: Int = 0,

    @Ignore
    val items: List<InvoiceItem> = emptyList()
) : Parcelable {

    constructor(
        id: Int,
        date: Long,
        total: Double,
        notes: String,
        client: String,
        is_sent: Int
    ): this(id, date, total, notes, client, is_sent, emptyList())

}