package com.android_a865.gebril_app.feature_main.domain.use_cases.invoice_use_cases

import com.android_a865.gebril_app.feature_main.data.mapper.toInvoices
import com.android_a865.gebril_app.feature_main.domain.model.Invoice
import com.android_a865.gebril_app.feature_main.domain.model.InvoiceTypes
import com.android_a865.gebril_app.feature_main.domain.repository.InvoiceRepository
import com.android_a865.gebril_app.feature_main.presentation.invoices_view.FilterOptions
import com.android_a865.gebril_app.utils.exhaustive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetInvoicesUseCase(
    private val repository: InvoiceRepository
) {
    operator fun invoke(filter: FilterOptions = FilterOptions.All): Flow<List<Invoice>> {

        var invoices = repository.getInvoices().map {
            it.toInvoices()
        }

        when (filter) {
            FilterOptions.All -> { }
            FilterOptions.Draft -> {
                invoices = invoices.map { invoices0 ->
                    invoices0.filter { invoice ->
                        invoice.type == InvoiceTypes.Draft
                    }

                }
            }
            FilterOptions.Estimate -> {
                invoices = invoices.map { invoices0 ->
                    invoices0.filter { invoice ->
                        invoice.type == InvoiceTypes.Estimate
                    }

                }
            }
            FilterOptions.Invoice -> {
                invoices = invoices.map { invoices0 ->
                    invoices0.filter { invoice ->
                        invoice.type == InvoiceTypes.Invoice
                    }

                }
            }
        }.exhaustive

        return invoices
    }
}