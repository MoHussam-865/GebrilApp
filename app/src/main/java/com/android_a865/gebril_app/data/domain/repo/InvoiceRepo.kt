package com.android_a865.gebril_app.data.domain.repo

import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.data.relation.FullInvoice
import kotlinx.coroutines.flow.Flow

interface InvoiceRepo {

    fun getInvoices(): Flow<List<Invoice>>

    suspend fun insertInvoice(invoice: FullInvoice)

    suspend fun updateInvoice(invoice: FullInvoice)

    suspend fun deleteInvoices(invoices: List<FullInvoice>)

}