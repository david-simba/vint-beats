package com.davidsimba.vintbeats.shared.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.shared.theme.VintageBgDark
import com.davidsimba.vintbeats.shared.theme.VintageBlue
import com.davidsimba.vintbeats.shared.theme.VintageBlueDeep
import com.davidsimba.vintbeats.shared.theme.VintageGreen
import com.davidsimba.vintbeats.shared.theme.VintageGreenDeep
import com.davidsimba.vintbeats.shared.theme.VintageOrange
import com.davidsimba.vintbeats.shared.theme.VintageOrangeDeep
import com.davidsimba.vintbeats.shared.theme.VintageRed
import com.davidsimba.vintbeats.shared.theme.VintageRedDeep
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import kotlin.math.absoluteValue

private val accentColors = listOf(VintageRed, VintageOrange, VintageGreen, VintageBlue)
private val accentDeepColors = listOf(VintageRedDeep, VintageOrangeDeep, VintageGreenDeep, VintageBlueDeep)
internal fun playlistAccentFor(id: String) = accentColors[id.hashCode().absoluteValue % accentColors.size]
internal fun playlistAccentDeepFor(id: String) = accentDeepColors[id.hashCode().absoluteValue % accentDeepColors.size]

@Composable
fun PlaylistCard(
    modifier: Modifier = Modifier,
    id: String,
    name: String,
    thumbnailUrl: String?,
    onClick: () -> Unit,
    showStripe: Boolean = true,
) {
    if (!showStripe) {
        AsyncImage(
            model = thumbnailUrl,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .width(180.dp)
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
        )
        return
    }

    val accent = playlistAccentFor(id)
    val accentDeep = playlistAccentDeepFor(id)

    Box(
        modifier = modifier
            .width(180.dp)
            .height(180.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.matchParentSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Brush.verticalGradient(listOf(accent, accentDeep)))
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(VintageWhite)
            )
        }

        Text(
            text = stringResource(R.string.mix_this_is),
            color = VintageWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
        )

        Image(
            painter = painterResource(R.mipmap.ic_launcher_foreground),
            contentDescription = null,
            colorFilter = ColorFilter.tint(VintageWhite, BlendMode.SrcIn),
            modifier = Modifier
                .size(32.dp)
                .align(Alignment.TopStart)
                .padding(top = 6.dp, start = 6.dp)
        )

        AsyncImage(
            model = thumbnailUrl,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .height(100.dp)
                .width(140.dp)
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(2.dp))
                .align(Alignment.Center)
        )

        Text(
            text = name,
            color = VintageBgDark,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp, start = 12.dp, end = 12.dp)
        )
    }
}
