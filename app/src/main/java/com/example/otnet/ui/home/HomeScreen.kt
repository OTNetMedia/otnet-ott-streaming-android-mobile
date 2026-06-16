package com.example.otnet.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.otnet.data.models.HomepageRow
import com.example.otnet.data.models.primaryGenreName
import com.example.otnet.ui.components.ContentRow
import com.example.otnet.ui.components.HeroPager
import com.example.otnet.ui.components.PlaceholderKind
import com.example.otnet.ui.components.StatePlaceholder
import com.example.otnet.ui.theme.OTNetBackground

@Composable
fun HomeScreen(
    onContentTap: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OTNetBackground),
    ) {
        when (val s = state) {
            HomeUiState.Loading -> StatePlaceholder(PlaceholderKind.Loading)
            is HomeUiState.Error -> StatePlaceholder(
                PlaceholderKind.Error(s.message, onRetry = viewModel::reload),
            )
            is HomeUiState.Data -> HomeContent(
                hero = s.hero,
                rows = s.rows,
                onContentTap = onContentTap,
            )
        }
    }
}

@Composable
private fun HomeContent(
    hero: List<com.example.otnet.data.models.Content>,
    rows: List<HomepageRow>,
    onContentTap: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        item(key = "hero") {
            HeroPager(
                items = hero,
                onContentTap = onContentTap,
                onPlayTap = onContentTap,
            )
        }
        item(key = "row-gap-top") { Spacer(Modifier.height(8.dp)) }
        items(rows, key = { row -> row.genre?.id ?: row.genre?.name ?: row.hashCode().toString() }) { row ->
            val title = row.genre?.name
                ?: row.items.firstOrNull()?.primaryGenreName()
                ?: "Featured"
            ContentRow(
                title = title,
                items = row.items,
                tileType = row.tileType,
                onContentTap = onContentTap,
            )
            Spacer(Modifier.height(24.dp))
        }
        item(key = "footer-gap") { Spacer(Modifier.height(40.dp)) }
    }
}
