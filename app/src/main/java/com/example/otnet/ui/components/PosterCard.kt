package com.example.otnet.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.otnet.data.models.Content
import com.example.otnet.data.models.displayTitle
import com.example.otnet.data.models.posterUrl
import com.example.otnet.ui.theme.OTNetBorder
import com.example.otnet.ui.theme.OTNetMuted
import com.example.otnet.ui.theme.OTNetPrimaryGlow
import com.example.otnet.ui.theme.OTNetTextSecondary

@Composable
fun PosterCard(
    content: Content,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val scale by animateFloatAsState(if (focused) 1.04f else 1f, label = "posterScale")

    Column(
        modifier = modifier
            .width(140.dp)
            .scale(scale)
            .clickable(interactionSource = interaction, indication = null) { onClick() },
    ) {
        Box(
            modifier = Modifier
                .size(width = 140.dp, height = 210.dp)
                .shadow(if (focused) 18.dp else 0.dp, RoundedCornerShape(12.dp), spotColor = OTNetPrimaryGlow)
                .clip(RoundedCornerShape(12.dp))
                .background(OTNetMuted)
                .border(BorderStroke(1.dp, OTNetBorder), RoundedCornerShape(12.dp)),
        ) {
            AsyncImage(
                model = content.posterUrl(),
                contentDescription = content.displayTitle(),
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = content.displayTitle(),
            color = OTNetTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
        )
    }
}

@Suppress("unused")
private val _alignHint = Alignment.Center
