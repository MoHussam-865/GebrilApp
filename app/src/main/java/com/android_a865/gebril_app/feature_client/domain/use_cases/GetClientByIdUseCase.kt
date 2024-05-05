package com.android_a865.gebril_app.feature_client.domain.use_cases

import com.android_a865.gebril_app.feature_client.data.mapper.toClient
import com.android_a865.gebril_app.feature_client.domain.model.Client
import com.android_a865.gebril_app.feature_client.domain.repository.ClientsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetClientByIdUseCase(
    private val repository: ClientsRepository
) {
    operator fun invoke(id: Int): Flow<Client?> {
        return repository.getClientById(id).map {
            it?.toClient()
        }
    }
}