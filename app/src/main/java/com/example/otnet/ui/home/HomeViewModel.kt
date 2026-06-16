package com.example.otnet.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otnet.data.api.OTNetService
import com.example.otnet.data.models.Content
import com.example.otnet.data.models.HomepageRow
import com.example.otnet.ui.AppDeps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Data(val hero: List<Content>, val rows: List<HomepageRow>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val service: OTNetService = AppDeps.service,
) : ViewModel() {
    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init { reload() }

    fun reload() {
        viewModelScope.launch {
            _state.value = HomeUiState.Loading
            _state.value = try {
                val r = withContext(Dispatchers.IO) { service.homepage() }
                if (r.hero.isEmpty() && r.rows.isEmpty()) {
                    HomeUiState.Error("Empty homepage. Check your /homepage configuration.")
                } else {
                    HomeUiState.Data(hero = r.hero, rows = r.rows)
                }
            } catch (t: Throwable) {
                Log.e("OTNet", "homepage load failed", t)
                HomeUiState.Error(t.message ?: "Failed to load")
            }
        }
    }
}
