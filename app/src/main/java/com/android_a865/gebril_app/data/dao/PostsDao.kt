package com.android_a865.gebril_app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android_a865.gebril_app.data.entities.Post
import kotlinx.coroutines.flow.Flow


@Dao
interface PostsDao {

    @Query("SELECT * FROM Post")
    fun getPosts(): Flow<List<Post>>

    @Query("DELETE FROM Post")
    suspend fun deletePost()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: Post)

}