package com.android_a865.gebril_app.feature_settings.domain.repository

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.android_a865.gebril_app.feature_client.data.entities.ClientEntity
import com.android_a865.gebril_app.feature_main.data.entities.InvoiceEntity
import com.android_a865.gebril_app.feature_main.data.entities.InvoiceItemEntity
import com.android_a865.gebril_app.feature_main.data.entities.ItemEntity
import com.android_a865.gebril_app.feature_main.data.relations.FullInvoice

interface ImportExportRepository {

    // Exporting
    suspend fun getAllInvoices(): List<FullInvoice>

    suspend fun getAllClients(): List<ClientEntity>

    suspend fun getAllItems(): List<ItemEntity>



    // Importing
    suspend fun insertInvoice(fullInvoice: FullInvoice)

    suspend fun insertItemEntity(itemEntity: ItemEntity)

    suspend fun insertClient(clientEntity: ClientEntity)


}