package com.android_a865.gebril_app.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "Items")
data class Item(
    @PrimaryKey
    val id: Int,
    val name: String,
    val description: String?,
    val price: Double,
    val discount: Double,
    val parentId: Int,  // parent name
    val path: String,
    val isFolder: Boolean,
    val lastUpdate: Int,

    // the name sent by the server
    val imageName: String? = null,
    // the image location on the device
    var imagePath: String? = null,

    @Ignore
    val isDeleted: Boolean = false
) {
    // item images folder path
    val itemsPath get() = "items"

    constructor(
        id: Int,
        name: String,
        description: String?,
        price: Double,
        discount: Double,
        parentId: Int,  // parent name
        path: String,
        isFolder: Boolean,
        lastUpdate: Int,
        imageName: String?,
        imagePath: String?,
    ) : this(
        id,
        name,
        description,
        price,
        discount,
        parentId,
        path,
        isFolder,
        lastUpdate,
        imageName,
        imagePath,
        false
    )

}
