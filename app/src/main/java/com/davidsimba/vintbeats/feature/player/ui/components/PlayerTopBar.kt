package com.davidsimba.vintbeats.feature.player.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.shared.components.VintClickableText
import com.davidsimba.vintbeats.shared.theme.VintageWhite

@Composable
fun PlayerTopBar(
    onBack: () -> Unit,
    onMoreOptions: () -> Unit,
    playingFromName: String? = null,
    onPlayingFromClick: (() -> Unit)? = null,
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

        if (playingFromName != null) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.playing_from_label),
                    color = VintageWhite.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                )
                VintClickableText(
                    text = playingFromName,
                    onClick = onPlayingFromClick,
                    color = VintageWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        } else {
            Spacer(Modifier.weight(1f))
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
