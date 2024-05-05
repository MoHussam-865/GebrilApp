package com.android_a865.gebril_app.feature_client.domain.use_cases

data class ClientsUseCases(
    val getClients: GetClientsUseCase,
    val addEditClient: AddEditClientUseCase,
    val deleteClient: DeleteClientUseCase,
    val getClient: GetClientByIdUseCase
)