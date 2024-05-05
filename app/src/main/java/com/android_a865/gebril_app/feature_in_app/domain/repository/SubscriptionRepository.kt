package com.android_a865.gebril_app.feature_in_app.domain.repository

import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {

    suspend fun getNumberOfInvoices(): Int

    suspend fun isSubscribed(): Flow<Boolean>

}