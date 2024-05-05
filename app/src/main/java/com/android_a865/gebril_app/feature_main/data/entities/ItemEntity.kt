package com.android_a865.gebril_app.feature_main.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val path: String = ".",
    val price: Double = 0.0,
    val isFolder: Boolean = false
)