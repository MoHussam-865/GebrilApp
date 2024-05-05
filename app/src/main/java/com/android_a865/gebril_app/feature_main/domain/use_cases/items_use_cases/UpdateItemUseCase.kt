package com.android_a865.gebril_app.feature_main.domain.use_cases.items_use_cases

import com.android_a865.gebril_app.feature_main.data.mapper.toEntity
import com.android_a865.gebril_app.feature_main.domain.model.Item
import com.android_a865.gebril_app.feature_main.domain.repository.ItemsRepository

class UpdateItemUseCase(
    val repository: ItemsRepository
) {
    suspend operator fun invoke(item: Item) {
        repository.updateItem(item.toEntity())
    }
}