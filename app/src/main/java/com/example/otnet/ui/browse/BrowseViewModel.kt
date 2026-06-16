package com.example.otnet.ui.browse

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otnet.data.api.OTNetService
import com.example.otnet.data.models.GenreNode
import com.example.otnet.ui.AppDeps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface BrowseUiState {
    data object Loading : BrowseUiState
    data class Data(val categories: List<GenreNode>) : BrowseUiState
    data class Error(val message: String) : BrowseUiState
}

class BrowseViewModel(
    private val service: OTNetService = AppDeps.service,
) : ViewModel() {
    private val _state = MutableStateFlow<BrowseUiState>(BrowseUiState.Loading)
    val state: StateFlow<BrowseUiState> = _state.asStateFlow()

    init { reload() }

    fun reload() {
        viewModelScope.launch {
            _state.value = BrowseUiState.Loading
            _state.value = try {
                val tree = withContext(Dispatchers.IO) { service.categoriesTree() }
                if (tree.isEmpty()) {
                    BrowseUiState.Error("No categories configured.")
                } else {
                    BrowseUiState.Data(categories = tree)
                }
            } catch (t: Throwable) {
                Log.e("OTNet", "categories load failed", t)
                BrowseUiState.Error(t.message ?: "Failed to load")
            }
        }
    }
}
