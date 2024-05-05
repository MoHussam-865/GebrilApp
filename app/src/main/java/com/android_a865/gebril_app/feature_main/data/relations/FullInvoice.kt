package com.android_a865.gebril_app.feature_main.data.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.android_a865.gebril_app.feature_client.data.entities.ClientEntity
import com.android_a865.gebril_app.feature_main.data.entities.InvoiceEntity
import com.android_a865.gebril_app.feature_main.data.entities.InvoiceItemEntity

// get all data for the invoice
data class FullInvoice(
    @Embedded
    val invoice: InvoiceEntity,

    @Relation(
        parentColumn = "clientId",
        entityColumn = "id"
    )
    val client: ClientEntity?,

    @Relation(
        parentColumn = "id",
        entityColumn = "invoiceId"
    )
    val items: List<InvoiceItemEntity>,

    )