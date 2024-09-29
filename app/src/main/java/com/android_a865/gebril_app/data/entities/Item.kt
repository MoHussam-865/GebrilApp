package com.android_a865.gebril_app.data.entities

import androidx.room.Entity
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
    val isFolder: Boolean,
    val lastUpdate: Int,

    val imageName: String? = null,
    var imagePath: String? = null,
) {
    val itemsPath get(): String = "items/$imageName"
}
