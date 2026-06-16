package com.example.otnet.ui.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.otnet.ui.components.PlaceholderKind
import com.example.otnet.ui.components.PosterCard
import com.example.otnet.ui.components.StatePlaceholder
import com.example.otnet.ui.theme.OTNetBackground
import com.example.otnet.ui.theme.OTNetTextPrimary

@Composable
fun CategoryDetailScreen(
    categoryId: String,
    onContentTap: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: CategoryDetailViewModel = viewModel(),
) {
    LaunchedEffect(categoryId) { viewModel.load(categoryId) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OTNetBackground),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 8.dp, end = 20.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = OTNetTextPrimary,
                )
            }
            val headerTitle = (state as? CategoryDetailUiState.Data)?.title ?: "Category"
            Text(
                text = headerTitle,
                color = OTNetTextPrimary,
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (val s = state) {
                CategoryDetailUiState.Loading -> StatePlaceholder(PlaceholderKind.Loading)
                is CategoryDetailUiState.Error -> StatePlaceholder(
                    PlaceholderKind.Error(s.message, onRetry = viewModel::retry),
                )
                is CategoryDetailUiState.Data -> LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 8.dp,
                        bottom = 40.dp,
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    items(s.items, key = { it.id }) { item ->
                        PosterCard(
                            content = item,
                            onClick = { onContentTap(item.id) },
                            modifier = Modifier.size(width = 140.dp, height = 254.dp),
                        )
                    }
                }
            }
        }
    }
}
