package com.android_a865.gebril_app.data.domain

import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.data.relation.FullInvoice
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {

    fun getInvoices(): Flow<List<Invoice>>

    suspend fun getCart(): Flow<List<InvoiceItem>>

    suspend fun insertInvoice(invoice: FullInvoice)

    suspend fun updateInvoice(invoice: FullInvoice)

    suspend fun deleteInvoices(invoices: List<FullInvoice>)

}