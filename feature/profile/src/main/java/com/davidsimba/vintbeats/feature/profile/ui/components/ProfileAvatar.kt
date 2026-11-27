package com.davidsimba.vintbeats.feature.profile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.davidsimba.vintbeats.core.designsystem.theme.VintageGrayDeep
import com.davidsimba.vintbeats.core.designsystem.theme.colorAcento
import java.io.File

/**
 * Avatar circular: la foto si existe o, si no, la inicial del nombre sobre el color de acento.
 * Con [showCameraBadge] todo el avatar es un botón (con descripción [clickLabel]) y lleva la insignia de cámara.
 */
@Composable
fun ProfileAvatar(
    photoPath: String?,
    photoVersion: Long,
    size: Dp = 100.dp,
    name: String = "",
    showCameraBadge: Boolean = false,
    clickLabel: String = "",
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .size(size)
            .then(
                if (showCameraBadge) {
                    // Sin recorte ni ripple rectangular: la insignia sobresale del círculo.
                    Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Button,
                            onClick = onClick,
                        )
                        .semantics { contentDescription = clickLabel }
                } else Modifier
            ),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(if (photoPath != null) VintageGrayDeep else colorAcento),
            contentAlignment = Alignment.Center,
        ) {
            if (photoPath != null) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(File(photoPath))
                        .memoryCacheKey("profile_photo_$photoVersion")
                        .diskCacheKey("profile_photo_$photoVersion")
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize(),
                )
            } else {
                val initial = name.trim().firstOrNull()?.uppercase().orEmpty()
                if (initial.isNotEmpty()) {
                    Text(
                        text = initial,
                        color = Color(0xFF0E0E10),
                        fontSize = (size.value * 0.4f).sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
        if (showCameraBadge) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color(0xFF0E0E10), CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF4F4F5)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    tint = Color(0xFF0E0E10),
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
