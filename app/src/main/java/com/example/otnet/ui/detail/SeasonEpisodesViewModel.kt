package com.example.otnet.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otnet.data.api.OTNetService
import com.example.otnet.data.models.Content
import com.example.otnet.ui.AppDeps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface SeasonEpisodesUiState {
    data object Loading : SeasonEpisodesUiState
    data class Data(val items: List<Content>) : SeasonEpisodesUiState
    data class Error(val message: String) : SeasonEpisodesUiState
}

class SeasonEpisodesViewModel(
    private val service: OTNetService = AppDeps.service,
) : ViewModel() {
    private val _state = MutableStateFlow<SeasonEpisodesUiState>(SeasonEpisodesUiState.Loading)
    val state: StateFlow<SeasonEpisodesUiState> = _state.asStateFlow()

    private var loaded: String? = null

    fun load(seasonId: String) {
        if (loaded == seasonId) return
        loaded = seasonId
        viewModelScope.launch {
            _state.value = SeasonEpisodesUiState.Loading
            _state.value = try {
                val items = withContext(Dispatchers.IO) {
                    service.contentChildren(seasonId).items
                }.sortedBy { it.sortOrder ?: Int.MAX_VALUE }
                SeasonEpisodesUiState.Data(items)
            } catch (t: Throwable) {
                Log.e("OTNet", "season episodes $seasonId failed", t)
                SeasonEpisodesUiState.Error(t.message ?: "Failed to load episodes")
            }
        }
    }
}
