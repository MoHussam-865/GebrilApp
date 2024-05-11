package com.android_a865.gebril_app.data.dao

import androidx.room.*
import com.android_a865.gebril_app.data.entities.ItemEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface ItemsDao {

    fun getItems(path: String = "", search: String = ""): Flow<List<ItemEntity>> {
        return if (search.isBlank()) getItemsEntity(path)
        else getItemsEntity(path, search)
    }

    @Query(
        """SELECT * FROM Items
                    WHERE path = :path AND (name LIKE '%'|| :search ||'%')
                    ORDER BY isFolder DESC"""
    )
    fun getItemsEntity(path: String, search: String): Flow<List<ItemEntity>>


    @Query("SELECT * FROM Items WHERE path = :path ORDER BY isFolder DESC")
    fun getItemsEntity(path: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM Items WHERE path LIKE :path || '%'")
    suspend fun getSubItems(path: String): List<ItemEntity>

    @Query("SELECT * FROM Items WHERE path = :path")
    suspend fun getItemFriends(path: String): List<ItemEntity>


    @Query("SELECT * FROM Items WHERE path = :path AND name = :name")
    suspend fun getItemEntity(name: String, path: String): ItemEntity

    // delete Item
    @Delete
    suspend fun deleteItemEntity(itemEntity: ItemEntity)

    // insert Item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemEntity(itemEntity: ItemEntity): Long
}