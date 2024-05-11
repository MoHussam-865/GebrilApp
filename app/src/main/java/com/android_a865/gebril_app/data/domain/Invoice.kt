package com.android_a865.gebril_app.data.domain

import java.util.*


/**
 * this only used to be sent
 */
data class Invoice(
    val id: Int,
    val date: Long = Calendar.getInstance().timeInMillis,

    val items: List<InvoiceItem>,
    val total: Double,

    val notes: String = "",
    val client: String = "",
)
