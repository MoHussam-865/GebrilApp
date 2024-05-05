package com.android_a865.gebril_app.feature_in_app.data.repository

import com.android_a865.gebril_app.feature_in_app.data.dao.SubscriptionDao
import com.android_a865.gebril_app.feature_in_app.domain.repository.SubscriptionRepository
import com.android_a865.gebril_app.feature_settings.data.data_source.PreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SubscriptionRepositoryImpl(
    private val dao: SubscriptionDao,
    private val preferences: PreferencesManager
): SubscriptionRepository {

    override suspend fun getNumberOfInvoices(): Int {
        return dao.getTotalNumberInInvoicesTable()
    }

    override suspend fun isSubscribed(): Flow<Boolean> {
        return preferences.preferencesFlow.map {
            it.isSubscribed
        }
    }
}