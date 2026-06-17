package com.example.otnet.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Content(
    @SerialName("_id") val id: String,
    val title: String? = null,
    val description: String? = null,
    val contentType: String? = null,
    val media: List<MediaItem> = emptyList(),
    val ageRating: String? = null,
    val titleImage: String? = null,
    val childCount: Int = 0,
    val sortOrder: Int? = null,
    val parent: ParentRef? = null,
    val genres: List<GenreRef> = emptyList(),
    // New top-level art fields (alongside the per-media fallbacks)
    val portrait: String? = null,
    val landscape: String? = null,
    val backdrop: String? = null,
    val teaser: Teaser? = null,
)

@Serializable
data class Teaser(
    val resources: TeaserResources? = null,
    val variants: List<MediaVariant> = emptyList(),
    val duration: Int? = null,
)

@Serializable
data class TeaserResources(
    val poster: String? = null,
)

@Serializable
data class ParentRef(@SerialName("_id") val id: String? = null)

@Serializable
data class MediaItem(
    val portrait: String? = null,
    val landscape: String? = null,
    val backdrop: String? = null,
    val variants: List<MediaVariant> = emptyList(),
)

@Serializable
data class MediaVariant(
    val protocol: String? = null,
    val entrypoint: String? = null,
    val duration: Int? = null,
    val drm: DrmConfig? = null,
    val resources: VariantResources? = null,
)

@Serializable
data class VariantResources(
    val poster: String? = null,
    val bif: String? = null,
    val adbreaks: String? = null,
)

@Serializable
data class DrmConfig(
    val sessionDrm: Boolean? = null,
    val provider: String? = null,
    val widevine: DrmSystem? = null,
    val playready: DrmSystem? = null,
    val fairplay: DrmFairplay? = null,
)

@Serializable
data class DrmSystem(val licenseUrl: String? = null)

@Serializable
data class DrmFairplay(
    val licenseUrl: String? = null,
    val certificateUrl: String? = null,
)

@Serializable
data class VodMintRequest(
    val contentId: String,
    val protocol: String = "dash",
    val mediaIndex: Int = 0,
)

@Serializable
data class VodMintResponse(
    val playback: PlaybackBlock? = null,
)

@Serializable
data class PlaybackBlock(
    val protocol: String? = null,
    val masterUrl: String? = null,
    val sessionId: String? = null,
    val sessionToken: String? = null,
    val analyticsToken: String? = null,
    val drm: DrmConfig? = null,
)
