package com.android_a865.gebril_app.feature_in_app.presentation.main

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.*
import com.android_a865.gebril_app.feature_in_app.domain.model.AppProducts
import com.android_a865.gebril_app.feature_in_app.domain.model.Security
import com.android_a865.gebril_app.feature_settings.domain.models.AppSettings
import com.android_a865.gebril_app.feature_settings.domain.repository.SettingsRepository
import com.android_a865.gebril_app.utils.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "SharedViewModel"


@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: SettingsRepository,
    @ApplicationContext context: Context
) : ViewModel() {

    var myAd: InterstitialAd? = null

    private var appSetting: AppSettings? = null

    private val subscriptionStatusFlow = repository.getAppSettings().map { it.isSubscribed }
    val isSubscribed = subscriptionStatusFlow.asLiveData()

    val products = MutableStateFlow(AppProducts())

    private val purchaseListener: PurchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                handlePurchases(purchases)
                Log.d(TAG, "Item Purchased")
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
                getPurchases()
                Log.d(TAG, "Already Owned")
            } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
                showMessage("Purchase Canceled")
            } else {
                showMessage("Error")
                Log.d(TAG, billingResult.debugMessage)
            }
        }

    private val billingClient = BillingClient.newBuilder(context)
        .enablePendingPurchases().setListener(purchaseListener).build()

    private val eventsChannel = Channel<WindowEvents>()
    val windowEvents = eventsChannel.receiveAsFlow()


    init {
        viewModelScope.launch {
            repository.getAppSettings().collect {
                appSetting = it
            }
        }
        Log.d(TAG, "initiated")
    }

    fun subscribe(productId: String) = billingClient.doOnReady {
        initiatePurchase(productId)
    }

    private fun initiatePurchase(productId: String) {
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(arrayListOf(productId)).setType(BillingClient.SkuType.SUBS)


        val billingResult =
            billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

            billingClient.querySkuDetailsAsync(params.build()) { billingResult0, skuDetailsList ->
                if (billingResult0.responseCode == BillingClient.BillingResponseCode.OK) {

                    if (skuDetailsList != null && skuDetailsList.size > 0) {

                        Log.d(TAG, "Launching Billing Flow")

                        val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(skuDetailsList.first())
                            .build()

                        viewModelScope.launch {
                            eventsChannel.send(WindowEvents.LaunchBillingFlow(flowParams))
                        }

                    } else {
                        showMessage("Item not found")
                    }
                } else {
                    showMessage("Error ${billingResult0.debugMessage}")
                }
            }

        } else {
            showMessage("Sorry Subscription not supported")
        }
    }

    private fun showMessage(msg: String) = viewModelScope.launch {
        eventsChannel.send(WindowEvents.ShowMessage(msg))
    }


    private fun queryProductsDetails() {
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(
            arrayListOf(
                MONTHLY_SUBSCRIPTION,
                YEARLY_SUBSCRIPTION
            )
        ).setType(BillingClient.SkuType.SUBS)


        val billingResult =
            billingClient.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {

            billingClient.querySkuDetailsAsync(params.build()) { billingResult0, skuDetailsList ->

                Log.d(TAG, "getting supported products")

                if (billingResult0.responseCode == BillingClient.BillingResponseCode.OK) {

                    if (skuDetailsList != null && skuDetailsList.size > 0) {

                        val monthly = skuDetailsList.filter {
                            it.sku == MONTHLY_SUBSCRIPTION
                        }

                        val yearly = skuDetailsList.filter {
                            it.sku == YEARLY_SUBSCRIPTION
                        }

                        if (monthly.isNotEmpty()) {
                            products.value = products.value.copy(
                                    monthly = monthly.first()
                                )
                        }

                        if (yearly.isNotEmpty()) {
                            products.value = products.value.copy(
                                yearly = yearly.first()
                            )
                        }


                    } else {
                        showMessage("Item not found")
                    }
                } else {
                    showMessage("Error ${billingResult0.debugMessage}")
                }
            }

        } else {
            showMessage("Sorry Subscription not supported")
        }
    }

    private fun handlePurchases(purchases: List<Purchase>) {
        val validProducts = listOf(MONTHLY_SUBSCRIPTION, YEARLY_SUBSCRIPTION)
        val validPurchases = mutableListOf<String>()

        for (purchase in purchases) {

            val isSubscribed = isValidPurchase(purchase)

            if (isSubscribed) {

                val productId = purchase.skus.first()
                Log.d(TAG, productId)
                if (productId in validProducts) {
                    validPurchases.add(productId)
                    Log.d(TAG, "one valid subscription")
                } else {
                    Log.d(TAG, "one invalid subscription")
                }
            }

        }

        setIsSubscribed(validPurchases.isNotEmpty())
    }

    private fun isValidPurchase(purchase: Purchase): Boolean {
        when (purchase.purchaseState) {
            Purchase.PurchaseState.PURCHASED -> {


                if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
                    // Invalid purchase
                    // show error to user
                    showMessage("Error : Invalid Purchase")
                    return false
                    //skip current iteration only because other items in purchase list
                    // must be checked if present
                }
                // else purchase is valid
                //if item is subscribed and not Acknowledged
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()
                    billingClient.acknowledgePurchase(
                        acknowledgePurchaseParams
                    ) { billingResult ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            //if purchase is acknowledged
                            // then saved value in preference
                            Log.d(TAG, " Item Subscribed")
                        }
                    }
                } else {
                    // Grant entitlement to the user on item purchase
                    val subscribed = isSubscribed.value ?: false
                    if (!subscribed) {
                        // save Subscribe Item Value To Pref as true)
                        Log.d(TAG, " Item Subscribed")
                    }
                }
                return true

            }
            Purchase.PurchaseState.PENDING -> {
                showMessage("Subscription is pending, Please complete Transaction")
                return false
            }
            Purchase.PurchaseState.UNSPECIFIED_STATE -> {
                showMessage("Subscription Status Unknown")
                return false
            }
            else -> {
                return false
            }
        }
    }


    fun launchBillingFlow(activity: Activity, flowParams: BillingFlowParams) {
        billingClient.launchBillingFlow(activity, flowParams)
    }

    fun onAppStarted() {
        // to force init called from MainActivity
        Log.d(TAG, "on app start check")
        billingClient.doOnReady {
            getPurchases()
        }
    }

    fun getProductsData() {
        val subscribed = isSubscribed.value ?: false
        val productsAlreadyLoaded = products.value.monthly != null &&
                products.value.yearly != null

        if (!subscribed && !productsAlreadyLoaded) {
            billingClient.doOnReady {
                queryProductsDetails()
            }
        }

    }

    private fun setIsSubscribed(b: Boolean) {
        viewModelScope.launch {
            repository.updateIsSubscribed(b)
        }
    }

    private fun getPurchases() {
        billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS) { _, purchases ->
            Log.d(TAG, "${purchases.size} purchases")
            handlePurchases(purchases)
        }
    }


    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        return try {
            Security.verifyPurchase(base64Key, signedData, signature)
        } catch (e: Exception) {
            Log.d(TAG, e.message.toString())
            false
        }
    }


    sealed class WindowEvents {
        data class ShowMessage(val msg: String) : WindowEvents()
        data class LaunchBillingFlow(val flowParams: BillingFlowParams) : WindowEvents()
    }
}