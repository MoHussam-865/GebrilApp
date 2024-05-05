package com.android_a865.gebril_app.feature_main.domain.use_cases.invoice_use_cases

data class InvoiceUseCases(
    val addInvoice: AddInvoiceUseCase,
    val getInvoices: GetInvoicesUseCase,
    val updateInvoice: UpdateInvoiceUseCase,
    val applyDiscountUseCase: ApplyDiscountUseCase
)
