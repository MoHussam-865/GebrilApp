package com.android_a865.gebril_app.feature_main.domain.use_cases.invoice_use_cases

import com.android_a865.gebril_app.feature_main.data.mapper.toInvoiceItem
import com.android_a865.gebril_app.feature_main.domain.model.InvoiceItem
import com.android_a865.gebril_app.feature_main.domain.repository.ItemsRepository
import com.android_a865.gebril_app.utils.Path

class ApplyDiscountUseCase(
    val repository: ItemsRepository
) {

    // returns invoiceItem friends to apply discount
    suspend operator fun invoke(item: InvoiceItem): List<Int> {
        val myItem = repository.getItemById(item.id)

         return repository.getItemFriends(myItem.path)
             .filter { it.id != item.id && !it.isFolder}
             .map { it.id }
    }
}