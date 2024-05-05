package com.android_a865.gebril_app.feature_main.domain.use_cases.items_use_cases

import com.android_a865.gebril_app.feature_main.data.mapper.toItems
import com.android_a865.gebril_app.feature_main.domain.model.Item
import com.android_a865.gebril_app.feature_main.domain.repository.ItemsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetItemsUseCase(
    val repository: ItemsRepository
) {

    operator fun invoke(path: String): Flow<List<Item>> {
        return repository.getItems(path).map {
            it.toItems()
        }
    }

}