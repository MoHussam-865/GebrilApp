package com.android_a865.gebril_app.data.domain

import com.android_a865.gebril_app.data.entities.InvoiceItemEntity
import com.android_a865.gebril_app.data.entities.Item
import kotlinx.coroutines.flow.Flow

interface ItemsRepository {

    fun getItems(parentId: Int): Flow<List<InvoiceItem>>

    suspend fun getFolderSubItems(parentId: Int): List<Item>

    suspend fun getItemFriends(parentId: Int): List<Item>

    suspend fun insertItem(item: Item): Long

    suspend fun deleteItem(item: Item)

    suspend fun getItemsById(items: List<InvoiceItemEntity>): List<InvoiceItem>
}