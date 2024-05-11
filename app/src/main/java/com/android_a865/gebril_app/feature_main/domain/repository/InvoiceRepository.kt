package com.android_a865.gebril_app.feature_main.domain.repository

import com.android_a865.gebril_app.data.domain.Invoice
import com.android_a865.gebril_app.data.relation.FullInvoice
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {

    fun getInvoices(): Flow<List<Invoice>>

    suspend fun insertInvoice(invoice: FullInvoice)

    suspend fun updateInvoice(invoice: FullInvoice)

    suspend fun deleteInvoices(invoices: List<FullInvoice>)

}