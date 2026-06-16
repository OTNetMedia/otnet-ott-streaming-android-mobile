package com.example.otnet.ui.browse

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.otnet.data.models.GenreNode
import com.example.otnet.ui.components.PlaceholderKind
import com.example.otnet.ui.components.StatePlaceholder
import com.example.otnet.ui.theme.OTNetBackground
import com.example.otnet.ui.theme.OTNetBorder
import com.example.otnet.ui.theme.OTNetCard
import com.example.otnet.ui.theme.OTNetMuted
import com.example.otnet.ui.theme.OTNetPrimary
import com.example.otnet.ui.theme.OTNetTextPrimary
import com.example.otnet.ui.theme.OTNetTextSecondary

@Composable
fun BrowseScreen(
    onCategoryTap: (String) -> Unit,
    viewModel: BrowseViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OTNetBackground),
    ) {
        when (val s = state) {
            BrowseUiState.Loading -> StatePlaceholder(PlaceholderKind.Loading)
            is BrowseUiState.Error -> StatePlaceholder(
                PlaceholderKind.Error(s.message, onRetry = viewModel::reload),
            )
            is BrowseUiState.Data -> CategoryList(
                categories = s.categories,
                onCategoryTap = onCategoryTap,
            )
        }
    }
}

@Composable
private fun CategoryList(
    categories: List<GenreNode>,
    onCategoryTap: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 16.dp,
            bottom = 40.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item("header") {
            Text(
                text = "Browse",
                color = OTNetTextPrimary,
                style = MaterialTheme.typography.headlineLarge,
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
            )
        }
        items(categories, key = { it.id ?: it.name ?: it.hashCode().toString() }) { node ->
            CategoryTile(node = node, onTap = { node.id?.let(onCategoryTap) })
        }
    }
}

@Composable
private fun CategoryTile(
    node: GenreNode,
    onTap: () -> Unit,
) {
    val gradient = Brush.horizontalGradient(
        0f to OTNetCard,
        1f to OTNetMuted,
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(gradient)
            .border(1.dp, OTNetBorder, RoundedCornerShape(12.dp))
            .clickable { onTap() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(OTNetPrimary),
            )
            Box(modifier = Modifier.size(16.dp))
            Text(
                text = node.name ?: "Untitled",
                color = OTNetTextPrimary,
                style = MaterialTheme.typography.titleMedium,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
            contentDescription = null,
            tint = OTNetTextSecondary,
        )
    }
}
