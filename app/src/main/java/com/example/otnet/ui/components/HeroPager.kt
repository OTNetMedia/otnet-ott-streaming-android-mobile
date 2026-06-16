@file:OptIn(ExperimentalFoundationApi::class)

package com.example.otnet.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.otnet.data.models.Content
import com.example.otnet.data.models.displayTitle
import com.example.otnet.data.models.heroBackdropUrl
import com.example.otnet.data.models.primaryGenreName
import com.example.otnet.ui.theme.OTNetBackground
import com.example.otnet.ui.theme.OTNetPrimary
import com.example.otnet.ui.theme.OTNetTextPrimary
import com.example.otnet.ui.theme.OTNetTextSecondary
import com.example.otnet.ui.theme.OTNetTextTertiary
import kotlinx.coroutines.delay

@Composable
fun HeroPager(
    items: List<Content>,
    onContentTap: (String) -> Unit,
    onPlayTap: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { items.size })

    LaunchedEffect(items.size) {
        if (items.size <= 1) return@LaunchedEffect
        while (true) {
            delay(8_000)
            val next = (pagerState.currentPage + 1) % items.size
            pagerState.animateScrollToPage(next)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(560.dp),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            val content = items[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onContentTap(content.id) },
            ) {
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
                                0.55f to OTNetBackground.copy(alpha = 0.55f),
                                1f to OTNetBackground,
                            ),
                        ),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                0f to OTNetBackground.copy(alpha = 0.65f),
                                0.5f to Color.Transparent,
                            ),
                        ),
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
                        .fillMaxWidth(0.85f),
                ) {
                    content.primaryGenreName()?.let { genre ->
                        Text(
                            text = genre.uppercase(),
                            color = OTNetPrimary,
                            style = MaterialTheme.typography.labelMedium,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    Text(
                        text = content.displayTitle(),
                        color = OTNetTextPrimary,
                        style = MaterialTheme.typography.displayLarge,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    content.description?.takeIf { it.isNotBlank() }?.let { desc ->
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = desc,
                            color = OTNetTextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            onClick = { onPlayTap(content.id) },
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
                            onClick = { onContentTap(content.id) },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = OTNetTextPrimary,
                            ),
                        ) {
                            Text("More info", style = MaterialTheme.typography.labelLarge)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            repeat(items.size.coerceAtMost(8)) { i ->
                val selected = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .size(if (selected) 18.dp else 6.dp, 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (selected) OTNetTextPrimary else OTNetTextTertiary,
                        ),
                )
            }
        }
    }
}
