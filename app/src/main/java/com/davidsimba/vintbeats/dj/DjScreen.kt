package com.davidsimba.vintbeats.dj

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.core.designsystem.components.background.rememberLightPaletteColor
import com.davidsimba.vintbeats.core.designsystem.components.inputs.TextField
import com.davidsimba.vintbeats.core.designsystem.theme.Fondo
import com.davidsimba.vintbeats.core.designsystem.theme.Superficie
import com.davidsimba.vintbeats.core.designsystem.theme.colorAcento
import com.davidsimba.vintbeats.feature.player.ui.PlaybackViewModel
import com.davidsimba.vintbeats.feature.player.ui.PlayerState

private val White = Color(0xFFF4F4F5)
private val Dark = Color(0xFF0E0E10)
private val Muted = Color(0xFFA1A1AA)
private val Soft = Color(0xFFD4D4D8)

@Composable
fun DjScreen(
    djViewModel: DjViewModel,
    playbackViewModel: PlaybackViewModel,
    onOpenPlayer: () -> Unit = {},
) {
    val state by djViewModel.state.collectAsStateWithLifecycle()
    val userName by djViewModel.userName.collectAsStateWithLifecycle()
    val currentTrack by playbackViewModel.currentTrack.collectAsStateWithLifecycle()
    val playerState by playbackViewModel.playerState.collectAsStateWithLifecycle()
    var showAsk by rememberSaveable { mutableStateOf(false) }

    val loading = state.phase == DjPhase.Loading
    val live = state.phase != DjPhase.Idle
    val startSession = { djViewModel.start(playbackViewModel) }

    // En vivo, el fondo se tiñe con el color claro de la portada actual (degradado suave hacia el fondo oscuro).
    val tint = rememberLightPaletteColor(if (live) currentTrack?.albumImageUrl else null, colorAcento)
    val tintAlpha by animateFloatAsState(if (live) 0.4f else 0f, tween(600), label = "dj_bg_tint")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .background(
                Brush.verticalGradient(
                    0f to tint.copy(alpha = tintAlpha),
                    0.75f to Color.Transparent,
                )
            )
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.dj_title),
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f),
            )
            if (live) LiveBadge()
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))
            DjOrb(
                live = live,
                busy = loading || state.changing,
                haloTint = tint,
                contentDescription = stringResource(
                    if (live) R.string.dj_orb_change_desc else R.string.dj_orb_start_desc
                ),
                onClick = { if (live) djViewModel.changeCourse() else startSession() },
            )
            if (live && !loading && !state.changing) {
                Text(
                    text = stringResource(R.string.dj_change_hint),
                    color = Muted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 34.dp),
                )
            }
            Spacer(Modifier.height(if (live) 20.dp else 40.dp))
            when {
                loading -> Subtitle(stringResource(R.string.dj_preparing))
                state.changing -> Subtitle(stringResource(R.string.dj_changing))
                live -> key(state.caption) { Subtitle("“${state.caption}”") }
                else -> IdleText(userName = userName, error = state.error)
            }
            Spacer(Modifier.height(24.dp))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = if (live) 24.dp else 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!live) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(CircleShape)
                        .background(White)
                        .clickable(role = Role.Button) { startSession() },
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.PlayArrow, null, tint = Dark, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.dj_start_session),
                        color = Dark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                }
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .heightIn(min = 44.dp)
                        .clip(CircleShape)
                        .clickable(role = Role.Button) { showAsk = true }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.ChatBubbleOutline, null, tint = Soft, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.dj_ask_link),
                        color = Soft,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            } else {
                currentTrack?.let { track ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.06f))
                            .clickable(role = Role.Button, onClick = onOpenPlayer)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AsyncImage(
                            model = track.albumImageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Superficie),
                        )
                        Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                            Text(
                                text = track.title,
                                color = White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = track.artist,
                                color = Muted,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                        val playing = playerState is PlayerState.Playing
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(White)
                                .clickable(role = Role.Button) { playbackViewModel.togglePlayPause() },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                contentDescription = stringResource(
                                    if (playing) R.string.dj_pause else R.string.dj_play
                                ),
                                tint = Dark,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PillButton(Icons.Outlined.ChatBubbleOutline, stringResource(R.string.dj_ask_button), Modifier.weight(1f)) {
                        showAsk = true
                    }
                    PillButton(Icons.Rounded.Stop, stringResource(R.string.dj_stop_button), Modifier.weight(1f)) {
                        djViewModel.stop()
                    }
                }
            }
        }
    }

    if (showAsk) {
        AskSheet(
            onDismiss = { showAsk = false },
            onSend = { text ->
                showAsk = false
                djViewModel.start(playbackViewModel, text)
            },
        )
    }
}

@Composable
private fun LiveBadge() {
    Row(
        modifier = Modifier
            .height(28.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(7.dp).clip(CircleShape).background(colorAcento))
        Spacer(Modifier.width(7.dp))
        Text(
            text = stringResource(R.string.dj_live_badge),
            color = White,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp,
        )
    }
}

@Composable
private fun IdleText(userName: String, error: DjError?) {
    Column(
        modifier = Modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = if (userName.isBlank()) stringResource(R.string.dj_greeting_no_name)
            else stringResource(R.string.dj_greeting, userName),
            color = White,
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
        )
        Text(
            text = stringResource(
                when (error) {
                    null -> R.string.dj_intro
                    DjError.RateLimited -> R.string.dj_error_rate_limit
                    else -> R.string.dj_error_retry
                }
            ),
            color = if (error == null) Muted else White,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}

/** Texto grande del DJ: cada vez que cambia, aparece y sube 8dp en 500ms. */
@Composable
private fun Subtitle(text: String) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val offset = with(LocalDensity.current) { 8.dp.roundToPx() }
    AnimatedVisibility(
        visible = shown,
        enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { offset },
    ) {
        Text(
            text = text,
            color = White,
            fontSize = 22.sp,
            lineHeight = 29.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 28.dp),
        )
    }
}

@Composable
private fun PillButton(icon: ImageVector, text: String, modifier: Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .height(44.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.08f))
            .clickable(role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = White, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text = text, color = White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AskSheet(onDismiss: () -> Unit, onSend: (String) -> Unit) {
    var prompt by rememberSaveable { mutableStateOf("") }
    val canSend = prompt.isNotBlank()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Superficie,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp),
        ) {
            TextField(
                value = prompt,
                onValueChange = { prompt = it.take(200) },
                label = stringResource(R.string.dj_prompt_label),
                placeholder = stringResource(R.string.dj_prompt_placeholder),
                borderColor = if (canSend) White else Color(0xFF27272A),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Send,
                ),
                keyboardActions = KeyboardActions(onSend = { if (canSend) onSend(prompt) }),
            )
            Spacer(Modifier.height(16.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(
                    R.string.dj_suggestion_1,
                    R.string.dj_suggestion_2,
                    R.string.dj_suggestion_3,
                    R.string.dj_suggestion_4,
                ).forEach { res ->
                    val text = stringResource(res)
                    Box(
                        modifier = Modifier
                            .heightIn(min = 44.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(22.dp))
                            .clickable(role = Role.Button) { prompt = text }
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = text, color = White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(CircleShape)
                    .background(if (canSend) White else Color(0xFF27272A))
                    .clickable(enabled = canSend, role = Role.Button) { onSend(prompt) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.dj_send),
                    color = if (canSend) Dark else Color(0xFF71717A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                )
            }
        }
    }
}
