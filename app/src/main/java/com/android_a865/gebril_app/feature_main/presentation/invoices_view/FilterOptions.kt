package com.android_a865.gebril_app.feature_main.presentation.invoices_view

sealed class FilterOptions {
    object All : FilterOptions()
    object Invoice: FilterOptions()
    object Estimate: FilterOptions()
    object Draft: FilterOptions()
}
