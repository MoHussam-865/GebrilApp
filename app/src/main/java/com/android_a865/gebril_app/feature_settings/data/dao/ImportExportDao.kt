package com.android_a865.gebril_app.feature_settings.data.dao

import androidx.room.*
import com.android_a865.gebril_app.feature_client.data.entities.ClientEntity
import com.android_a865.gebril_app.feature_main.data.entities.InvoiceEntity
import com.android_a865.gebril_app.feature_main.data.entities.InvoiceItemEntity
import com.android_a865.gebril_app.feature_main.data.entities.ItemEntity
import com.android_a865.gebril_app.feature_main.data.relations.FullInvoice

@Dao
interface ImportExportDao {

    // Exporting
    @Transaction
    @Query("SELECT * FROM Invoices")
    suspend fun getAllInvoices(): List<FullInvoice>

    @Query("SELECT * FROM Clients")
    suspend fun getAllClients(): List<ClientEntity>

    @Query("SELECT * FROM Items")
    suspend fun getAllItems(): List<ItemEntity>



    // Importing
    suspend fun insertInvoice(fullInvoice: FullInvoice) {
        fullInvoice.apply {
            val invoiceId = insertInvoice(invoice).toInt()

            items.forEach {
                insertInvoiceItem(it.copy(invoiceId = invoiceId))
            }
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoiceEntity: InvoiceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceItem(invoiceItemEntity: InvoiceItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemEntity(itemEntity: ItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(clientEntity: ClientEntity)


}