package com.example.otnet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.otnet.data.models.Content
import com.example.otnet.data.models.DeviceProgressItem
import com.example.otnet.data.models.displayTitle
import com.example.otnet.data.models.fractionComplete
import com.example.otnet.data.models.heroBackdropUrl
import com.example.otnet.data.models.landscapeUrl
import com.example.otnet.ui.theme.OTNetBorder
import com.example.otnet.ui.theme.OTNetCard
import com.example.otnet.ui.theme.OTNetMuted
import com.example.otnet.ui.theme.OTNetPrimary
import com.example.otnet.ui.theme.OTNetTextPrimary
import com.example.otnet.ui.theme.OTNetTextSecondary

@Composable
fun ContinueWatchingRow(
    items: List<DeviceProgressItem>,
    onResume: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Continue Watching",
            color = OTNetTextPrimary,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(12.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
        ) {
            items(items, key = { it.contentId ?: it.hashCode().toString() }) { item ->
                val content = item.content ?: return@items
                ResumeCard(
                    content = content,
                    fraction = item.fractionComplete(),
                    onClick = { onResume(content.id) },
                )
            }
        }
    }
}

@Composable
private fun ResumeCard(
    content: Content,
    fraction: Float,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .clickable { onClick() },
    ) {
        Box(
            modifier = Modifier
                .size(width = 280.dp, height = 158.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(OTNetCard)
                .border(1.dp, OTNetBorder, RoundedCornerShape(12.dp)),
        ) {
            AsyncImage(
                model = content.landscapeUrl() ?: content.heroBackdropUrl(),
                contentDescription = content.displayTitle(),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(OTNetCard.copy(alpha = 0.85f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Resume",
                    tint = OTNetTextPrimary,
                )
            }
            // progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .align(Alignment.BottomStart)
                    .background(OTNetMuted),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .fillMaxHeight()
                        .background(OTNetPrimary),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = content.displayTitle(),
            color = OTNetTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
