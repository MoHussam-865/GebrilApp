package com.android_a865.gebril_app.data.repository

import com.android_a865.gebril_app.data.dao.CartDao
import com.android_a865.gebril_app.data.dao.ItemsDao
import com.android_a865.gebril_app.data.domain.CartRepo
import com.android_a865.gebril_app.data.domain.InvoiceItem
import com.android_a865.gebril_app.data.entities.CartItem
import com.android_a865.gebril_app.data.entities.Item
import com.android_a865.gebril_app.data.mapper.toInvoiceItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CartRepoIml(
    val dao: CartDao,
    private val itemsDao: ItemsDao
): CartRepo {

    override fun getCart(): Flow<List<InvoiceItem>> {
        return dao.getCart().map { cart ->
            cart.map { cartItem ->
                val item: Item = itemsDao.getItemById(cartItem.itemId)
                item.toInvoiceItem().copy(qty = cartItem.qty)
            }

        }
    }

    override suspend fun addToCart(invoiceItem: InvoiceItem) {

        val cartItem: CartItem? = dao.getItemWithId(invoiceItem.id)

        val myCartItem =
            cartItem?.copy(qty = invoiceItem.qty) ?:
            CartItem(itemId = invoiceItem.id, qty = invoiceItem.qty)

        dao.addToCart(myCartItem)
    }

    override suspend fun removeFromCart(invoiceItem: InvoiceItem) {
        dao.removeFromCart(invoiceItem.id)
    }

    override suspend fun clearCart() {
        dao.clearCart()
    }


}