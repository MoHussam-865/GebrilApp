package com.android_a865.gebril_app.data.repository

import com.android_a865.gebril_app.data.dao.ItemsDao
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.data.entities.Item
import com.android_a865.gebril_app.data.mapper.toInvoiceItems
import com.android_a865.gebril_app.data.domain.ItemsRepository
import com.android_a865.gebril_app.data.entities.InvoiceItemEntity
import com.android_a865.gebril_app.data.mapper.toInvoiceItem
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemsRepositoryImpl @Inject constructor(
    private val dao: ItemsDao,
): ItemsRepository {

    override fun getItems(parentId: Int) = dao.getItemsEntity(parentId).map {
        it.toInvoiceItems()
    }

    override suspend fun getFolderSubItems(parentId: Int) = dao.getSubItems(parentId)

    override suspend fun getItemFriends(parentId: Int) = dao.getItemFriends(parentId)

    override suspend fun insertItem(item: Item) = dao.insertItemEntity(item)

    override suspend fun deleteItem(item: Item) =
        dao.deleteItemEntity(item)

    override suspend fun getItemsById(items: List<InvoiceItemEntity>): List<InvoiceItem> {
        return items.map { item ->
            val myItem = dao.getItemById(item.itemId)
            val discount = dao.getFolderDiscount(myItem.parentId)

            myItem.toInvoiceItem().copy(
                discount = discount,
                fullName = item.fullName,
                qty = item.qty
            )
        }
    }



    override suspend fun getDiscount(parentId: Int): Double {
        return dao.getDiscount(parentId)
    }

    override suspend fun getItemImageUrl(item: Item): String? {
        return dao.getItemById(item.id).imageUrl
    }

}