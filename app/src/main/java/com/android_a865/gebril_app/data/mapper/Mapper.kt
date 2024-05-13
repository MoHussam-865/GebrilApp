package com.android_a865.gebril_app.data.mapper


import com.android_a865.gebril_app.data.domain.Invoice
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.data.entities.InvoiceEntity
import com.android_a865.gebril_app.data.entities.InvoiceItemEntity
import com.android_a865.gebril_app.data.entities.Item
import com.android_a865.gebril_app.data.relation.FullInvoice
import com.android_a865.gebril_app.utils.Path
import com.android_a865.gebril_app.utils.toJson


// database entity

fun Item.toInvoiceItem(): InvoiceItem {
    return InvoiceItem(
        id = id,
        name = name,
        fullName = Path(path).fullName(name),
        price = price,
        isFolder = is_folder,
        path = path
    )
}


fun List<Item>.toInvoiceItems() = map { it.toInvoiceItem() }

fun InvoiceItemEntity.toInvoiceItem() = InvoiceItem(
    id = itemId,
    name = name,
    fullName = name,
    qty = qty,
)

fun InvoiceItem.toEntity(invoiceId: Int) = InvoiceItemEntity(
    invoiceId = invoiceId,
    itemId = id,
    name = fullName,
    qty = qty,
)

fun FullInvoice.toInvoice() = Invoice(
    id = invoice.id,
    date = invoice.date,
    items = items.map { it.toInvoiceItem() },
    total = 0.0
)

fun Invoice.toEntity() = FullInvoice(
    invoice = InvoiceEntity(
        id = id,
        client = client,
        date = date,
        total = items.sumOf { it.total }
    ),

    items = items.map { it.toEntity(id) }
)

fun List<FullInvoice>.toInvoices() = map { it.toInvoice() }



