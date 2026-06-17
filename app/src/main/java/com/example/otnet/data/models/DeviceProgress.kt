package com.example.otnet.data.models

import kotlinx.serialization.Serializable

@Serializable
data class DeviceHelloRequest(val deviceId: String)

@Serializable
data class DeviceHelloResponse(val ok: Boolean? = null)

@Serializable
data class DeviceProgressRequest(
    val deviceId: String,
    val contentId: String,
    val mediaIndex: Int = 0,
    val progressSeconds: Int,
    val durationSeconds: Int,
)

@Serializable
data class DeviceProgressResponse(val items: List<DeviceProgressItem> = emptyList())

@Serializable
data class DeviceProgressItem(
    val contentId: String? = null,
    val mediaIndex: Int? = null,
    val progressSeconds: Int? = null,
    val durationSeconds: Int? = null,
    val updatedAt: String? = null,
    val content: Content? = null,
)

fun DeviceProgressItem.fractionComplete(): Float {
    val pos = progressSeconds?.toFloat() ?: return 0f
    val dur = durationSeconds?.toFloat() ?: return 0f
    if (dur <= 0f) return 0f
    return (pos / dur).coerceIn(0f, 1f)
}

fun DeviceProgressItem.isPartiallyWatched(): Boolean {
    val f = fractionComplete()
    return f in 0.02f..0.95f
}
