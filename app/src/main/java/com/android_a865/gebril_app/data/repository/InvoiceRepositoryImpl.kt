package com.android_a865.gebril_app.data.repository

import com.android_a865.gebril_app.data.dao.InvoicesDao
import com.android_a865.gebril_app.data.dao.ItemsDao
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.data.domain.InvoiceRepository
import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.data.mapper.toInvoiceItem
import com.android_a865.gebril_app.data.relation.FullInvoice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InvoiceRepositoryImpl(
    private val dao: InvoicesDao,
    private val itemsDao: ItemsDao
) : InvoiceRepository {


    override fun getInvoices(): Flow<List<Invoice>> {

        return dao.getInvoices().map {
            it.map { fullInvoice ->
                toInvoice(fullInvoice)
            }
        }
    }

    override suspend fun getCart(): Flow<List<InvoiceItem>> {
        return dao.getCart().map { items ->
            items.map { item ->
                val itemId = item.itemId
                val qty = item.qty
                itemsDao.getItemById(itemId).toInvoiceItem().copy(qty = qty)
            }
        }
    }

    private suspend fun toInvoice(fullInvoice: FullInvoice): Invoice {

        val items = fullInvoice.items.map { invoiceItemEntity ->

            val itemId = invoiceItemEntity.itemId
            val qty = invoiceItemEntity.qty
            val invoiceItem = itemsDao.getItemById(itemId).toInvoiceItem().copy(qty = qty)

            invoiceItem
        }

        return fullInvoice.invoice.copy(items = items)
    }


    override suspend fun insertInvoice(invoice: FullInvoice) = dao.insertInvoice(invoice)

    override suspend fun updateInvoice(invoice: FullInvoice) = dao.updateInvoice(invoice)

    override suspend fun deleteInvoices(invoices: List<FullInvoice>) = dao.deleteInvoices(invoices)

}