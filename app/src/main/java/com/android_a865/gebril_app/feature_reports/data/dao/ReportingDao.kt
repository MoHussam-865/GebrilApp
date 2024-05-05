package com.android_a865.gebril_app.feature_reports.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.android_a865.gebril_app.feature_client.data.entities.ClientEntity
import com.android_a865.gebril_app.feature_main.data.entities.InvoiceItemEntity
import com.android_a865.gebril_app.feature_main.data.relations.FullInvoice


@Dao
interface ReportingDao {

    @Query("SELECT COUNT(*) FROM Invoices WHERE type = :invoiceType")
    suspend fun getNumberOfInvoicesWithType(invoiceType: String): Int

    @Query("SELECT SUM(total) FROM Invoices WHERE type = :invoiceType")
    suspend fun getTotalOf(invoiceType: String): Double?

    @Query("SELECT COUNT(DISTINCT clientId) FROM Invoices")
    suspend fun getNumberOfClients(): Int

    @Query("SELECT COUNT(DISTINCT itemId) FROM InvoiceItems")
    suspend fun getNumberOfItems(): Int

    @Query("SELECT * FROM Clients")
    suspend fun getClients(): List<ClientEntity>

    @Transaction
    @Query("SELECT * FROM Invoices WHERE clientId = :id")
    suspend fun getClientInvoices(id: Int): List<FullInvoice>


    @Query("SELECT * FROM InvoiceItems")
    suspend fun getInvoicesItems(): List<InvoiceItemEntity>

    @Transaction
    @Query("SELECT * FROM Invoices")
    suspend fun getInvoices(): List<FullInvoice>


}