package com.android_a865.gebril_app.data.domain

import android.os.Parcelable
import com.android_a865.gebril_app.utils.Path
import com.android_a865.gebril_app.utils.Path.Companion.ROOT
import com.android_a865.gebril_app.utils.remove
import com.google.gson.annotations.Expose
import kotlinx.parcelize.Parcelize

@Parcelize
data class InvoiceItem(
    val id: Int = 0,
    val name: String = "",
    var qty: Double = 0.0,
    val price: Double = 0.0,
    var discount: Double = 0.0,

    @Expose(serialize = false, deserialize = false)
    val isFolder: Boolean = false,

    @Expose(serialize = false, deserialize = false)
    val parentId: Int = ROOT,

    @Expose(serialize = false, deserialize = false)
    val path: String,

    @Expose(serialize = false, deserialize = false)
    val imagePath: String? = null
) : Parcelable {

    val total get() = finalPrice * qty
    val finalPrice get() = price * (1 - discount/100)

    val fullName get(): String {
        val names = path.split(Path.SEPARATION)
        val n = names.drop(1)
        return "$name " + n.joinToString(" ")
    }

}