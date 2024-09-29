package com.android_a865.gebril_app.data.mapper


import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.data.entities.InvoiceItemEntity
import com.android_a865.gebril_app.data.entities.Item
import com.android_a865.gebril_app.data.relation.FullInvoice


// database entity
fun Item.toInvoiceItem(): InvoiceItem {
    return InvoiceItem(
        id = id,
        name = name,
        price = price,
        discount = discount,
        isFolder = isFolder,
        parentId = parentId,
        imagePath = imagePath
    )
}


fun List<Item>.toInvoiceItems() = map { it.toInvoiceItem() }


fun InvoiceItem.toEntity(invoiceId: Int) = InvoiceItemEntity(
    invoiceId = invoiceId,
    itemId = id,
    fullName = fullName,
    qty = qty,
)


fun Invoice.toEntity() = FullInvoice(
    invoice = Invoice(
        id = id,
        client = client,
        date = date,
    ),

    items = items.map {
        InvoiceItemEntity(
            itemId = it.id,
            invoiceId = id,
            fullName = it.fullName,
            qty = it.qty
        )
    }
)




