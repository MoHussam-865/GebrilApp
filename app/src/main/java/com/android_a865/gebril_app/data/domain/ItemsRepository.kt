package com.android_a865.gebril_app.data.domain

import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.data.entities.Item
import kotlinx.coroutines.flow.Flow

interface ItemsRepository {

    fun getItems(path: String): Flow<List<InvoiceItem>>

    suspend fun getFolderSubItems(path: String): List<Item>

    suspend fun getItemFriends(path: String): List<Item>

    suspend fun insertItem(item: Item): Long

    suspend fun deleteItem(item: Item)
}