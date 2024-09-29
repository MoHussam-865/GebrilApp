package com.android_a865.gebril_app.data.domain

import com.android_a865.gebril_app.data.entities.Invoice
import com.android_a865.gebril_app.data.entities.Item
import com.android_a865.gebril_app.data.entities.Post


/**
 * used to be transformed to json and from json
 * to communicate with the server
 */
data class Message(
    val lastUpdate: Int = 0,
    //val invoice: Invoice? = null,
    val items: List<Item>? = null,
    val posts: List<Post>? = null,
    val message: String = "",
)
