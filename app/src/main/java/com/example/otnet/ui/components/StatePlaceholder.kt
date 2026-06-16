package com.example.otnet.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.otnet.ui.theme.OTNetPrimary
import com.example.otnet.ui.theme.OTNetTextPrimary
import com.example.otnet.ui.theme.OTNetTextSecondary

sealed interface PlaceholderKind {
    data object Loading : PlaceholderKind
    data class Empty(val message: String = "Nothing to show yet.") : PlaceholderKind
    data class Error(
        val message: String,
        val onRetry: (() -> Unit)? = null,
    ) : PlaceholderKind
}

@Composable
fun StatePlaceholder(
    kind: PlaceholderKind,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when (kind) {
            PlaceholderKind.Loading -> {
                CircularProgressIndicator(color = OTNetPrimary)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Loading…",
                    color = OTNetTextSecondary,
                    textAlign = TextAlign.Center,
                )
            }
            is PlaceholderKind.Empty -> {
                Text(
                    text = kind.message,
                    color = OTNetTextSecondary,
                    textAlign = TextAlign.Center,
                )
            }
            is PlaceholderKind.Error -> {
                Text(
                    text = "Something went wrong",
                    color = OTNetTextPrimary,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = kind.message,
                    color = OTNetTextSecondary,
                    textAlign = TextAlign.Center,
                )
                if (kind.onRetry != null) {
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = kind.onRetry,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = OTNetPrimary,
                            contentColor = OTNetTextPrimary,
                        ),
                    ) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}
