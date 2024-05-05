package com.android_a865.gebril_app.feature_client.data.dao

import androidx.room.*
import com.android_a865.gebril_app.feature_client.data.entities.ClientEntity
import com.android_a865.gebril_app.feature_client.domain.model.Client
import com.android_a865.gebril_app.feature_main.data.entities.InvoiceEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ClientsDao {

    @Query("SELECT * FROM Clients ORDER BY id DESC")
    fun getClientsEntity(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM Clients WHERE id = :id")
    fun getClientById(id: Int): Flow<ClientEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(clientEntity: ClientEntity)

    @Delete
    suspend fun deleteClient(clientEntity: ClientEntity)

    suspend fun updateInvoicesClient(clientId: Int, client: String) {
        getInvoicesWithClientId(clientId).forEach {
            updateInvoice(
                it.copy(
                    client = client
                )
            )
        }
    }

    @Query("SELECT * FROM Invoices WHERE clientId = :id")
    suspend fun getInvoicesWithClientId(id: Int): List<InvoiceEntity>

    @Update
    suspend fun updateInvoice(invoiceEntity: InvoiceEntity)
}