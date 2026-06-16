package com.example.otnet.ui.player

import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.C
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.otnet.data.models.PlaybackBlock
import com.example.otnet.ui.components.PlaceholderKind
import com.example.otnet.ui.components.StatePlaceholder

@Composable
fun PlayerScreen(
    contentId: String,
    mediaIndex: Int,
    onClose: () -> Unit,
    viewModel: PlayerViewModel = viewModel(),
) {
    LaunchedEffect(contentId, mediaIndex) { viewModel.load(contentId, mediaIndex) }
    val state by viewModel.state.collectAsStateWithLifecycle()

    KeepScreenOn()
    BackHandler(onBack = onClose)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        when (val s = state) {
            PlayerUiState.Loading -> StatePlaceholder(PlaceholderKind.Loading)
            is PlayerUiState.Error -> StatePlaceholder(PlaceholderKind.Error(s.message))
            is PlayerUiState.Ready -> ExoPlayerSurface(playback = s.playback)
        }

        IconButton(
            onClick = onClose,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color.Black.copy(alpha = 0.4f),
                contentColor = Color.White,
            ),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
        ) {
            Icon(Icons.Outlined.Close, contentDescription = "Close player")
        }
    }
}

@Composable
private fun ExoPlayerSurface(playback: PlaybackBlock) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(10_000)
            .setSeekForwardIncrementMs(10_000)
            .build()
            .apply {
                repeatMode = Player.REPEAT_MODE_OFF
                playWhenReady = true
            }
    }

    LaunchedEffect(playback.masterUrl, playback.sessionToken) {
        val item = buildMediaItem(playback)
        player.setMediaItem(item)
        player.seekTo(0, C.TIME_UNSET)
        player.prepare()
        player.playWhenReady = true
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                android.util.Log.e("OTNet", "ExoPlayer error: ${error.errorCodeName}", error)
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            PlayerView(ctx).apply {
                this.player = player
                useController = true
                setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                setBackgroundColor(android.graphics.Color.BLACK)
            }
        },
        update = { view -> view.player = player },
    )
}

@Composable
private fun KeepScreenOn() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? android.app.Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
