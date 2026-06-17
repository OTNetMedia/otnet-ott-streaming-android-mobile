package com.example.otnet.data.models

fun Content.displayTitle(): String = title ?: "Untitled"

private fun String?.nonBlank(): String? = this?.takeIf { it.isNotBlank() }

fun Content.posterUrl(): String? =
    portrait.nonBlank()
        ?: media.firstOrNull()?.portrait.nonBlank()

fun Content.landscapeUrl(): String? =
    landscape.nonBlank()
        ?: media.firstOrNull()?.landscape.nonBlank()
        ?: media.firstOrNull()?.backdrop.nonBlank()

fun Content.heroBackdropUrl(): String? =
    backdrop.nonBlank()
        ?: landscape.nonBlank()
        ?: media.firstOrNull()?.backdrop.nonBlank()
        ?: media.firstOrNull()?.landscape.nonBlank()
        ?: portrait.nonBlank()
        ?: media.firstOrNull()?.portrait.nonBlank()

fun Content.isSeries(): Boolean = contentType == "series" || childCount > 0

fun Content.primaryGenreName(): String? = genres.firstOrNull()?.name

fun Content.firstDashVariant(): MediaVariant? =
    media.firstOrNull()?.variants?.firstOrNull { it.protocol == "dash" }

fun Content.firstHlsVariant(): MediaVariant? =
    media.firstOrNull()?.variants?.firstOrNull { it.protocol == "hls" }

fun Content.playableVariant(): MediaVariant? = firstDashVariant() ?: firstHlsVariant()

fun Content.allVariants(): List<MediaVariant> =
    media.flatMap { it.variants }

fun Content.runtimeMinutes(): Int? {
    val seconds = media.firstOrNull()?.variants?.firstOrNull()?.duration ?: return null
    return (seconds / 60).takeIf { it > 0 }
}

fun Content.teaserDashUrl(): String? =
    teaser?.variants?.firstOrNull { it.protocol == "dash" }?.entrypoint.nonBlank()

fun Content.teaserHlsUrl(): String? =
    teaser?.variants?.firstOrNull { it.protocol == "hls" }?.entrypoint.nonBlank()

fun Content.teaserPlaybackUrl(): String? = teaserDashUrl() ?: teaserHlsUrl()

fun Content.hasTeaser(): Boolean = teaserPlaybackUrl() != null
