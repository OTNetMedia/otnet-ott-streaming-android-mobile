package com.example.otnet.ui.livetv

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.otnet.data.models.EpgChannel
import com.example.otnet.data.models.EpgProgram
import com.example.otnet.data.models.displayTitle
import com.example.otnet.data.models.thumbnailUrl
import com.example.otnet.ui.components.PlaceholderKind
import com.example.otnet.ui.components.StatePlaceholder
import com.example.otnet.ui.theme.OTNetBackground
import com.example.otnet.ui.theme.OTNetBorder
import com.example.otnet.ui.theme.OTNetCard
import com.example.otnet.ui.theme.OTNetMuted
import com.example.otnet.ui.theme.OTNetPrimary
import com.example.otnet.ui.theme.OTNetTextPrimary
import com.example.otnet.ui.theme.OTNetTextSecondary
import com.example.otnet.ui.theme.OTNetTextTertiary
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LiveTvScreen(
    onChannelTap: (String) -> Unit,
    viewModel: LiveTvViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(OTNetBackground),
    ) {
        Text(
            text = "Live TV",
            color = OTNetTextPrimary,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(start = 20.dp, top = 16.dp, bottom = 12.dp),
        )

        Box(modifier = Modifier.fillMaxSize()) {
            when (val s = state) {
                LiveTvUiState.Loading -> StatePlaceholder(PlaceholderKind.Loading)
                is LiveTvUiState.Error -> StatePlaceholder(
                    PlaceholderKind.Error(s.message, onRetry = viewModel::reload),
                )
                is LiveTvUiState.Data -> ChannelGrid(
                    channels = s.channels,
                    onChannelTap = onChannelTap,
                )
            }
        }
    }
}

@Composable
private fun ChannelGrid(
    channels: List<EpgChannel>,
    onChannelTap: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        items(channels, key = { it.channel?.id ?: it.hashCode().toString() }) { row ->
            ChannelRow(
                row = row,
                onChannelTap = onChannelTap,
            )
        }
    }
}

@Composable
private fun ChannelRow(
    row: EpgChannel,
    onChannelTap: (String) -> Unit,
) {
    val channel = row.channel ?: return
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable { channel.id?.let(onChannelTap) },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(OTNetCard)
                    .border(1.dp, OTNetBorder, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center,
            ) {
                if (!channel.logo.isNullOrBlank()) {
                    AsyncImage(
                        model = channel.logo,
                        contentDescription = channel.name,
                        modifier = Modifier.size(36.dp),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    Text(
                        text = (channel.name?.firstOrNull()?.uppercase() ?: "?"),
                        color = OTNetTextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = channel.name ?: "Channel",
                    color = OTNetTextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                channel.channelNumber?.let { num ->
                    Text(
                        text = "Channel $num",
                        color = OTNetTextTertiary,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        if (row.programs.isEmpty()) {
            Text(
                text = "No schedule available",
                color = OTNetTextTertiary,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 20.dp),
            ) {
                items(row.programs, key = { it.id ?: it.hashCode().toString() }) { program ->
                    ProgramCard(
                        program = program,
                        onClick = { channel.id?.let(onChannelTap) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgramCard(
    program: EpgProgram,
    onClick: () -> Unit,
) {
    val isLive = isLiveNow(program)
    Box(
        modifier = Modifier
            .width(240.dp)
            .height(130.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(OTNetCard)
            .border(
                width = if (isLive) 2.dp else 1.dp,
                color = if (isLive) OTNetPrimary else OTNetBorder,
                shape = RoundedCornerShape(10.dp),
            )
            .clickable { onClick() },
    ) {
        if (!program.thumbnailUrl().isNullOrBlank()) {
            AsyncImage(
                model = program.thumbnailUrl(),
                contentDescription = program.displayTitle(),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(OTNetBackground.copy(alpha = 0.55f)),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isLive) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(OTNetPrimary)
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                    ) {
                        Text(
                            text = "LIVE",
                            color = OTNetTextPrimary,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                    Spacer(Modifier.width(6.dp))
                }
                formatTime(program.startTime)?.let { start ->
                    Text(
                        text = start,
                        color = OTNetTextTertiary,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
            Text(
                text = program.displayTitle(),
                color = OTNetTextPrimary,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (program.thumbnailUrl().isNullOrBlank()) {
            // hairline tint so the card never looks empty
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(OTNetMuted),
            )
        }
    }
}

private val isoFormatter: DateTimeFormatter? = runCatching {
    DateTimeFormatter.ISO_OFFSET_DATE_TIME
}.getOrNull()

private val timeFormatter: DateTimeFormatter? = runCatching {
    DateTimeFormatter.ofPattern("HH:mm")
}.getOrNull()

private fun formatTime(iso: String?): String? {
    if (iso.isNullOrBlank()) return null
    val odt = runCatching { OffsetDateTime.parse(iso, isoFormatter) }.getOrNull() ?: return null
    val local = odt.atZoneSameInstant(ZoneId.systemDefault())
    return local.format(timeFormatter)
}

private fun isLiveNow(program: EpgProgram): Boolean {
    val start = program.startTime?.let { runCatching { OffsetDateTime.parse(it) }.getOrNull() }
        ?: return false
    val end = program.endTime?.let { runCatching { OffsetDateTime.parse(it) }.getOrNull() }
        ?: program.durationSeconds?.let { start.plusSeconds(it.toLong()) }
        ?: return false
    val now = Instant.now()
    return !now.isBefore(start.toInstant()) && now.isBefore(end.toInstant())
}
