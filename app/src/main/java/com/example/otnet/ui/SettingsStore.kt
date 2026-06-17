package com.example.otnet.ui

import android.util.Log
import com.example.otnet.data.api.OTNetService
import com.example.otnet.data.models.PublisherSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * App-scope cache of /catalog/settings. Fetched once at app launch; screens
 * read it via [settings] to decide which tabs / rows / paywalls to surface.
 * A null value means "still loading" — features that depend on it should
 * either wait or use safe defaults.
 */
object SettingsStore {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _settings = MutableStateFlow<PublisherSettings?>(null)
    val settings: StateFlow<PublisherSettings?> = _settings.asStateFlow()

    fun refresh(service: OTNetService) {
        scope.launch {
            runCatching { withContext(Dispatchers.IO) { service.settings() } }
                .onSuccess { _settings.value = it }
                .onFailure { Log.w("OTNet", "settings fetch failed", it) }
        }
    }
}
