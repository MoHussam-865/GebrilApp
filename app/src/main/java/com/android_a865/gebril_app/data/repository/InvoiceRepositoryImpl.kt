package com.android_a865.gebril_app.data.repository

import com.android_a865.gebril_app.data.dao.InvoicesDao
import com.android_a865.gebril_app.data.domain.Invoice
import com.android_a865.gebril_app.data.mapper.toInvoices
import com.android_a865.gebril_app.data.relation.FullInvoice
import com.android_a865.gebril_app.data.domain.InvoiceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class InvoiceRepositoryImpl(
    private val dao: InvoicesDao
) : InvoiceRepository {


    override fun getInvoices(): Flow<List<Invoice>> {
        return  dao.getInvoices().map {
            it.toInvoices()
        }
    }


    override suspend fun insertInvoice(invoice: FullInvoice) = dao.insertInvoice(invoice)

    override suspend fun updateInvoice(invoice: FullInvoice) = dao.updateInvoice(invoice)

    override suspend fun deleteInvoices(invoices: List<FullInvoice>) = dao.deleteInvoices(invoices)

}