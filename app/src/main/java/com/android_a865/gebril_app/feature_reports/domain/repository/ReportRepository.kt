package com.android_a865.gebril_app.feature_reports.domain.repository

import com.android_a865.gebril_app.feature_client.data.entities.ClientEntity
import com.android_a865.gebril_app.feature_main.data.entities.InvoiceItemEntity
import com.android_a865.gebril_app.feature_main.data.relations.FullInvoice

interface ReportRepository {

    suspend fun getNumberOf(invoiceType: String): Int

    suspend fun getTotalOf(invoiceType: String): Double?

    suspend fun getNumberOfClients(): Int

    suspend fun getNumberOfItems(): Int

    suspend fun getClients(): List<ClientEntity>

    suspend fun getClientInvoices(id: Int): List<FullInvoice>

    suspend fun getInvoicesItems(): List<InvoiceItemEntity>

    suspend fun getInvoices(): List<FullInvoice>

}