package com.android_a865.gebril_app.feature_settings.domain.repository

import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getAppSettings(): Flow<AppSettings>

    suspend fun getAppSetting(): AppSettings

    suspend fun updateSettings(settings: AppSettings)
}