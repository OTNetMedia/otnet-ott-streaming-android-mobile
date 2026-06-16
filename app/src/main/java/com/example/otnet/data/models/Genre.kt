package com.example.otnet.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenreRef(
    @SerialName("_id") val id: String? = null,
    val name: String? = null,
    val slug: String? = null,
)

@Serializable
data class GenreNode(
    @SerialName("_id") val id: String? = null,
    val name: String? = null,
    val slug: String? = null,
    val order: Int? = null,
    val parent: String? = null,
    val children: List<GenreNode> = emptyList(),
)
