package com.android_a865.gebril_app.feature_main.domain.use_cases.items_use_cases

import com.android_a865.gebril_app.feature_main.data.mapper.toInvoiceItems
import com.android_a865.gebril_app.feature_main.domain.model.InvoiceItem
import com.android_a865.gebril_app.feature_main.domain.repository.ItemsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetInvoiceItemsUseCase(
    val repository: ItemsRepository
) {
    operator fun invoke(path: String): Flow<List<InvoiceItem>> {
        return repository.getItems(path).map {
            it.toInvoiceItems()
        }
    }
}