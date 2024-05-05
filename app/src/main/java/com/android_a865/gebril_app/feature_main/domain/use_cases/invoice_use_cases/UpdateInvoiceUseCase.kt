package com.android_a865.gebril_app.feature_main.domain.use_cases.invoice_use_cases

import com.android_a865.gebril_app.feature_main.data.mapper.toEntity
import com.android_a865.gebril_app.feature_main.domain.model.Invoice
import com.android_a865.gebril_app.feature_main.domain.repository.InvoiceRepository

class UpdateInvoiceUseCase(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(invoice: Invoice) {
        repository.updateInvoice(invoice.toEntity())
    }
}