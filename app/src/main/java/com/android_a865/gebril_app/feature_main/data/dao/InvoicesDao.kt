package com.android_a865.gebril_app.feature_main.data.dao

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.*
import com.android_a865.gebril_app.feature_main.data.entities.InvoiceEntity
import com.android_a865.gebril_app.feature_main.data.entities.InvoiceItemEntity
import com.android_a865.gebril_app.feature_main.data.relations.FullInvoice
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoicesDao {

    @Transaction
    @Query("SELECT * FROM Invoices ORDER BY date DESC")
    fun getInvoices(): Flow<List<FullInvoice>>

    @Transaction
    @Query("SELECT * FROM Invoices WHERE id = :invoiceId")
    suspend fun getInvoiceById(invoiceId: Int): FullInvoice

    // Insert
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


    // Update Invoice
    suspend fun updateInvoice(newInvoice: FullInvoice) {

        val oldInvoice = getInvoiceById(newInvoice.invoice.id)
        updateInvoice(newInvoice.invoice)

        val newItems = newInvoice.items.toMutableList()
        val oldItems = oldInvoice.items.toMutableList()

        newItems.forEach { newItem ->

            //
            insertInvoiceItem(newItem)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                oldItems.removeIf {
                    it.itemId == newItem.itemId
                }
            }

        }

        // the remaining items are removed from the newInvoice
        oldItems.forEach { oldItem ->
            deleteInvoiceItem(oldItem)
        }

    }

    @Update
    suspend fun updateInvoice(invoiceEntity: InvoiceEntity)

    // Delete
    suspend fun deleteInvoices(invoices: List<FullInvoice>) = invoices.forEach { deleteInvoice(it) }

    suspend fun deleteInvoice(fullInvoice: FullInvoice) {
        fullInvoice.apply {
            deleteInvoice(invoice)
            deleteInvoiceItems(invoice.id)
        }
    }

    @Delete
    suspend fun deleteInvoice(invoiceEntity: InvoiceEntity)

    @Query("DELETE FROM InvoiceItems WHERE invoiceId = :invoiceId")
    suspend fun deleteInvoiceItems(invoiceId: Int)

    @Delete
    suspend fun deleteInvoiceItem(invoiceItemEntity: InvoiceItemEntity)

}