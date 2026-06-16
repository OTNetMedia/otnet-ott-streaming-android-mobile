package com.example.otnet.ui.browse

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

sealed interface CategoryDetailUiState {
    data object Loading : CategoryDetailUiState
    data class Data(val items: List<Content>, val title: String?) : CategoryDetailUiState
    data class Error(val message: String) : CategoryDetailUiState
}

class CategoryDetailViewModel(
    private val service: OTNetService = AppDeps.service,
) : ViewModel() {
    private val _state = MutableStateFlow<CategoryDetailUiState>(CategoryDetailUiState.Loading)
    val state: StateFlow<CategoryDetailUiState> = _state.asStateFlow()

    private var loadedId: String? = null

    fun load(categoryId: String) {
        if (categoryId == loadedId) return
        loadedId = categoryId
        viewModelScope.launch {
            _state.value = CategoryDetailUiState.Loading
            _state.value = try {
                val page = withContext(Dispatchers.IO) { service.contentByCategory(categoryId) }
                if (page.items.isEmpty()) {
                    CategoryDetailUiState.Error("No titles in this category.")
                } else {
                    CategoryDetailUiState.Data(items = page.items, title = null)
                }
            } catch (t: Throwable) {
                Log.e("OTNet", "category $categoryId failed", t)
                CategoryDetailUiState.Error(t.message ?: "Failed to load")
            }
        }
    }

    fun retry() {
        val id = loadedId ?: return
        loadedId = null
        load(id)
    }
}
