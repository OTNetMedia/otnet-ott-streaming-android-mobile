package com.example.otnet.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EpgResponse(val channels: List<EpgChannel> = emptyList())

@Serializable
data class ChannelsResponse(val channels: List<EpgChannelInfo> = emptyList())

@Serializable
data class EpgChannel(
    val channel: EpgChannelInfo? = null,
    val playbackUrl: String? = null,
    val programs: List<EpgProgram> = emptyList(),
)

@Serializable
data class EpgChannelInfo(
    @SerialName("_id") val id: String? = null,
    val name: String? = null,
    val logo: String? = null,
    val channelNumber: Int? = null,
    val description: String? = null,
    val backgroundImage: String? = null,
)

@Serializable
data class EpgProgram(
    @SerialName("_id") val id: String? = null,
    val title: String? = null,
    val programName: String? = null,
    val description: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val durationSeconds: Int? = null,
    val contentId: String? = null,
    val content: EpgProgramContent? = null,
)

@Serializable
data class EpgProgramContent(
    @SerialName("_id") val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val thumbnail: String? = null,
    val landscape: String? = null,
    val portrait: String? = null,
)

fun EpgProgram.displayTitle(): String =
    content?.title?.takeIf { it.isNotBlank() }
        ?: programName?.takeIf { it.isNotBlank() }
        ?: title?.takeIf { it.isNotBlank() }
        ?: "Live"

fun EpgProgram.displayDescription(): String? =
    content?.description?.takeIf { it.isNotBlank() }
        ?: description?.takeIf { it.isNotBlank() }

fun EpgProgram.thumbnailUrl(): String? =
    content?.thumbnail?.takeIf { it.isNotBlank() }
        ?: content?.landscape?.takeIf { it.isNotBlank() }
        ?: content?.portrait?.takeIf { it.isNotBlank() }
