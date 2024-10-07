package com.android_a865.gebril_app.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class CartItem(
    @PrimaryKey(autoGenerate = false)
    val itemId: Int = 0,
    val qty: Double = 1.0
): Parcelable
