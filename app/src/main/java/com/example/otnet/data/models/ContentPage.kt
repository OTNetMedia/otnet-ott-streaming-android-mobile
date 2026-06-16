package com.example.otnet.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ContentPage(
    val items: List<Content> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val totalPages: Int = 1,
)
