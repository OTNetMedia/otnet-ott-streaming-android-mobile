package com.example.otnet.ui.player

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import com.example.otnet.data.models.PlaybackBlock

/**
 * Build a Media3 [MediaItem] from an OTNet `/playback/vod/mint` response.
 * The mint endpoint hands back both the playable master URL and the
 * short-lived session JWT used to fetch the Widevine license.
 */
fun buildMediaItem(playback: PlaybackBlock): MediaItem {
    val uri = playback.masterUrl ?: error("Playback block has no masterUrl")
    val mime = when (playback.protocol) {
        "hls" -> MimeTypes.APPLICATION_M3U8
        else -> MimeTypes.APPLICATION_MPD
    }

    val builder = MediaItem.Builder()
        .setUri(uri)
        .setMimeType(mime)

    val token = playback.sessionToken
    if (playback.drm != null && !token.isNullOrBlank()) {
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
