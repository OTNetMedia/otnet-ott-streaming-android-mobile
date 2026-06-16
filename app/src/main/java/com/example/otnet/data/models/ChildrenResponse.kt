package com.example.otnet.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ChildrenResponse(
    val items: List<Content> = emptyList(),
)
