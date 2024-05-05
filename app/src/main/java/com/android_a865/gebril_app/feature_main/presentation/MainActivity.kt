package com.android_a865.gebril_app.feature_main.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.android_a865.gebril_app.R
import com.android_a865.gebril_app.databinding.ActivityMainBinding
import com.android_a865.gebril_app.feature_in_app.presentation.main.SharedViewModel
import com.android_a865.gebril_app.feature_main.presentation.new_estimate.TAG
import com.android_a865.gebril_app.utils.NO_AD
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val sharedViewModel by viewModels<SharedViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        sharedViewModel.onAppStarted()

        if (!NO_AD) {
            binding.apply {

                sharedViewModel.isSubscribed.observe(this@MainActivity) { isSubscribed ->
                    if (isSubscribed) {
                        Log.d(TAG, "ads must not be shown")
                        adView.isVisible = false
                    } else {

                        Log.d(TAG, "Checking Connectivity")
                        val connectivityManager =
                            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        connectivityManager.let {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                it.registerDefaultNetworkCallback(object :
                                    ConnectivityManager.NetworkCallback() {
                                    override fun onAvailable(network: Network) {
                                        //take action when network connection is gained
                                        Log.d(TAG, "Connected")
                                        runOnUiThread {
                                            loadAdsOnConnected(binding)
                                        }

                                    }
                                })
                            }
                        }

                    }
                }

            }
        } else binding.adView.isVisible = false
    }

    private fun loadAdsOnConnected(binding: ActivityMainBinding) {
        MobileAds.initialize(this@MainActivity)
        val adRequest = AdRequest.Builder().build()
        binding.adView.isVisible = true
        binding.adView.loadAd(adRequest)

        loadInterstitialAd()
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        // TODO replace with ad_mob_interstitial
        InterstitialAd.load(
            this,
            getString(R.string.ad_mob_interstitial),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d(TAG, adError.message)
                    sharedViewModel.myAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    sharedViewModel.myAd = interstitialAd
                    sharedViewModel.myAd?.fullScreenContentCallback =
                        object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                Log.d(TAG, "Ad was dismissed.")
                            }

                            override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                                Log.d(TAG, "Ad failed to show.")
                            }

                            override fun onAdShowedFullScreenContent() {
                                Log.d(TAG, "Ad showed fullscreen content.")
                                sharedViewModel.myAd = null
                                loadInterstitialAd()
                            }
                        }

                }
            }
        )
    }


}