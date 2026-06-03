package com.davidsimba.vintbeats.feature.library.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.davidsimba.vintbeats.shared.components.ImageShape

internal data class LibraryItem(
    val icon: ImageVector,
    val iconTint: Color,
    val iconBg: Color,
    val title: String,
    val subtitle: String,
    val onClick: () -> Unit,
    val imageUrl: String? = null,
    val imageShape: ImageShape = ImageShape.Square,
)
