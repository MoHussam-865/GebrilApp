package com.android_a865.gebril_app.feature_main.domain.repository

import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.data.domain.Item
import com.android_a865.gebril_app.data.entities.ItemEntity
import kotlinx.coroutines.flow.Flow

interface ItemsRepository {

    fun getItems(path: String): Flow<List<InvoiceItem>>

    suspend fun getFolderSubItems(path: String): List<ItemEntity>

    suspend fun getItemFriends(path: String): List<ItemEntity>

    suspend fun insertItem(item: Item): Long

    suspend fun deleteItems(items: List<ItemEntity>)

    suspend fun deleteItem(item: Item)
}