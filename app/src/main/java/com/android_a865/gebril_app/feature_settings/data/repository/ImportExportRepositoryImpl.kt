package com.android_a865.gebril_app.feature_settings.data.repository

import com.android_a865.gebril_app.feature_client.data.entities.ClientEntity
import com.android_a865.gebril_app.feature_settings.data.dao.ImportExportDao
import com.android_a865.gebril_app.feature_settings.domain.repository.ImportExportRepository
import com.android_a865.gebril_app.feature_main.data.entities.ItemEntity
import com.android_a865.gebril_app.feature_main.data.relations.FullInvoice

class ImportExportRepositoryImpl(
    private val dao: ImportExportDao
): ImportExportRepository {

    override suspend fun getAllInvoices(): List<FullInvoice> {
        return dao.getAllInvoices()
    }

    override suspend fun getAllClients(): List<ClientEntity> {
        return dao.getAllClients()
    }

    override suspend fun getAllItems(): List<ItemEntity> {
        return dao.getAllItems()
    }

    override suspend fun insertInvoice(fullInvoice: FullInvoice) {
        dao.insertInvoice(fullInvoice)
    }

    override suspend fun insertItemEntity(itemEntity: ItemEntity) {
        dao.insertItemEntity(itemEntity)
    }

    override suspend fun insertClient(clientEntity: ClientEntity) {
        dao.insertClient(clientEntity)
    }
}