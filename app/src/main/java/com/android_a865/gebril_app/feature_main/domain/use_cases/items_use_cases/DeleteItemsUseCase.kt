package com.android_a865.gebril_app.feature_main.domain.use_cases.items_use_cases

import com.android_a865.gebril_app.feature_main.data.mapper.toEntities
import com.android_a865.gebril_app.feature_main.domain.model.Item
import com.android_a865.gebril_app.feature_main.domain.repository.ItemsRepository

class DeleteItemsUseCase(
    val repository: ItemsRepository
) {
    suspend operator fun invoke(items: List<Item>) {
        repository.deleteItems(items.toEntities())
    }
}