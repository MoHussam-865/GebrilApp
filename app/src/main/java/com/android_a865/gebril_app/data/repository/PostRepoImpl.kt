package com.android_a865.gebril_app.data.repository

import com.android_a865.gebril_app.data.dao.PostsDao
import com.android_a865.gebril_app.data.domain.repo.PostRepo
import com.android_a865.gebril_app.data.entities.Post
import kotlinx.coroutines.flow.Flow

class PostRepoImpl(
    private val dao: PostsDao
) : PostRepo {

    override fun getPosts(): Flow<List<Post>> {
        return dao.getPosts()
    }

    override suspend fun insertPost(post: Post) {
        dao.insertPost(post)
    }

    override suspend fun deletePost() {
        dao.deletePost()
    }
}