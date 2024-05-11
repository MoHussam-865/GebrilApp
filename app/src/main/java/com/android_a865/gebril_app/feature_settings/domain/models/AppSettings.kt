package com.android_a865.gebril_app.feature_settings.domain.models

data class AppSettings(
    val clientInfo: String,
    val dateFormat: String,
    val currency: String,
    val lastUpdate: Int,
    val lastUpdateDate: Long,
)
