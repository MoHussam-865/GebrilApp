package com.android_a865.gebril_app.feature_reports.domain.model

import com.android_a865.gebril_app.feature_client.domain.model.Client
import com.android_a865.gebril_app.feature_main.domain.model.Invoice
import com.android_a865.gebril_app.feature_main.domain.model.InvoiceItem

data class ClientReport(
    val client: Client,
    val invoices: List<Invoice>,
    val estimates: List<Invoice>,
    val items: SmartList<InvoiceItem>
)