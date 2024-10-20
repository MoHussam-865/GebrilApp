package com.android_a865.gebril_app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.data.entities.InvoiceItemEntity
import com.android_a865.gebril_app.data.entities.Item
import com.android_a865.gebril_app.data.MyRoomDatabase.Companion.DATABASE_VERSION
import com.android_a865.gebril_app.data.dao.CartDao
import com.android_a865.gebril_app.data.dao.InvoicesDao
import com.android_a865.gebril_app.data.dao.ItemsDao
import com.android_a865.gebril_app.data.dao.PostsDao
import com.android_a865.gebril_app.data.entities.CartItem
import com.android_a865.gebril_app.data.entities.Post

@Database(
    entities = [
        Item::class,
        InvoiceItemEntity::class,
        Invoice::class,
        Post::class,
        CartItem::class
               ],
    version = DATABASE_VERSION,
    exportSchema = false
)

abstract class MyRoomDatabase: RoomDatabase() {

    abstract fun getItemsDao(): ItemsDao

    abstract fun getInvoicesDao(): InvoicesDao

    abstract fun getPostsDao(): PostsDao

    abstract  fun getCartDao(): CartDao


    companion object {
        // Room Database
        const val DATABASE_NAME = "InvoiceMaster.db"
        const val DATABASE_VERSION = 4
    }
}