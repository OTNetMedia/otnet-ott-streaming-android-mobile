package com.example.otnet.data.models

import kotlinx.serialization.Serializable

@Serializable
data class HomepageResponse(
    val hero: List<Content> = emptyList(),
    val rows: List<HomepageRow> = emptyList(),
)

@Serializable
data class HomepageRow(
    val items: List<Content> = emptyList(),
    val tileType: String = "portrait",
    val tileStyle: String? = null,
    val genre: GenreRef? = null,
)
