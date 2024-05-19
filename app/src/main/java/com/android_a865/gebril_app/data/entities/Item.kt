package com.android_a865.gebril_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Items")
data class Item(
    @PrimaryKey
    val id: Int,
    val name: String,
    val price: Double,
    val discount: Double,
    val parentId: Int,  // parent name
    val is_folder: Boolean,
    val last_update: Int,
)
