package com.davidsimba.vintbeats.feature.home.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.feature.home.domain.HomeSectionPlaylists
import com.davidsimba.vintbeats.shared.components.cards.PlaylistCard
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun PlaylistSection(
    section: HomeSectionPlaylists,
    onPlaylistSelected: (id: String, thumbnailUrl: String?, artistId: String?, artistName: String?) -> Unit,
) {
    Column(modifier = Modifier.padding(bottom = 28.dp)) {
        Text(
            text = section.title,
            color = VintageWhiteWarm,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )
        Spacer(Modifier.height(10.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(section.playlists, key = { it.id }) { playlist ->
                val showStripe = !section.title.lowercase().contains("descubre")
                Column {
                    PlaylistCard(
                        id = playlist.id,
                        name = playlist.artistName ?: playlist.title,
                        thumbnailUrl = playlist.thumbnailUrl,
                        showStripe = showStripe,
                        onClick = {
                            onPlaylistSelected(
                                playlist.id,
                                playlist.thumbnailUrl,
                                playlist.artistId,
                                playlist.artistName
                            )
                        }
                    )
                    if (showStripe) {
                        CardSubtitle(text = "Playlist · ${playlist.artistName ?: playlist.title}")
                    }
                }
            }
        }
    }
}
