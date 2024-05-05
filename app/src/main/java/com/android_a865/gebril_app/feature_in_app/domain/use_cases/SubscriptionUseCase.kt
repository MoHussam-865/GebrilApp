package com.android_a865.gebril_app.feature_in_app.domain.use_cases

import android.util.Log
import com.android_a865.gebril_app.feature_in_app.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.first

class SubscriptionUseCase(
    private val repository: SubscriptionRepository
) {
    suspend operator fun invoke(): Boolean {

        val invoicesNumber = repository.getNumberOfInvoices()
        val isSubscribed = repository.isSubscribed().first()

        Log.d("Subscription", "$invoicesNumber, $isSubscribed")

        return isSubscribed
    }
}