package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.Player
import com.davidsimba.vintbeats.feature.player.R

/**
 * Controles del reproductor, con el mismo diseño que los de la pantalla de letras:
 * barra blanca arrastrable, tiempos al 75% y botón de play blanco de 64dp.
 */
@Composable
fun PlayerControls(
    isPlaying: Boolean,
    isLoading: Boolean,
    positionMs: Long,
    durationMs: Long,
    backgroundColor: Color,
    onSeek: (Long) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSkipNext: () -> Unit,
    modifier: Modifier = Modifier,
    repeatMode: Int = Player.REPEAT_MODE_OFF,
    shuffleEnabled: Boolean = false,
    onToggleRepeat: () -> Unit = {},
    onToggleShuffle: () -> Unit = {},
) {
    val inactive = Color(0xFFA1A1AA)
    Column(modifier = modifier.fillMaxWidth()) {
        LyricsProgressBar(
            positionMs = if (isLoading) 0L else positionMs,
            durationMs = if (isLoading) 0L else durationMs,
            onSeek = onSeek,
        )
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = if (isLoading) "--:--" else formatTime(positionMs),
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = if (isLoading) "--:--" else formatTime(durationMs),
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 12.sp,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onToggleShuffle, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = Icons.Rounded.Shuffle,
                    contentDescription = stringResource(R.string.player_shuffle),
                    tint = if (shuffleEnabled) Color.White else inactive,
                    modifier = Modifier.size(24.dp),
                )
            }
            IconButton(onClick = onSkipPrevious, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Rounded.SkipPrevious,
                    contentDescription = stringResource(R.string.lyrics_previous),
                    tint = Color.White,
                    modifier = Modifier.size(34.dp),
                )
            }
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .clickable(role = Role.Button, onClick = onTogglePlayPause),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = stringResource(
                        if (isPlaying) R.string.lyrics_pause else R.string.lyrics_play
                    ),
                    tint = backgroundColor,
                    modifier = Modifier.size(36.dp),
                )
            }
            IconButton(onClick = onSkipNext, modifier = Modifier.size(48.dp)) {
                Icon(
                    imageVector = Icons.Rounded.SkipNext,
                    contentDescription = stringResource(R.string.lyrics_next),
                    tint = Color.White,
                    modifier = Modifier.size(34.dp),
                )
            }
            IconButton(onClick = onToggleRepeat, modifier = Modifier.size(44.dp)) {
                Icon(
                    imageVector = if (repeatMode == Player.REPEAT_MODE_ONE) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                    contentDescription = stringResource(R.string.player_repeat),
                    tint = if (repeatMode == Player.REPEAT_MODE_OFF) inactive else Color.White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}
