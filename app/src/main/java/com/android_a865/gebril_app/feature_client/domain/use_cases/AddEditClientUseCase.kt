package com.android_a865.gebril_app.feature_client.domain.use_cases

import com.android_a865.gebril_app.feature_client.data.mapper.toEntity
import com.android_a865.gebril_app.feature_client.domain.model.Client
import com.android_a865.gebril_app.feature_client.domain.repository.ClientsRepository
import com.android_a865.gebril_app.utils.toJson

class AddEditClientUseCase(
    private val repository: ClientsRepository
) {
    suspend operator fun invoke(client: Client) {
        repository.insertClient(client.toEntity())

        // client is updated and we want to update all his invoices
        if (client.id != 0) {
            repository.updateInvoicesClient(client.id, client.toJson())
        }
    }
}