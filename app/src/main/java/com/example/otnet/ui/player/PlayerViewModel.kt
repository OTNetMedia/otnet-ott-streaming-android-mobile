package com.example.otnet.ui.player

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otnet.data.api.OTNetService
import com.example.otnet.data.models.Content
import com.example.otnet.data.models.MediaVariant
import com.example.otnet.data.models.allVariants
import com.example.otnet.ui.AppDeps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data class Ready(
        val content: Content,
        val variant: MediaVariant,
        val variantIndex: Int,
    ) : PlayerUiState
    data class Error(val message: String) : PlayerUiState
}

class PlayerViewModel(
    private val service: OTNetService = AppDeps.service,
) : ViewModel() {
    private val _state = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val state: StateFlow<PlayerUiState> = _state.asStateFlow()

    private var loadedKey: String? = null

    fun load(contentId: String, variantIndex: Int) {
        val key = "$contentId/$variantIndex"
        if (loadedKey == key) return
        loadedKey = key
        viewModelScope.launch {
            _state.value = PlayerUiState.Loading
            _state.value = try {
                val content = withContext(Dispatchers.IO) { service.contentDetail(contentId) }
                val variants = content.allVariants()
                if (variants.isEmpty()) {
                    PlayerUiState.Error("No playable variants on this title.")
                } else {
                    val safeIndex = variantIndex.coerceIn(0, variants.lastIndex)
                    PlayerUiState.Ready(content, variants[safeIndex], safeIndex)
                }
            } catch (t: Throwable) {
                Log.e("OTNet", "player load $contentId failed", t)
                PlayerUiState.Error(t.message ?: "Failed to load")
            }
        }
    }
}
