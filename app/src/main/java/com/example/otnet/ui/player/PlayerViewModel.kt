package com.example.otnet.ui.player

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otnet.data.api.OTNetService
import com.example.otnet.data.models.PlaybackBlock
import com.example.otnet.data.models.VodMintRequest
import com.example.otnet.ui.AppDeps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface PlayerUiState {
    data object Loading : PlayerUiState
    data class Ready(val playback: PlaybackBlock) : PlayerUiState
    data class Error(val message: String) : PlayerUiState
}

class PlayerViewModel(
    private val service: OTNetService = AppDeps.service,
) : ViewModel() {
    private val _state = MutableStateFlow<PlayerUiState>(PlayerUiState.Loading)
    val state: StateFlow<PlayerUiState> = _state.asStateFlow()

    private var loadedKey: String? = null

    fun load(contentId: String, mediaIndex: Int) {
        val key = "$contentId/$mediaIndex"
        if (loadedKey == key) return
        loadedKey = key
        viewModelScope.launch {
            _state.value = PlayerUiState.Loading
            _state.value = try {
                val mint = withContext(Dispatchers.IO) {
                    service.vodMint(VodMintRequest(contentId = contentId, mediaIndex = mediaIndex))
                }
                val playback = mint.playback
                when {
                    playback == null -> PlayerUiState.Error("Mint response had no playback block.")
                    playback.masterUrl.isNullOrBlank() ->
                        PlayerUiState.Error("Mint response had no masterUrl.")
                    else -> PlayerUiState.Ready(playback)
                }
            } catch (t: Throwable) {
                Log.e("OTNet", "vod mint failed for $contentId", t)
                PlayerUiState.Error(t.message ?: "Failed to mint playback session")
            }
        }
    }
}
