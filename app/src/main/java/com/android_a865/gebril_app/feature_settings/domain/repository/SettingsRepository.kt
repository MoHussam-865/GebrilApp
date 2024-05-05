package com.android_a865.gebril_app.feature_settings.domain.repository

import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.feature_settings.domain.models.Company
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getAppSettings(): Flow<AppSettings>

    suspend fun updateCompanyInfo(company: Company)

    suspend fun updateDateFormat(dateFormat: String)

    suspend fun updateCurrency(currency: String)

    suspend fun updateIsFirst(isFirst: Boolean)

    suspend fun updateIsSubscribed(isSubscribed: Boolean)
}