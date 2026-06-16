package com.example.otnet.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.otnet.data.api.OTNetService
import com.example.otnet.data.models.Content
import com.example.otnet.data.models.isSeries
import com.example.otnet.ui.AppDeps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface ContentDetailUiState {
    data object Loading : ContentDetailUiState
    data class Data(
        val content: Content,
        val children: List<Content>,
    ) : ContentDetailUiState
    data class Error(val message: String) : ContentDetailUiState
}

class ContentDetailViewModel(
    private val service: OTNetService = AppDeps.service,
) : ViewModel() {
    private val _state = MutableStateFlow<ContentDetailUiState>(ContentDetailUiState.Loading)
    val state: StateFlow<ContentDetailUiState> = _state.asStateFlow()

    private var loadedId: String? = null

    fun load(contentId: String) {
        if (loadedId == contentId) return
        loadedId = contentId
        viewModelScope.launch {
            _state.value = ContentDetailUiState.Loading
            _state.value = try {
                withContext(Dispatchers.IO) {
                    val content = service.contentDetail(contentId)
                    val children = if (content.isSeries()) {
                        runCatching { service.contentChildren(contentId).items }.getOrDefault(emptyList())
                    } else emptyList()
                    ContentDetailUiState.Data(content, children.sortedBy { it.sortOrder ?: Int.MAX_VALUE })
                }
            } catch (t: Throwable) {
                Log.e("OTNet", "content $contentId failed", t)
                ContentDetailUiState.Error(t.message ?: "Failed to load")
            }
        }
    }

    fun retry() {
        val id = loadedId ?: return
        loadedId = null
        load(id)
    }
}
