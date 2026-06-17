package com.example.otnet.ui.livetv

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otnet.data.api.OTNetService
import com.example.otnet.data.models.EpgChannel
import com.example.otnet.ui.AppDeps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface LiveTvUiState {
    data object Loading : LiveTvUiState
    data class Data(val channels: List<EpgChannel>) : LiveTvUiState
    data class Error(val message: String) : LiveTvUiState
}

class LiveTvViewModel(
    private val service: OTNetService = AppDeps.service,
) : ViewModel() {
    private val _state = MutableStateFlow<LiveTvUiState>(LiveTvUiState.Loading)
    val state: StateFlow<LiveTvUiState> = _state.asStateFlow()

    init { reload() }

    fun reload() {
        viewModelScope.launch {
            _state.value = LiveTvUiState.Loading
            _state.value = try {
                val epg = withContext(Dispatchers.IO) {
                    service.epg(backHours = 0, aheadHours = 2)
                }
                if (epg.channels.isEmpty()) {
                    LiveTvUiState.Error("No live channels configured.")
                } else {
                    LiveTvUiState.Data(epg.channels)
                }
            } catch (t: Throwable) {
                Log.e("OTNet", "epg load failed", t)
                LiveTvUiState.Error(t.message ?: "Failed to load guide")
            }
        }
    }
}
