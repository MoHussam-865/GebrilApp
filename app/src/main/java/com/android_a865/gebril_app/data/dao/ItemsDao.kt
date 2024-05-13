package com.android_a865.gebril_app.data.dao

import androidx.room.*
import com.android_a865.gebril_app.data.entities.Item
import kotlinx.coroutines.flow.Flow


@Dao
interface ItemsDao {

    fun getItems(path: String = "", search: String = ""): Flow<List<Item>> {
        return if (search.isBlank()) getItemsEntity(path)
        else getItemsEntity(path, search)
    }

    @Query(
        """SELECT * FROM Items
                    WHERE path = :path AND (name LIKE '%'|| :search ||'%')
                    ORDER BY is_folder DESC"""
    )
    fun getItemsEntity(path: String, search: String): Flow<List<Item>>


    @Query("SELECT * FROM Items WHERE path = :path ORDER BY is_folder DESC")
    fun getItemsEntity(path: String): Flow<List<Item>>

    @Query("SELECT * FROM Items WHERE path LIKE :path || '%'")
    suspend fun getSubItems(path: String): List<Item>

    @Query("SELECT * FROM Items WHERE path = :path")
    suspend fun getItemFriends(path: String): List<Item>


    @Query("SELECT * FROM Items WHERE path = :path AND name = :name")
    suspend fun getItemEntity(name: String, path: String): Item

    // delete Item
    @Delete
    suspend fun deleteItemEntity(itemEntity: Item)

    // insert Item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemEntity(itemEntity: Item): Long
}