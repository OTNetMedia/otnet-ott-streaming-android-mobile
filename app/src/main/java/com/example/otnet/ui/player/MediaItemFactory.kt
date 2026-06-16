package com.example.otnet.ui.player

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import com.example.otnet.data.api.OTNetService
import com.example.otnet.data.models.DrmSessionRequest
import com.example.otnet.data.models.MediaVariant

/**
 * Build a Media3 [MediaItem] for a variant, transparently minting a session-mode
 * Widevine license token when [variant.drm] is present. Clear-text variants skip
 * the DRM block entirely.
 */
suspend fun buildMediaItem(
    service: OTNetService,
    variant: MediaVariant,
    contentId: String,
    mediaIndex: Int = 0,
): MediaItem {
    val uri = variant.entrypoint ?: error("Variant has no entrypoint")
    val mime = when (variant.protocol) {
        "dash" -> MimeTypes.APPLICATION_MPD
        "hls" -> MimeTypes.APPLICATION_M3U8
        else -> MimeTypes.APPLICATION_MPD
    }

    val builder = MediaItem.Builder()
        .setUri(uri)
        .setMimeType(mime)

    if (variant.drm != null) {
        val session = service.drmSession(DrmSessionRequest(contentId, mediaIndex))
        val token = session.token ?: error("DRM session response had no token")
        val licenseUrl =
            "https://otnet.io/api/v1/playback/drm/license?token=$token&system=widevine"

        builder.setDrmConfiguration(
            MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                .setLicenseUri(licenseUrl)
                .setMultiSession(true)
                .build()
        )
    }

    return builder.build()
}
