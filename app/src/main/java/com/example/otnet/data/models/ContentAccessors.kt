package com.example.otnet.data.models

fun Content.displayTitle(): String = title ?: "Untitled"

fun Content.posterUrl(): String? = media.firstOrNull()?.portrait

fun Content.landscapeUrl(): String? =
    media.firstOrNull()?.landscape ?: media.firstOrNull()?.backdrop

fun Content.heroBackdropUrl(): String? =
    media.firstOrNull()?.backdrop
        ?: media.firstOrNull()?.landscape
        ?: media.firstOrNull()?.portrait

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
