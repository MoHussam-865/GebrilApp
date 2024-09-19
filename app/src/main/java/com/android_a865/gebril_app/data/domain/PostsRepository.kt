package com.android_a865.gebril_app.data.domain

import com.android_a865.gebril_app.data.entities.Post
import kotlinx.coroutines.flow.Flow

interface PostsRepository {

    fun getPosts(): Flow<List<Post>>

    suspend fun insertPost(post: Post)

    suspend fun deletePost()

}