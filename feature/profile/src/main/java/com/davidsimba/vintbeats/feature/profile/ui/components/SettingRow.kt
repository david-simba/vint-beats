package com.davidsimba.vintbeats.feature.profile.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.designsystem.theme.colorAcento

private val CardColor = Color(0xFF1C1C1F)
private val Secondary = Color(0xFFA1A1AA)
private val Tertiary = Color(0xFF71717A)
private val Primary = Color(0xFFF4F4F5)

/** Título de sección en mayúsculas ("AJUSTES", "ACERCA DE"). */
@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title.uppercase(),
        color = Secondary,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
    )
}

/** Tarjeta con filas separadas por una línea de 1dp. */
@Composable
fun ProfileCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardColor),
        content = content,
    )
}

@Composable
fun ProfileRowDivider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF27272A)))
}

/** Botón "Editar perfil": pastilla de 32dp con borde y área táctil de 44dp. */
@Composable
fun ProfileEditPill(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(44.dp)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier
                .height(32.dp)
                .clip(CircleShape)
                .border(BorderStroke(1.dp, Color(0xFF3F3F46)), CircleShape)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(14.dp),
            )
            Spacer(Modifier.width(6.dp))
            Text(text = text, color = Primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** Tarjeta destacada "Apoya al creador"; toda ella es un solo elemento táctil. */
@Composable
fun ProfileSupportCard(title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardColor)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(colorAcento.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = null,
                tint = colorAcento,
                modifier = Modifier.size(20.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Primary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = subtitle, color = Secondary, fontSize = 13.sp)
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = Tertiary,
            modifier = Modifier.size(18.dp),
        )
    }
}

/** Fila de ajuste con interruptor; tocar cualquier parte de la fila lo cambia. */
@Composable
fun ProfileToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF27272A)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = Primary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = Secondary, fontSize = 12.sp)
        }
        Spacer(Modifier.width(12.dp))
        ProfileSwitch(checked)
    }
}

/** Interruptor de 52x32dp: solo dibuja el estado, el toque lo gestiona la fila. */
@Composable
private fun ProfileSwitch(checked: Boolean) {
    val track by animateColorAsState(
        if (checked) colorAcento else Color(0xFF3F3F46),
        tween(200),
        label = "switch_track",
    )
    val thumb by animateColorAsState(
        if (checked) Color.White else Secondary,
        tween(200),
        label = "switch_thumb",
    )
    val thumbX by animateDpAsState(if (checked) 24.dp else 4.dp, tween(200), label = "switch_x")
    Box(
        modifier = Modifier
            .size(width = 52.dp, height = 32.dp)
            .clip(CircleShape)
            .background(track),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbX)
                .size(24.dp)
                .clip(CircleShape)
                .background(thumb),
        )
    }
}

/** Fila de navegación de al menos 56dp, con valor opcional (p. ej. la versión) y chevron. */
@Composable
fun ProfileNavRow(title: String, trailingText: String? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 56.dp)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            color = Primary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        if (!trailingText.isNullOrBlank()) {
            Text(text = trailingText, color = Tertiary, fontSize = 13.sp)
            Spacer(Modifier.width(8.dp))
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = Tertiary,
            modifier = Modifier.size(18.dp),
        )
    }
}
