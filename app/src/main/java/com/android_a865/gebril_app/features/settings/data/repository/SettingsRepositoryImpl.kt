package com.android_a865.gebril_app.feature_settings.data.repository

import com.android_a865.gebril_app.feature_settings.data.data_source.PreferencesManager
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class SettingsRepositoryImpl(
    private val preferences: PreferencesManager
): SettingsRepository {

    override suspend fun getAppSetting(): AppSettings {
        return preferences.preferencesFlow.first()
    }

    override fun getAppSettings(): Flow<AppSettings> {
        return preferences.preferencesFlow
    }

    override suspend fun updateSettings(settings: AppSettings) =
        preferences.updateSettings(settings)

}