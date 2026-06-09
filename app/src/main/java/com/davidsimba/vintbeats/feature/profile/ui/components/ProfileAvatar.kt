package com.davidsimba.vintbeats.feature.profile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageGrayDeep
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import java.io.File

@Composable
fun ProfileAvatar(
    photoPath: String?,
    photoVersion: Long,
    size: androidx.compose.ui.unit.Dp = 100.dp,
    showCameraBadge: Boolean = false,
    onClick: () -> Unit = {},
) {
    val context = LocalContext.current

    Box(modifier = Modifier.size(size)) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(CircleShape)
                .background(VintageGrayDeep)
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
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    tint = VintageGrayMid,
                    modifier = Modifier.size(50.dp),
                )
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
