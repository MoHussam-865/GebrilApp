package com.android_a865.gebril_app.feature_settings.data.data_source

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.utils.DATE_FORMATS
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("user_preferences")

    val preferencesFlow = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.d("PreferencesManager", "Error reading the preferences", exception)
                emit(emptyPreferences())
            }else {
                throw exception
            }

        }
        .map { preferences ->
            AppSettings(
                dateFormat = preferences[PreferencesKeys.DATE_FORMAT] ?: DATE_FORMATS[0],
                currency = preferences[PreferencesKeys.CURRENCY] ?: "",
                clientInfo = preferences[PreferencesKeys.CLIENTS_INFO] ?: "",
                lastUpdate = preferences[PreferencesKeys.LAST_UPDATE] ?: 0,
                lastUpdateDate = preferences[PreferencesKeys.LAST_UPDATE_DATE] ?: 0,
            )
        }


    suspend fun updateSettings(settings: AppSettings) {
        context.dataStore.edit { preferences ->
            settings.apply {
                preferences[PreferencesKeys.CLIENTS_INFO] = clientInfo
                preferences[PreferencesKeys.LAST_UPDATE] = lastUpdate
                preferences[PreferencesKeys.LAST_UPDATE_DATE] = lastUpdateDate
            }
        }
    }


    private object PreferencesKeys {
        val CLIENTS_INFO = stringPreferencesKey("client_info")
        val DATE_FORMAT = stringPreferencesKey("date_format")
        val CURRENCY = stringPreferencesKey("currency")
        val LAST_UPDATE = intPreferencesKey("last_update")
        val LAST_UPDATE_DATE = longPreferencesKey("last_update_date")
    }
}