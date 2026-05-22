package com.davidsimba.vintbeats.feature.player.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.davidsimba.vintbeats.feature.player.ui.PlayerTab
import com.davidsimba.vintbeats.shared.theme.VintageBlackMid
import com.davidsimba.vintbeats.shared.theme.VintageWhitePure
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun PlayerBottomNav(
    selectedTab: PlayerTab,
    onTabSelected: (PlayerTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(VintageBlackMid)
            .navigationBarsPadding()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerTab.entries.forEach { tab ->
            NavTabItem(
                tab = tab,
                isSelected = selectedTab == tab,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

@Composable
private fun NavTabItem(
    tab: PlayerTab,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val icon: ImageVector = when (tab) {
        PlayerTab.Lyrics -> Icons.Default.MusicNote
        PlayerTab.Player -> Icons.Default.Tune
        PlayerTab.Queue -> Icons.AutoMirrored.Filled.QueueMusic
    }

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tab.label,
            tint = if (isSelected) VintageWhiteWarm else VintageWhitePure.copy(alpha = 0.4f),
            modifier = Modifier.size(22.dp)
        )
    }
}
