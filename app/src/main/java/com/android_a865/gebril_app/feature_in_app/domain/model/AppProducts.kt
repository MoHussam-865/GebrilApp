package com.android_a865.gebril_app.feature_in_app.domain.model

import com.android.billingclient.api.SkuDetails

data class AppProducts(
    val monthly: SkuDetails? = null,
    val yearly: SkuDetails? = null
)