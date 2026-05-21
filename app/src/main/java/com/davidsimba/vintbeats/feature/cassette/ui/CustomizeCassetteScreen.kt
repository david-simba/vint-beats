package com.davidsimba.vintbeats.feature.cassette.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.Gradient
import androidx.compose.material.icons.rounded.LinearScale
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.shared.components.ColorDot
import com.davidsimba.vintbeats.shared.components.SectionLabel
import com.davidsimba.vintbeats.shared.components.StyleToggle
import com.davidsimba.vintbeats.shared.components.TrackCard
import com.davidsimba.vintbeats.shared.components.cassette.CassetteView
import com.davidsimba.vintbeats.shared.theme.VintageBgBase
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

private val cassetteColors = listOf(
    Color(0xFF1A1510),
    Color(0xFF2D1F3D),
    Color(0xFF1F2D3D),
    Color(0xFF1F3D2A),
    Color(0xFF3D1F1F),
    Color(0xFF3D3428),
    Color(0xFFE8E0D0),
)

private val lineColors = listOf(
    Color(0xFFE85D5D),
    Color(0xFFE8A23A),
    Color(0xFFE8E03A),
    Color(0xFF5DB85D),
    Color(0xFF5D8DE8),
    Color(0xFFC8A87A),
    Color(0xFFFFFFFF),
)

@Composable
fun CustomizeCassetteScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    viewModel: CassetteSharedViewModel = hiltViewModel(),
) {
    val config by viewModel.cassetteConfig.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        viewModel.onScreenResume()
        onDispose { viewModel.onScreenPause() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(VintageBgDark, VintageBgBase)
                    )
                )
        )

        Column(
            modifier = Modifier.fillMaxSize().statusBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Rounded.ChevronLeft,
                        contentDescription = "Back",
                        tint = VintageWhite
                    )
                }
                Text(
                    text = "Your cassette",
                    color = VintageWhiteWarm,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                val canSave = config.track.id.isNotEmpty()
                Text(
                    text = "Save",
                    color = if (canSave) VintageWhiteWarm else VintageWhiteWarm.copy(alpha = 0.3f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable(enabled = canSave) { viewModel.saveCassette(onSave) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(VintageBgBase)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CassetteView(
                    isPlaying = playerState is PlayerState.Playing,
                    isFloating = true,
                    cassetteColor = config.cassetteColor,
                    lineColor = config.lineColor,
                    drawRainbow = config.isRainbow,
                )
            }

            if (config.track.title.isNotEmpty()) {
                TrackCard(
                    track = config.track,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SectionLabel("Cassette color")
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                cassetteColors.forEach { color ->
                    ColorDot(
                        color = color,
                        selected = config.cassetteColor == color,
                        onClick = { viewModel.updateCassetteColor(color) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            SectionLabel("Style")
            Row(
                modifier = Modifier.padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StyleToggle(
                    label = "Rainbow",
                    selected = config.isRainbow,
                    onClick = { viewModel.updateStyle(true) },
                    icon = Icons.Rounded.Gradient
                )
                StyleToggle(
                    label = "Line",
                    selected = !config.isRainbow,
                    onClick = { viewModel.updateStyle(false) },
                    icon = Icons.Rounded.LinearScale
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SectionLabel(
                text = "Line color",
                modifier = Modifier.alpha(if (config.isRainbow) 0.3f else 1f)
            )
            Row(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .alpha(if (config.isRainbow) 0.3f else 1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                lineColors.forEach { color ->
                    ColorDot(
                        color = color,
                        selected = config.lineColor == color,
                        onClick = { if (!config.isRainbow) viewModel.updateLineColor(color) }
                    )
                }
            }
        }
    }
}
