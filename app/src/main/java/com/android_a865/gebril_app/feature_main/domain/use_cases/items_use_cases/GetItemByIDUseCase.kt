package com.android_a865.gebril_app.feature_main.domain.use_cases.items_use_cases

import com.android_a865.gebril_app.feature_main.data.mapper.toItem
import com.android_a865.gebril_app.feature_main.domain.model.Item
import com.android_a865.gebril_app.feature_main.domain.repository.ItemsRepository

class GetItemByIDUseCase(
    val repository: ItemsRepository
) {
    suspend operator fun invoke(id: Int): Item {
        return repository.getItemById(id).toItem()
    }
}