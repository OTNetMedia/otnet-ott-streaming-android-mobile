package com.example.otnet

import android.app.Application
import com.example.otnet.data.api.NetworkModule
import com.example.otnet.ui.AppDeps

class OTNetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val key = BuildConfig.OTNET_API_KEY
        check(key.isNotBlank()) {
            "OTNET_API_KEY is missing. Copy local.properties.example to local.properties " +
                "and set OTNET_API_KEY=<your publisher key>."
        }
        AppDeps.service = NetworkModule.provideService(key)
    }
}
