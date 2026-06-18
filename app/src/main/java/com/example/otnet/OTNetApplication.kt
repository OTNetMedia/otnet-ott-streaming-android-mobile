package com.example.otnet

import android.app.Application
import com.example.otnet.data.api.NetworkModule
import com.example.otnet.ui.AppDeps
import com.example.otnet.ui.ContinueWatchingStore
import com.example.otnet.ui.DeviceStore
import com.example.otnet.ui.ProfileStore
import com.example.otnet.ui.SettingsStore

class OTNetApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val key = BuildConfig.OTNET_API_KEY
        check(key.isNotBlank()) {
            "OTNET_API_KEY is missing. Copy local.properties.example to local.properties " +
                "and set OTNET_API_KEY=<your publisher key>."
        }
        val service = NetworkModule.provideService(
            apiKey = key,
            profileIndexProvider = { ProfileStore.index.value },
        )
        AppDeps.service = service
        SettingsStore.refresh(service)
        DeviceStore.hello(this, service)
        ContinueWatchingStore.refresh(this, service)
    }
}
