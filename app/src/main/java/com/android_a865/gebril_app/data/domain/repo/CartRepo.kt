package com.android_a865.gebril_app.data.domain.repo

import com.android_a865.gebril_app.data.domain.InvoiceItem
import kotlinx.coroutines.flow.Flow

interface CartRepo {

    fun getCart(): Flow<List<InvoiceItem>>

    suspend fun addOne(invoiceItem: InvoiceItem)

    suspend fun removeOne(invoiceItem: InvoiceItem)

    suspend fun setQty(invoiceItem: InvoiceItem, qty: Double)

    suspend fun clearCart()


    suspend fun addToCart(invoiceItem: InvoiceItem)

    suspend fun removeFromCart(invoiceItem: InvoiceItem)

}