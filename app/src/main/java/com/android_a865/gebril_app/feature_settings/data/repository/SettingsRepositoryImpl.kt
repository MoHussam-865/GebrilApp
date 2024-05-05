package com.android_a865.gebril_app.feature_settings.data.repository

import com.android_a865.gebril_app.feature_settings.data.data_source.PreferencesManager
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.feature_settings.domain.models.Company
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class SettingsRepositoryImpl(
    private val preferences: PreferencesManager
): SettingsRepository {

    override fun getAppSettings(): Flow<AppSettings> {
        return preferences.preferencesFlow
    }

    override suspend fun updateCompanyInfo(company: Company) {
        preferences.updateCompanyInfo(company)
    }

    override suspend fun updateDateFormat(dateFormat: String) {
        preferences.updateDateFormat(dateFormat)
    }

    override suspend fun updateCurrency(currency: String) {
        preferences.updateCurrency(currency)
    }

    override suspend fun updateIsFirst(isFirst: Boolean) {
        preferences.updateIsFirst(isFirst)
    }

    override suspend fun updateIsSubscribed(isSubscribed: Boolean) {
        preferences.updateIsSubscribed(isSubscribed)
    }
}