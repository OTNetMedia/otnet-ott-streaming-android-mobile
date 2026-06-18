package com.example.otnet.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Active profile index (key into `Viewer.profiles[]`) sent as `X-Profile-Index`
 * on every catalog + playback request. The server defaults to 0 when the
 * header is missing, which silently bypasses kids-profile rating gates — so
 * we always emit the header, even before there's a profile picker UI.
 *
 * When auth + profile picker land, the picker should call [setActive] and any
 * cached UI (Continue Watching, My List) should refresh.
 */
object ProfileStore {
    private val _index = MutableStateFlow(0)
    val index: StateFlow<Int> = _index.asStateFlow()

    fun setActive(profileIndex: Int) {
        _index.value = profileIndex.coerceAtLeast(0)
    }
}
