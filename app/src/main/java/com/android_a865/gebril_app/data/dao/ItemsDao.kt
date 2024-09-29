package com.android_a865.gebril_app.data.dao

import androidx.room.*
import com.android_a865.gebril_app.data.entities.Item
import kotlinx.coroutines.flow.Flow


@Dao
interface ItemsDao {

    fun getItems(parentId: Int = 0, search: String = ""): Flow<List<Item>> {
        return if (search.isBlank()) getItemsEntity(parentId)
        else getItemsEntity(parentId, search)
    }

    @Query(
        """SELECT * FROM Items
                    WHERE parentId = :parentId AND (name LIKE '%'|| :search ||'%')
                    ORDER BY isFolder DESC"""
    )
    fun getItemsEntity(parentId: Int, search: String): Flow<List<Item>>



    @Query("SELECT * FROM Items WHERE parentId = :parentId ORDER BY isFolder DESC")
    fun getItemsEntity(parentId: Int): Flow<List<Item>>

    @Query("SELECT * FROM Items WHERE parentId LIKE :parentId || '%'")
    suspend fun getSubItems(parentId: Int): List<Item>

    @Query("SELECT * FROM Items WHERE parentId = :parentId")
    suspend fun getItemFriends(parentId: Int): List<Item>

    @Query("SELECT discount FROM Items WHERE id = :parentId")
    suspend fun getDiscount(parentId: Int): Double

//    @Query("SELECT * FROM Items WHERE parentId = :path AND name = :name")
//    suspend fun getItemEntity(name: String, path: String): Item

    // get one item
    @Query("SELECT * FROM Items WHERE id = :id")
    suspend fun getItemById(id: Int): Item


    @Query("SELECT discount FROM Items WHERE id = :folderId")
    suspend fun getFolderDiscount(folderId: Int): Double

    // delete Item
    @Delete
    suspend fun deleteItemEntity(itemEntity: Item)

    // insert Item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemEntity(itemEntity: Item): Long
}