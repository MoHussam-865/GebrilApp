package com.android_a865.gebril_app.feature_main.data.dao

import androidx.room.*
import com.android_a865.gebril_app.feature_main.data.entities.ItemEntity
import com.android_a865.gebril_app.utils.Path
import kotlinx.coroutines.flow.Flow


@Dao
interface ItemsDao {

    fun getItems(path: String = "", search: String = ""): Flow<List<ItemEntity>> {
        return if (search.isBlank()) getItemsEntity(path)
        else getItemsEntity(path, search)
    }

    @Query("SELECT * FROM Items WHERE path = :path ORDER BY isFolder DESC")
    fun getItemsEntity(path: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM Items WHERE path LIKE :path || '%'")
    suspend fun getSubItems(path: String): List<ItemEntity>

    @Query("SELECT * FROM Items WHERE path = :path")
    suspend fun getItemFriends(path: String): List<ItemEntity>


    @Query(
        """SELECT * FROM Items
                    WHERE path = :path AND (name LIKE '%'|| :search ||'%')
                    ORDER BY isFolder DESC"""
    )
    fun getItemsEntity(path: String, search: String): Flow<List<ItemEntity>>

    @Query("SELECT * FROM Items WHERE path = :path AND name = :name")
    suspend fun getItemEntity(name: String, path: String): ItemEntity

    // copy and move items
    suspend fun copyItem(item: ItemEntity, path: String) {

        insertItemEntity(item.copy(path = path))
        // if it's a folder copy all sub-items
        if (item.isFolder) {
            // get all the sub-items
            val items = getFolderContent(item.path)
            items.forEach { subItem ->
                val itemNewPath = subItem.path.replace(item.path, path)
                val newItem = subItem.copy(path = itemNewPath)
                insertItemEntity(newItem)
            }
        }

    }

    suspend fun moveItem(item: ItemEntity, path: String) {

        insertItemEntity(item.copy(path = path))
        // if it's a folder move all sub-items
        if (item.isFolder) {
            // get all the sub-items
            val items = getFolderContent(item.path)
            items.forEach { subItem ->
                val itemNewPath = subItem.path.replace(item.path, path)
                val newItem = subItem.copy(path = itemNewPath)
                insertItemEntity(newItem)
            }
        }

    }

    suspend fun copyItems(items: List<ItemEntity>, path: String) =
        items.forEach { copyItem(it, path) }

    suspend fun moveItems(items: List<ItemEntity>, path: String) =
        items.forEach { moveItem(it, path) }


    // update Item
    suspend fun updateItems(items: List<ItemEntity>) = items.forEach { updateItem(it) }

    suspend fun updateItem(newItem: ItemEntity) {

        // if it's a folder get it's old data before editing
        if (newItem.isFolder) {
            val oldItem = getItemByID(newItem.id)
            insertItemEntity(newItem)

            // if the path has changed or the name has changed
            if (newItem.path != oldItem.path || newItem.name != oldItem.name) {

                val oldPath = oldItem.run { Path(path).pathOf(name) }
                val newPath = newItem.run { Path(path).pathOf(name) }

                // get all the items that has the old path that is no longer exist
                val items = getFolderContent(oldPath)
                items.forEach { item ->
                    val itemNewPath = item.path.replace(oldPath, newPath)
                    insertItemEntity(item.copy(path = itemNewPath))
                }
            }

        }

        // if it's not a folder update it
        else insertItemEntity(newItem)
    }

    @Query("SELECT * FROM Items WHERE path LIKE :oldPath || '%'")
    suspend fun getFolderContent(oldPath: String): List<ItemEntity>

    @Query("SELECT * FROM Items WHERE id = :id")
    suspend fun getItemByID(id: Int): ItemEntity


    // delete Item
    suspend fun deleteItems(items: List<ItemEntity>) = items.forEach { deleteItem(it) }
    suspend fun deleteItem(item: ItemEntity) {
        deleteItemEntity(item)
        if (item.isFolder) {
            deleteFolderContent(item.run { Path(path).pathOf(name) })
        }
    }

    @Delete
    suspend fun deleteItemEntity(itemEntity: ItemEntity)

    @Query("DELETE FROM Items WHERE path LIKE :path || '%' ")
    suspend fun deleteFolderContent(path: String)


    // insert Item
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItemEntity(itemEntity: ItemEntity): Long
}