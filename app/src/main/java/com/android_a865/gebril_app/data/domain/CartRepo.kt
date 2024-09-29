package com.android_a865.gebril_app.data.domain

import kotlinx.coroutines.flow.Flow

interface CartRepo {

    fun getCart(): Flow<List<InvoiceItem>>

    suspend fun addToCart(invoiceItem: InvoiceItem)

    suspend fun removeFromCart(invoiceItem: InvoiceItem)

}