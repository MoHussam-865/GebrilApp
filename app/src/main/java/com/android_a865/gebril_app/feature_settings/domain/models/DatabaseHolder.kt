package com.android_a865.gebril_app.feature_settings.domain.models

import com.android_a865.gebril_app.feature_client.data.entities.ClientEntity
import com.android_a865.gebril_app.feature_main.data.entities.ItemEntity
import com.android_a865.gebril_app.feature_main.data.relations.FullInvoice

data class DatabaseHolder(
    val items: List<ItemEntity>,
    val clients: List<ClientEntity>,
    val invoices: List<FullInvoice>
)
