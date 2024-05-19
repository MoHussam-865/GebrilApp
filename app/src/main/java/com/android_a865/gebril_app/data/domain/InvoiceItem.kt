package com.android_a865.gebril_app.data.domain

import android.os.Parcelable
import com.android_a865.gebril_app.utils.Path.Companion.ROOT
import kotlinx.parcelize.Parcelize

@Parcelize
data class InvoiceItem(
    val id: Int = 0,
    val name: String = "",
    val fullName: String = name,
    val price: Double = 0.0,
    var qty: Double = 0.0,
    var discount: Double = 0.0,
    val isFolder: Boolean = false,
    val parentId: Int = ROOT
) : Parcelable {
    val total get() = finalPrice * qty
    val finalPrice get() = price * (1 - discount/100)
//    private val discountDetail get(): String {
//        val sign = if (discount > 0.0) "-" else "+"
//        return if (discount != 0.0) {
//            "(${price.toFormattedString()}  $sign ${abs(discount).toFormattedString()}%)"
//        } else ""
//    }
//    val details get(): String {
//        if (discount != 0.0) {
//            val sign = if (discount > 0.0) "-" else "+"
//            return "$fullName  $discountDetail"
//        }
//        return fullName
//    }

}