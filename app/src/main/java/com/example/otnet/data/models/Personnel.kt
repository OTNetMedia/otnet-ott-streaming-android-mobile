package com.example.otnet.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Personnel(
    @SerialName("_id") val id: String? = null,
    val role: String? = null,
    val person: PersonRef? = null,
)

@Serializable
data class PersonRef(
    @SerialName("_id") val id: String? = null,
    val name: String? = null,
    val title: String? = null,
    val headshot: String? = null,
)

fun Personnel.displayName(): String? = person?.name?.takeIf { it.isNotBlank() }
fun Personnel.displayRole(): String? = role?.takeIf { it.isNotBlank() }
fun Personnel.headshotUrl(): String? = person?.headshot?.takeIf { it.isNotBlank() }
