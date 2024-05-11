package com.android_a865.gebril_app.data.domain

data class Item(
    val id: Int,
    val name: String,
    val price: Double,
    val qty: Double,
    val discount: Double,
    val total: Double,
    val path: String,
    val invoice_id: Long,
    val last_update: Long,
    val is_folder: Boolean,
    val status: Int = 1,
)
