package com.example.otnet.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otnet.data.api.OTNetService
import com.example.otnet.data.models.Content
import com.example.otnet.ui.AppDeps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext

sealed interface SearchUiState {
    data object Idle : SearchUiState
    data object Loading : SearchUiState
    data class Data(val items: List<Content>) : SearchUiState
    data object Empty : SearchUiState
    data class Error(val message: String) : SearchUiState
}

@OptIn(FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val service: OTNetService = AppDeps.service,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _state = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    init {
        _query
            .debounce(300)
            .distinctUntilChanged()
            .flatMapLatest { q ->
                flow {
                    val trimmed = q.trim()
                    if (trimmed.length < 2) {
                        emit(SearchUiState.Idle)
                        return@flow
                    }
                    emit(SearchUiState.Loading)
                    emit(
                        try {
                            val page = withContext(Dispatchers.IO) {
                                service.searchContent(trimmed)
                            }
                            if (page.items.isEmpty()) SearchUiState.Empty
                            else SearchUiState.Data(page.items)
                        } catch (t: Throwable) {
                            Log.e("OTNet", "search '$trimmed' failed", t)
                            SearchUiState.Error(t.message ?: "Search failed")
                        }
                    )
                }
            }
            .onEach { _state.value = it }
            .launchIn(viewModelScope)
    }

    fun setQuery(q: String) { _query.value = q }
}
