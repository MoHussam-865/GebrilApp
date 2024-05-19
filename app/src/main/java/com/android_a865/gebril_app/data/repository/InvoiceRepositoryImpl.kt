package com.android_a865.gebril_app.data.repository

import com.android_a865.gebril_app.data.dao.InvoicesDao
import com.android_a865.gebril_app.data.domain.InvoiceHolder
import com.android_a865.gebril_app.data.domain.InvoiceRepository
import com.android_a865.gebril_app.data.mapper.toInvoice
import com.android_a865.gebril_app.data.relation.FullInvoice
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InvoiceRepositoryImpl(
    private val dao: InvoicesDao
) : InvoiceRepository {


    override fun getInvoices(): Flow<List<InvoiceHolder>> {
        return  dao.getInvoices().map {
            it.map { invoice ->
                invoice.toInvoice()
            }
        }
    }


    override suspend fun insertInvoice(invoice: FullInvoice) = dao.insertInvoice(invoice)

    override suspend fun updateInvoice(invoice: FullInvoice) = dao.updateInvoice(invoice)

    override suspend fun deleteInvoices(invoices: List<FullInvoice>) = dao.deleteInvoices(invoices)

}