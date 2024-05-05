package com.android_a865.gebril_app.feature_settings.domain.use_cases

import com.android_a865.gebril_app.feature_settings.domain.models.DatabaseHolder
import com.android_a865.gebril_app.feature_settings.domain.repository.ImportExportRepository
import com.android_a865.gebril_app.utils.toJson

class ExportUseCase(
    private val repository: ImportExportRepository
) {

    suspend operator fun invoke(): String {

        val invoices = repository.getAllInvoices()
        val clients = repository.getAllClients()
        val items = repository.getAllItems()

        return DatabaseHolder(
            invoices = invoices,
            items = items,
            clients = clients
        ).toJson()

    }

}