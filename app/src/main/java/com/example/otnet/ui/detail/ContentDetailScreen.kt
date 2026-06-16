package com.example.otnet.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.otnet.data.models.Content
import com.example.otnet.data.models.displayTitle
import com.example.otnet.data.models.heroBackdropUrl
import com.example.otnet.data.models.isSeries
import com.example.otnet.data.models.posterUrl
import com.example.otnet.data.models.primaryGenreName
import com.example.otnet.data.models.runtimeMinutes
import com.example.otnet.ui.components.LandscapeCard
import com.example.otnet.ui.components.PlaceholderKind
import com.example.otnet.ui.components.StatePlaceholder
import com.example.otnet.ui.theme.OTNetBackground
import com.example.otnet.ui.theme.OTNetBorder
import com.example.otnet.ui.theme.OTNetMuted
import com.example.otnet.ui.theme.OTNetTextPrimary
import com.example.otnet.ui.theme.OTNetTextSecondary
import com.example.otnet.ui.theme.OTNetTextTertiary

@Composable
fun ContentDetailScreen(
    contentId: String,
    onPlay: (Int) -> Unit,
    onBack: () -> Unit,
    viewModel: ContentDetailViewModel = viewModel(),
) {
    LaunchedEffect(contentId) { viewModel.load(contentId) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OTNetBackground),
    ) {
        when (val s = state) {
            ContentDetailUiState.Loading -> StatePlaceholder(PlaceholderKind.Loading)
            is ContentDetailUiState.Error -> StatePlaceholder(
                PlaceholderKind.Error(s.message, onRetry = viewModel::retry),
            )
            is ContentDetailUiState.Data -> DetailContent(
                content = s.content,
                children = s.children,
                onBack = onBack,
                onPlay = onPlay,
                onChildTap = { /* episode tap → could open child detail in a follow-up */ },
                onPlayChild = { _, _ -> /* episode play handled later */ },
            )
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(8.dp),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                tint = OTNetTextPrimary,
            )
        }
    }
}

@Composable
private fun DetailContent(
    content: Content,
    children: List<Content>,
    onBack: () -> Unit,
    onPlay: (Int) -> Unit,
    onChildTap: (String) -> Unit,
    onPlayChild: (String, Int) -> Unit,
) {
    val canPlay = content.media.any { it.variants.isNotEmpty() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp),
    ) {
        item("hero") { DetailHero(content) }
        item("meta") {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                MetaPills(content)
                Spacer(Modifier.height(16.dp))
                ActionRow(
                    canPlay = canPlay,
                    onPlay = { onPlay(0) },
                )
                content.description?.takeIf { it.isNotBlank() }?.let { desc ->
                    Spacer(Modifier.height(20.dp))
                    Text(
                        text = desc,
                        color = OTNetTextSecondary,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                if (!canPlay) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "No playable variant available on this device.",
                        color = OTNetTextTertiary,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
        if (children.isNotEmpty()) {
            item("episodes-header") {
                Text(
                    text = if (content.isSeries()) "Episodes" else "Related",
                    color = OTNetTextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 12.dp),
                )
            }
            items(children.size) { idx ->
                EpisodeRow(
                    episode = children[idx],
                    index = idx + 1,
                    onTap = { onChildTap(children[idx].id) },
                    onPlay = { onPlayChild(children[idx].id, 0) },
                )
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun DetailHero(content: Content) {
    Box(modifier = Modifier.fillMaxWidth().height(420.dp)) {
        AsyncImage(
            model = content.heroBackdropUrl(),
            contentDescription = content.displayTitle(),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.6f to OTNetBackground.copy(alpha = 0.6f),
                        1f to OTNetBackground,
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
        ) {
            content.titleImage?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = content.displayTitle(),
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .height(120.dp),
                    contentScale = ContentScale.Fit,
                )
            } ?: Text(
                text = content.displayTitle(),
                color = OTNetTextPrimary,
                style = MaterialTheme.typography.displayLarge,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MetaPills(content: Content) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        content.ageRating?.takeIf { it.isNotBlank() }?.let { Pill(it) }
        content.contentType?.takeIf { it.isNotBlank() }?.let { Pill(it.replaceFirstChar { c -> c.uppercase() }) }
        content.runtimeMinutes()?.let { Pill("${it}m") }
        content.primaryGenreName()?.let { Pill(it) }
    }
}

@Composable
private fun Pill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(OTNetMuted)
            .border(1.dp, OTNetBorder, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            color = OTNetTextSecondary,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun ActionRow(
    canPlay: Boolean,
    onPlay: () -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = onPlay,
            enabled = canPlay,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OTNetTextPrimary,
                contentColor = OTNetBackground,
            ),
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Play", style = MaterialTheme.typography.labelLarge)
        }
        OutlinedButton(
            onClick = { /* no-op for starter */ },
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = OTNetTextPrimary),
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("My List", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun EpisodeRow(
    episode: Content,
    index: Int,
    onTap: () -> Unit,
    onPlay: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(OTNetMuted)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "$index",
            color = OTNetTextTertiary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.width(32.dp),
        )
        Box(
            modifier = Modifier
                .size(width = 140.dp, height = 80.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(OTNetBackground),
        ) {
            AsyncImage(
                model = episode.posterUrl() ?: episode.heroBackdropUrl(),
                contentDescription = episode.displayTitle(),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = episode.displayTitle(),
                color = OTNetTextPrimary,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            episode.description?.takeIf { it.isNotBlank() }?.let {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = it,
                    color = OTNetTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        IconButton(onClick = onPlay) {
            Icon(
                imageVector = Icons.Filled.PlayArrow,
                contentDescription = "Play episode",
                tint = OTNetTextPrimary,
            )
        }
    }
    @Suppress("UNUSED_EXPRESSION") onTap
}
