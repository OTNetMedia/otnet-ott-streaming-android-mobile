package com.example.otnet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeOff
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage

/**
 * Netflix-style autoplay teaser. Starts muted on enter, loops, falls back
 * to [fallbackImage] until the first frame is ready. Releases the player
 * on dispose so navigating away doesn't leak audio/codec resources.
 */
@Composable
fun TeaserPlayer(
    teaserUrl: String,
    isDash: Boolean,
    fallbackImage: String?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    var muted by remember { mutableStateOf(true) }

    val player = remember(teaserUrl) {
        ExoPlayer.Builder(context).build().apply {
            volume = 0f
            repeatMode = Player.REPEAT_MODE_ALL
            playWhenReady = true
        }
    }

    LaunchedEffect(teaserUrl) {
        val item = MediaItem.Builder()
            .setUri(teaserUrl)
            .setMimeType(if (isDash) MimeTypes.APPLICATION_MPD else MimeTypes.APPLICATION_M3U8)
            .build()
        player.setMediaItem(item)
        player.prepare()
        player.playWhenReady = true
    }

    LaunchedEffect(muted) { player.volume = if (muted) 0f else 1f }

    DisposableEffect(player) {
        onDispose { player.release() }
    }

    Box(modifier = modifier.background(Color.Black)) {
        AsyncImage(
            model = fallbackImage,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    this.player = player
                    useController = false
                    setShowBuffering(PlayerView.SHOW_BUFFERING_NEVER)
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    setBackgroundColor(android.graphics.Color.BLACK)
                }
            },
            update = { it.player = player },
        )

        IconButton(
            onClick = { muted = !muted },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.45f),
                contentColor = Color.White,
            ),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(40.dp)
                .clip(CircleShape),
        ) {
            Icon(
                imageVector = if (muted) Icons.AutoMirrored.Outlined.VolumeOff
                else Icons.AutoMirrored.Outlined.VolumeUp,
                contentDescription = if (muted) "Unmute teaser" else "Mute teaser",
            )
        }
    }
}
