package com.davidsimba.vintbeats.feature.profile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.davidsimba.vintbeats.core.designsystem.theme.VintageBgDark
import com.davidsimba.vintbeats.core.designsystem.theme.VintageGrayDeep
import com.davidsimba.vintbeats.core.designsystem.theme.colorAcento
import com.davidsimba.vintbeats.core.designsystem.theme.VintageWhite
import java.io.File

@Composable
fun ProfileAvatar(
    photoPath: String?,
    photoVersion: Long,
    size: androidx.compose.ui.unit.Dp = 100.dp,
    name: String = "",
    showCameraBadge: Boolean = false,
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current

    Box(modifier = Modifier.size(size)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(if (photoPath != null) VintageGrayDeep else colorAcento)
                .then(if (showCameraBadge) Modifier.clickable { onClick() } else Modifier),
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
                        fontSize = (size.value * 0.42f).sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
            }
        }
        if (showCameraBadge) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(VintageWhite),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.CameraAlt,
                    contentDescription = null,
                    tint = VintageBgDark,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}
