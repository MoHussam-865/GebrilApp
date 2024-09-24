package com.android_a865.gebril_app.data.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.data.entities.InvoiceItemEntity

// get all data for the invoice
data class FullInvoice(
    @Embedded
    val invoice: Invoice,

    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val items: List<InvoiceItemEntity>

)