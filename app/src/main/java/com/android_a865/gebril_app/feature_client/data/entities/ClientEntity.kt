package com.android_a865.gebril_app.feature_client.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "Clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val org: String? = null,
    val title: String? = null,
    val phone1: String? = null,
    val phone2: String? = null,
    val email: String? = null,
    val address: String? = null
)