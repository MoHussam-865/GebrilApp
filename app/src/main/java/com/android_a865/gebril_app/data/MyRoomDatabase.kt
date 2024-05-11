package com.android_a865.gebril_app.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.android_a865.gebril_app.data.entities.InvoiceEntity
import com.android_a865.gebril_app.data.entities.InvoiceItemEntity
import com.android_a865.gebril_app.data.entities.ItemEntity
import com.android_a865.gebril_app.data.MyRoomDatabase.Companion.DATABASE_VERSION
import com.android_a865.gebril_app.data.dao.InvoicesDao
import com.android_a865.gebril_app.data.dao.ItemsDao

@Database(
    entities = [
        ItemEntity::class,
        InvoiceItemEntity::class,
        InvoiceEntity::class,
               ],
    version = DATABASE_VERSION,
    exportSchema = false
)

abstract class MyRoomDatabase: RoomDatabase() {

    abstract fun getItemsDao(): ItemsDao

    abstract fun getInvoicesDao(): InvoicesDao


    companion object {
        // Room Database
        const val DATABASE_NAME = "InvoiceMaster.db"
        const val DATABASE_VERSION = 3
    }
}