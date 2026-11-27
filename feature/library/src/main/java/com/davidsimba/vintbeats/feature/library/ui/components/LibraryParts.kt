package com.davidsimba.vintbeats.feature.library.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.designsystem.theme.VintRed
import com.davidsimba.vintbeats.core.designsystem.theme.colorAcento

/** Pastilla de filtro de 36dp con área táctil de 44dp. */
@Composable
internal fun LibraryFilterPill(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(44.dp)
            .selectable(selected = selected, role = Role.Tab, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .height(36.dp)
                .clip(CircleShape)
                .then(
                    if (selected) Modifier.background(LibraryPrimary)
                    else Modifier.border(BorderStroke(1.dp, Color(0xFF3F3F46)), CircleShape)
                )
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                color = if (selected) Color(0xFF0E0E10) else LibraryPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

/** Dos accesos fijos lado a lado: Me gusta y Descargas. */
@Composable
internal fun LibraryPinnedCards(
    likedTitle: String,
    likedSubtitle: String,
    downloadsTitle: String,
    downloadsSubtitle: String,
    onLikedClick: () -> Unit,
    onDownloadsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        PinnedCard(
            title = likedTitle,
            subtitle = likedSubtitle,
            iconBg = colorAcento.copy(alpha = 0.16f),
            onClick = onLikedClick,
            modifier = Modifier.weight(1f),
        ) {
            Icon(Icons.Rounded.Favorite, contentDescription = null, tint = colorAcento, modifier = Modifier.size(24.dp))
        }
        PinnedCard(
            title = downloadsTitle,
            subtitle = downloadsSubtitle,
            iconBg = Color(0xFF2A2A2E),
            onClick = onDownloadsClick,
            modifier = Modifier.weight(1f),
        ) {
            Icon(
                Icons.Outlined.FileDownload,
                contentDescription = null,
                tint = Color(0xFFE4E4E7),
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

@Composable
private fun PinnedCard(
    title: String,
    subtitle: String,
    iconBg: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(LibraryCardColor)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) { icon() }
        Column {
            Text(text = title, color = LibraryPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = subtitle, color = LibrarySecondary, fontSize = 12.sp)
        }
    }
}

/** Estado vacío: borde punteado, ícono, título, texto y, si aplica, botón de crear playlist. */
@Composable
internal fun LibraryEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    val dashColor = Color(0xFF3F3F46)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawRoundRect(
                    color = dashColor,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10.dp.toPx(), 8.dp.toPx())),
                    ),
                )
            }
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(LibraryCardColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                contentDescription = null,
                tint = LibraryPrimary,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text = title,
            color = LibraryPrimary,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = message,
            color = LibrarySecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(max = 260.dp),
        )
        if (actionLabel != null) {
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .height(44.dp)
                    .clip(CircleShape)
                    .background(VintRed)
                    .clickable(role = Role.Button, onClick = onAction)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = actionLabel,
                    color = Color(0xFF0E0E10),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}
