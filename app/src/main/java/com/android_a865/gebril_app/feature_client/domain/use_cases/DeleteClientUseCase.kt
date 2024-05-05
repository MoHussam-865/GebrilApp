package com.android_a865.gebril_app.feature_client.domain.use_cases

import com.android_a865.gebril_app.feature_client.data.mapper.toEntity
import com.android_a865.gebril_app.feature_client.domain.model.Client
import com.android_a865.gebril_app.feature_client.domain.repository.ClientsRepository

class DeleteClientUseCase(
    private val repository: ClientsRepository
) {
    suspend operator fun invoke(client: Client?) {
        client?.let {
            repository.deleteClient(it.toEntity())
        }
    }
}