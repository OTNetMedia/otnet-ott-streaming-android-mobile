package com.example.otnet.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val OTNetDarkColors = darkColorScheme(
    primary = OTNetPrimary,
    onPrimary = OTNetTextPrimary,
    secondary = OTNetPrimary,
    onSecondary = OTNetTextPrimary,
    background = OTNetBackground,
    onBackground = OTNetTextPrimary,
    surface = OTNetCard,
    onSurface = OTNetTextPrimary,
    surfaceVariant = OTNetMuted,
    onSurfaceVariant = OTNetTextSecondary,
    outline = OTNetBorder,
    error = androidx.compose.ui.graphics.Color(0xFFFF5C7A),
)

@Composable
fun OTNetTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = OTNetDarkColors,
        typography = OTNetTypography,
        content = content,
    )
}
