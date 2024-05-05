package com.android_a865.gebril_app.feature_client.data.repository

import com.android_a865.gebril_app.feature_client.data.dao.ClientsDao
import com.android_a865.gebril_app.feature_client.data.entities.ClientEntity
import com.android_a865.gebril_app.feature_client.domain.repository.ClientsRepository
import kotlinx.coroutines.flow.Flow

class ClientsRepositoryImpl(
    private val dao: ClientsDao
): ClientsRepository {

    override fun getClients(): Flow<List<ClientEntity>> {
        return dao.getClientsEntity()
    }

    override fun getClientById(id: Int): Flow<ClientEntity?> {
        return dao.getClientById(id)
    }

    override suspend fun insertClient(client: ClientEntity) {
        dao.insertClient(client)
    }

    override suspend fun deleteClient(client: ClientEntity) {
        dao.deleteClient(client)
    }

    override suspend fun updateInvoicesClient(clientId: Int, client: String) {
        dao.updateInvoicesClient(clientId, client)
    }
}