package com.android_a865.gebril_app.feature_main.data.repository

import android.util.Log
import com.android_a865.gebril_app.feature_main.data.dao.ItemsDao
import com.android_a865.gebril_app.feature_main.data.entities.ItemEntity
import com.android_a865.gebril_app.feature_main.domain.model.Item
import com.android_a865.gebril_app.feature_main.domain.repository.ItemsRepository
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemsRepositoryImpl @Inject constructor(
    private val dao: ItemsDao,
): ItemsRepository {

    override fun getItems(path: String) = dao.getItemsEntity(path)

    override suspend fun getFolderSubItems(path: String) = dao.getSubItems(path)

    override suspend fun getItemFriends(path: String) = dao.getItemFriends(path)

    override suspend fun getItemById(id: Int) = dao.getItemByID(id)

    override suspend fun insertItem(item: ItemEntity) = dao.insertItemEntity(item)

    override suspend fun getAllowedName(item: Item): String {
        var itemName = item.name
        val items = getItemFriends(item.path.path).map { it.name }

        items.forEach {
            Log.d("AllowedName", it)
        }

        while (true) {
            delay(10)
            if (itemName in items) {
                itemName += " (copy)"
            } else {
                return itemName
            }
        }
    }

    override suspend fun updateItem(item: ItemEntity) = dao.updateItem(item)

    override suspend fun deleteItems(items: List<ItemEntity>) = dao.deleteItems(items)

}