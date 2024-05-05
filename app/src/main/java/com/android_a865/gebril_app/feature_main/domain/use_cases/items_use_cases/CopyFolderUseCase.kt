package com.android_a865.gebril_app.feature_main.domain.use_cases.items_use_cases

import android.util.Log
import com.android_a865.gebril_app.feature_main.data.mapper.toEntity
import com.android_a865.gebril_app.feature_main.domain.model.Item
import com.android_a865.gebril_app.feature_main.domain.repository.ItemsRepository

class CopyFolderUseCase(
    val repository: ItemsRepository
) {

    suspend operator fun invoke(items: List<Item>) {

        items.forEach { item ->

            /**
             * change "name" to "name (copy)" and insert
             */
            val newName = repository.getAllowedName(item)
            val newItem = item.copy(id = 0, name = newName)
            val oldItemNameAsFolder = item.nameAsFolder
            val newItemNameAsFolder = newItem.nameAsFolder
            repository.insertItem(newItem.toEntity())

            if (item.isFolder) {

                /**
                 * this item is folder
                 *  items/parent/anotherOne  -> name
                 *
                 *  sub items
                 *   items/parent/anotherOne/name
                 *
                 *   new items must replace
                 *
                 *   items/parent/anotherOne/name   nameAsFolder
                 *
                 *   items/parent/anotherOne/newName nameAsFolder
                 *
                 */

                // get all items inside the folder
                val subItems = repository.getFolderSubItems(oldItemNameAsFolder)

                Log.d("AllowedName", subItems.size.toString())


                subItems.forEach {

                    val newPath = it.path.replaceFirst(
                        oldItemNameAsFolder,
                        newItemNameAsFolder
                    )

                    repository.insertItem(it.copy(id = 0, path = newPath))
                }
            }
        }

    }
}