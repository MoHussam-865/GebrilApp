package com.android_a865.gebril_app.feature_main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android_a865.gebril_app.databinding.ActivityFeature2Binding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ActivityFeature2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityFeature2Binding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}