package com.android_a865.gebril_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val content: String,
    val imageName: String? = null,
    var imagePath: String? = null
) {
    val postsPath get(): String = "posts/$imageName"
}
