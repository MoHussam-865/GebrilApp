package com.android_a865.gebril_app.data.domain.repo

import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.data.entities.InvoiceItemEntity
import com.android_a865.gebril_app.data.entities.Item
import kotlinx.coroutines.flow.Flow

interface ItemRepo {

    fun getItems(parentId: Int): Flow<List<InvoiceItem>>

    suspend fun getFolderSubItems(parentId: Int): List<Item>

    suspend fun getItemFriends(parentId: Int): List<Item>

    suspend fun insertItem(item: Item): Long

    suspend fun deleteItem(item: Item)

    suspend fun getItemsById(items: List<InvoiceItemEntity>): List<InvoiceItem>

    suspend fun getItemById(id: Int): Item

    suspend fun getDiscount(parentId: Int): Double

    suspend fun getItemImageUrl(item: Item): String?

}