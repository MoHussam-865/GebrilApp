package com.android_a865.gebril_app.data.domain


/**
 * used to be transformed to json and from json
 * to communicate with the server
 */
data class Message(
    val last_update: Int = 0,
    val invoice: Invoice? = null,
    val items: List<Item>? = null,
    val status: Int = 0,
)
