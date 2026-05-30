package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.QueueMusic
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun PlayerTopBar(
    onBack: () -> Unit,
    onQueueOpen: () -> Unit,
    onMoreOptions: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = VintageWhite
            )
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onQueueOpen) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.QueueMusic,
                contentDescription = "Queue",
                tint = VintageWhite
            )
        }
        IconButton(onClick = onMoreOptions) {
            Icon(
                imageVector = Icons.Rounded.MoreVert,
                contentDescription = "More options",
                tint = VintageWhite
            )
        }
    }
}
