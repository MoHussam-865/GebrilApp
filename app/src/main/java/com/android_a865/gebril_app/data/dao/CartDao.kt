package com.android_a865.gebril_app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android_a865.gebril_app.data.entities.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Query("SELECT * FROM CartItem")
    fun getCart(): Flow<List<CartItem>>

    @Query("SELECT * FROM CartItem WHERE itemId = :id")
    suspend fun getItemWithId(id: Int): CartItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToCart(cartItem: CartItem)

    @Query("DELETE FROM CartItem WHERE itemId = :id ")
    suspend fun removeFromCart(id: Int)

    @Query("DELETE FROM cartitem")
    suspend fun clearCart()
}