package com.example.otnet.ui

import android.content.Context
import android.util.Log
import com.example.otnet.data.api.OTNetService
import com.example.otnet.data.models.DeviceHelloRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Persists a stable per-install UUID and fires the `/device/hello` ping
 * once per launch so anonymous progress + recommendations can land on this
 * device without a viewer login.
 */
object DeviceStore {
    private const val PREFS = "otnet_device"
    private const val KEY_DEVICE_ID = "device_id"

    @Volatile private var cachedId: String? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    fun deviceId(context: Context): String {
        cachedId?.let { return it }
        val prefs = context.applicationContext
            .getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_DEVICE_ID, null)
        val id = existing ?: UUID.randomUUID().toString().also {
            prefs.edit().putString(KEY_DEVICE_ID, it).apply()
        }
        cachedId = id
        return id
    }

    fun hello(context: Context, service: OTNetService) {
        val id = deviceId(context)
        scope.launch {
            runCatching {
                withContext(Dispatchers.IO) { service.deviceHello(DeviceHelloRequest(id)) }
            }.onFailure { Log.w("OTNet", "device/hello failed", it) }
        }
    }
}
