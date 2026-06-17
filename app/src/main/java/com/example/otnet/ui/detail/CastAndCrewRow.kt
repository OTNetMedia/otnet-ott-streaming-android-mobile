package com.example.otnet.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.otnet.data.models.Personnel
import com.example.otnet.data.models.displayName
import com.example.otnet.data.models.displayRole
import com.example.otnet.data.models.headshotUrl
import com.example.otnet.ui.theme.OTNetBorder
import com.example.otnet.ui.theme.OTNetMuted
import com.example.otnet.ui.theme.OTNetTextPrimary
import com.example.otnet.ui.theme.OTNetTextSecondary
import com.example.otnet.ui.theme.OTNetTextTertiary

@Composable
fun CastAndCrewRow(
    personnel: List<Personnel>,
    modifier: Modifier = Modifier,
) {
    val withNames = personnel.filter { it.displayName() != null }
    if (withNames.isEmpty()) return
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Cast & Crew",
            color = OTNetTextPrimary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 20.dp, top = 16.dp, bottom = 12.dp),
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
        ) {
            items(withNames, key = { it.id ?: it.hashCode().toString() }) { item ->
                CastCard(item)
            }
        }
    }
}

@Composable
private fun CastCard(p: Personnel) {
    Column(
        modifier = Modifier.width(96.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(OTNetMuted)
                .border(1.dp, OTNetBorder, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            val head = p.headshotUrl()
            if (!head.isNullOrBlank()) {
                AsyncImage(
                    model = head,
                    contentDescription = p.displayName(),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Text(
                    text = (p.displayName()?.firstOrNull()?.uppercase() ?: "?"),
                    color = OTNetTextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = p.displayName().orEmpty(),
            color = OTNetTextSecondary,
            style = MaterialTheme.typography.labelMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        p.displayRole()?.let { role ->
            Text(
                text = role,
                color = OTNetTextTertiary,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
            )
        }
    }
}
