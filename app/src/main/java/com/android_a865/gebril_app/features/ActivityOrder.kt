package com.android_a865.gebril_app.features

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android_a865.gebril_app.databinding.ActivityOrderBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ActivityOrder : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}