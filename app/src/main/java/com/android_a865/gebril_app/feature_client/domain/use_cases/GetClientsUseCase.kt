package com.android_a865.gebril_app.feature_client.domain.use_cases

import android.util.Log
import com.android_a865.gebril_app.feature_client.data.mapper.toClient
import com.android_a865.gebril_app.feature_client.domain.model.Client
import com.android_a865.gebril_app.feature_client.domain.repository.ClientsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import java.util.*

class GetClientsUseCase(
    private val repository: ClientsRepository
) {
    operator fun invoke(searchQuery: String = ""): Flow<List<Client>> {
        var clients = repository.getClients().map { clients ->
            clients.map { client ->
                client.toClient()
            }
        }

        if (searchQuery.isNotBlank()) {

            clients  = clients.map { clients0 ->
                clients0.filter { client ->
                    client.name.toLowerCase(Locale.ROOT).contains(
                        searchQuery.toLowerCase(Locale.ROOT)
                    ) ||

                            client.org?.toLowerCase(Locale.ROOT)?.contains(
                                searchQuery.toLowerCase(Locale.ROOT)
                            ) ?: false ||

                            client.title?.toLowerCase(Locale.ROOT)?.contains(
                                searchQuery.toLowerCase(Locale.ROOT)
                            ) ?: false ||

                            client.phone1?.toLowerCase(Locale.ROOT)?.contains(
                                searchQuery.toLowerCase(Locale.ROOT)
                            ) ?: false ||

                            client.phone2?.toLowerCase(Locale.ROOT)?.contains(
                                searchQuery.toLowerCase(Locale.ROOT)
                            ) ?: false ||

                            client.email?.toLowerCase(Locale.ROOT)?.contains(
                                searchQuery.toLowerCase(Locale.ROOT)
                            ) ?: false ||

                            client.address?.toLowerCase(Locale.ROOT)?.contains(
                                searchQuery.toLowerCase(Locale.ROOT)
                            ) ?: false
                }
            }
        }

        return clients
    }

}