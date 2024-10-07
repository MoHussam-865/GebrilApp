package com.android_a865.gebril_app.utils

import android.content.Context
import android.os.NetworkOnMainThreadException
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.android_a865.gebril_app.data.domain.Message
import com.android_a865.gebril_app.data.domain.repo.ItemRepo
import com.android_a865.gebril_app.data.domain.repo.PostRepo
import com.android_a865.gebril_app.external_api.ItemsApi
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepo
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject


class HandleApi @Inject constructor(
    private val context: Context,
    private val settingsRepo: SettingsRepo,
    private val itemsApi: ItemsApi,
    private val itemRepo: ItemRepo,
    private val postRepo: PostRepo
) {

    suspend fun updateDatabase() {
        val mySettings = settingsRepo.getAppSetting()

        /** get the response from the server to update the database with the latest prices */
        val response = try {
            itemsApi.getItems(mySettings.lastUpdate)
        } catch (e: IOException) {
            Log.d("my error", e.message.toString())
            throw Exception("Server Response Error Estimates might be inaccurate")
        } catch (e: HttpException) {
            Log.d("my error", e.message.toString())
            throw Exception("Server Response Error Estimates might be inaccurate")
        }

        Log.d("my error", response.toString())
        if (response.isSuccessful && response.body() != null) {
            /** handel the response */
            handleResponse(message = response.body()!!, settings = mySettings,)
        } else {
            throw Exception("Server Response Error Estimates might be inaccurate")
            // use existing database
        }
    }


    /** handle the server response */
    private suspend fun handleResponse(
        message: Message,
        settings: AppSettings,
    ) {

        /** save the posts */
        message.posts?.let { posts ->
            if (posts.isNotEmpty()) {
                posts.forEach { post ->

                    if (post.imageName != null) {
                        /** download & save the image */
                        val absolutePath = downloadImage(imageName = post.imageName, imagePath = post.postsPath)

                        post.imagePath = absolutePath
                    }

                    /** insert the post */
                    postRepo.insertPost(post)
                }
            }
        }

        /** handle the items prices changes
         * only items that have price changes are received
         * */
        message.items?.let { items ->
            if (items.isNotEmpty()) {
                items.forEach { item ->

                    if (item.isDeleted) {
                        itemRepo.deleteItem(item)
                    } else {

                        /** get the item image if needed */
                        // check if the item already have an image
                        // if the imageUrl is not null

                        if (item.imageName != null) {
                            // if the imageUrl is different from the one in the local database
                            //val myItem = itemsRepository.getItemById(item.id)
                            // TODO add condition do not download every time
                            /** download & save the image */
                            val absolutePath =
                                downloadImage(imageName = item.imageName, imagePath = item.itemsPath)

                            item.imagePath = absolutePath

                        }

                        /** insert the item */
                        itemRepo.insertItem(item)
                    }
                }

                /** update last_update & last_update_date */
                val lastUpdate = items.maxOf { it.lastUpdate }
                settingsRepo.updateSettings(
                    settings.copy(
                        lastUpdate = lastUpdate,
                        lastUpdateDate = System.currentTimeMillis()
                    )
                )
            }
        }
        val updates = (message.posts?.size ?: 0) + (message.items?.size ?: 0)
        throw Exception("Successfully Updated $updates")
    }


    /** downloads and save the image */
    private suspend fun downloadImage(
        imageName: String,
        imagePath: String,
    ): String? {

        /** here we download the image */
        val response = try {
            itemsApi.downloadImage(imageName)
        } catch (e: IOException) {
            Log.d("items update", e.message.toString())
            return null
        } catch (e: HttpException) {
            Log.d("items update", e.message.toString())
            return null
        }

        // image downloaded
        if (response.isSuccessful) {
            response.body()?.let { body ->


                val imageFolder = getFile(context, imagePath)
                val imageFile = File(imageFolder.absolutePath, imageName)


                try {
                    /** saving the image */
                    val inputStream = body.byteStream()
                    val fileOutputStream = FileOutputStream(imageFile)
                    val buffer = ByteArray(4096)

                    var byteRead: Int
                    while (inputStream.read(buffer).also { byteRead = it } != -1) {
                        fileOutputStream.write(buffer, 0, byteRead)
                    }

                    fileOutputStream.flush()
                    fileOutputStream.close()
                    inputStream.close()

                    return imageFile.absolutePath
                } catch (e: NetworkOnMainThreadException) {
                    Log.d("items update", "do not download on main thread")
                }

            }
        } else {
            Log.d("items update", "Image download response unsuccessful")
        }
        return null
    }

    private fun getFile(context: Context, fileName: String): File {
        val imageFile = File(context.filesDir, fileName)
        if (!imageFile.exists()) {
            imageFile.mkdirs()
        }
        return imageFile
    }
}
