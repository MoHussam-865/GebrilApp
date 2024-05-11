package com.android_a865.gebril_app.data.repository

import com.android_a865.gebril_app.data.dao.ItemsDao
import com.android_a865.gebril_app.data.domain.Item
import com.android_a865.gebril_app.data.entities.ItemEntity
import com.android_a865.gebril_app.data.mapper.toEntity
import com.android_a865.gebril_app.data.mapper.toInvoiceItems
import com.android_a865.gebril_app.feature_main.domain.repository.ItemsRepository
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemsRepositoryImpl @Inject constructor(
    private val dao: ItemsDao,
): ItemsRepository {

    override fun getItems(path: String) = dao.getItemsEntity(path).map {
        it.toInvoiceItems()
    }

    override suspend fun getFolderSubItems(path: String) = dao.getSubItems(path)

    override suspend fun getItemFriends(path: String) = dao.getItemFriends(path)

    override suspend fun insertItem(item: Item) = dao.insertItemEntity(item.toEntity())

    override suspend fun deleteItems(items: List<ItemEntity>) =
        items.forEach { dao.deleteItemEntity(it) }

    override suspend fun deleteItem(item: Item) =
        dao.deleteItemEntity(item.toEntity())

}