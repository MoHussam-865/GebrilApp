package com.android_a865.gebril_app.feature_main.domain.model

import android.os.Parcelable
import com.android_a865.gebril_app.feature_client.domain.model.Client
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Invoice(
    val id: Int = 0,
    val type: InvoiceTypes,
    val date: Long = Calendar.getInstance().timeInMillis,
    val client: Client? = null,
    val items: List<InvoiceItem>,
) : Parcelable {
    val total get() = items.sumOf { it.total }
}
