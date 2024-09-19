package com.android_a865.gebril_app.data.domain

import com.android_a865.gebril_app.data.entities.Item
import com.android_a865.gebril_app.data.entities.Post


/**
 * used to be transformed to json and from json
 * to communicate with the server
 */
data class Message(
    val last_update: Int = 0,
    val invoice: InvoiceHolder? = null,
    val items: List<Item>? = null,
    val posts: List<Post>? = null,
    val message: String = "",
)
