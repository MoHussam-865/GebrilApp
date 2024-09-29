package com.android_a865.gebril_app.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.android_a865.gebril_app.data.domain.Client
import com.android_a865.gebril_app.data.domain.InvoiceItem
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "Invoices")
data class Invoice(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long,
    val notes: String = "",
    val isSent: Int = 0,

    @Ignore
    val items: List<InvoiceItem> = emptyList(),

    @Ignore
    val client: Client? = null
) : Parcelable {

    constructor(
        id: Int,
        date: Long,
        notes: String,
        isSent: Int,
    ): this(id=id, date=date, notes=notes, isSent=isSent, client = null, items = emptyList())

    val total get() = items.sumOf { it.total }
}