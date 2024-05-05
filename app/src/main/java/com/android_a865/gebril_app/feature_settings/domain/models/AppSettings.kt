package com.android_a865.gebril_app.feature_settings.domain.models

data class AppSettings(
    val company: Company,
    val dateFormat: String,
    val currency: String,
    val isFirst: Boolean,
    val isSubscribed: Boolean
)
