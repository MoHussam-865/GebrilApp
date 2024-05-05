package com.android_a865.gebril_app.utils

import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android_a865.gebril_app.feature_in_app.presentation.main.TAG

inline fun BillingClient.doOnReady(crossinline onReady: () -> Unit) {
    if (isReady) {
        onReady()
    } else {
        startConnection(object: BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "Billing service disconnected")
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    onReady()
                }else {
                    Log.d(TAG, billingResult.debugMessage)
                }
            }
        })
    }
}