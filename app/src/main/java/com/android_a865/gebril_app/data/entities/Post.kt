package com.android_a865.gebril_app.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Post(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val content: String,
    val imageUrl: String? = null,
    var imageAbsolutePath: String? = null,

) {
    val imagePath get(): String = "posts/post-$id.jpg"
}
