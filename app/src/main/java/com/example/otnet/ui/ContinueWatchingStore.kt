package com.example.otnet.ui

import android.content.Context
import android.util.Log
import com.example.otnet.data.api.OTNetService
import com.example.otnet.data.models.DeviceProgressItem
import com.example.otnet.data.models.DeviceProgressRequest
import com.example.otnet.data.models.isPartiallyWatched
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * App-scope Continue Watching cache, fed by `/device/progress`. Reads happen
 * on app launch and after the player reports new progress; writes happen via
 * [reportProgress] (fire-and-forget, throttled to one in-flight call).
 */
object ContinueWatchingStore {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _items = MutableStateFlow<List<DeviceProgressItem>>(emptyList())
    val items: StateFlow<List<DeviceProgressItem>> = _items.asStateFlow()

    @Volatile private var inFlightWrite = false

    fun refresh(context: Context, service: OTNetService) {
        val deviceId = DeviceStore.deviceId(context)
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { service.getDeviceProgress(deviceId) }
            }.onSuccess { resp ->
                _items.value = resp.items
                    .filter { it.isPartiallyWatched() && it.content != null }
                    .sortedByDescending { it.updatedAt ?: "" }
            }.onFailure { Log.w("OTNet", "device/progress GET failed", it) }
        }
    }

    fun reportProgress(
        context: Context,
        service: OTNetService,
        contentId: String,
        mediaIndex: Int,
        positionSeconds: Int,
        durationSeconds: Int,
    ) {
        if (inFlightWrite || durationSeconds <= 0) return
        val deviceId = DeviceStore.deviceId(context)
        inFlightWrite = true
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    service.postDeviceProgress(
                        DeviceProgressRequest(
                            deviceId = deviceId,
                            contentId = contentId,
                            mediaIndex = mediaIndex,
                            progressSeconds = positionSeconds,
                            durationSeconds = durationSeconds,
                        )
                    )
                }
            }.onFailure { Log.w("OTNet", "device/progress POST failed", it) }
            inFlightWrite = false
        }
    }

    fun progressFor(contentId: String): DeviceProgressItem? =
        _items.value.firstOrNull { it.contentId == contentId }
}
