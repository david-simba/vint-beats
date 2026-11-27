package com.davidsimba.vintbeats.dj

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.core.designsystem.components.inputs.TextField
import com.davidsimba.vintbeats.core.designsystem.theme.Fondo
import com.davidsimba.vintbeats.core.designsystem.theme.Superficie
import com.davidsimba.vintbeats.feature.player.ui.PlaybackViewModel

private val White = Color(0xFFF4F4F5)
private val Dark = Color(0xFF0E0E10)
private val Muted = Color(0xFFA1A1AA)

@Composable
fun DjScreen(
    djViewModel: DjViewModel,
    playbackViewModel: PlaybackViewModel,
) {
    val state by djViewModel.state.collectAsStateWithLifecycle()
    val currentTrack by playbackViewModel.currentTrack.collectAsStateWithLifecycle()
    var prompt by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Fondo)
            .statusBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 120.dp),
    ) {
        Text(
            text = stringResource(R.string.dj_title),
            color = White,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 16.dp),
        )
        Text(
            text = stringResource(R.string.dj_subtitle),
            color = Muted,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp),
        )

        when (state.phase) {
            DjPhase.Idle -> IdleContent(
                prompt = prompt,
                onPromptChange = { prompt = it },
                error = state.error,
                onStart = { djViewModel.start(prompt, playbackViewModel) },
            )
            DjPhase.Loading -> LoadingContent()
            DjPhase.Live -> LiveContent(
                caption = state.caption,
                speaking = state.speaking,
                trackTitle = currentTrack?.title,
                trackArtist = currentTrack?.artist,
                onStop = { djViewModel.stop() },
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun IdleContent(
    prompt: String,
    onPromptChange: (String) -> Unit,
    error: DjError?,
    onStart: () -> Unit,
) {
    val canStart = prompt.isNotBlank()
    TextField(
        value = prompt,
        onValueChange = { onPromptChange(it.take(200)) },
        label = stringResource(R.string.dj_prompt_label),
        placeholder = stringResource(R.string.dj_prompt_placeholder),
        borderColor = if (canStart) White else Color(0xFF27272A),
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Go),
        keyboardActions = KeyboardActions(onGo = { if (canStart) onStart() }),
    )

    error?.let {
        Text(
            text = stringResource(
                when (it) {
                    DjError.RateLimited -> R.string.dj_error_rate_limit
                    DjError.NotFound -> R.string.dj_error_not_found
                    DjError.Generic -> R.string.dj_error_generic
                }
            ),
            color = Color(0xFFE5484D),
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 10.dp),
        )
    }

    Spacer(Modifier.height(16.dp))
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
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
                    .border(1.dp, Color(0xFF27272A), RoundedCornerShape(22.dp))
                    .clickable(role = Role.Button) { onPromptChange(text) }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = text, color = White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    Spacer(Modifier.height(24.dp))
    ActionButton(text = stringResource(R.string.dj_start), enabled = canStart, onClick = onStart)
}

@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(color = White, strokeWidth = 3.dp, modifier = Modifier.size(36.dp))
        Text(
            text = stringResource(R.string.dj_loading),
            color = Muted,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Composable
private fun LiveContent(
    caption: String,
    speaking: Boolean,
    trackTitle: String?,
    trackArtist: String?,
    onStop: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Superficie)
            .padding(20.dp),
    ) {
        Text(
            text = stringResource(if (speaking) R.string.dj_speaking else R.string.dj_on_air),
            color = Muted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = caption,
            color = White,
            fontSize = 20.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(top = 10.dp),
        )
    }
    if (trackTitle != null) {
        Text(
            text = stringResource(R.string.dj_now_playing),
            color = Muted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 24.dp),
        )
        Text(
            text = trackTitle,
            color = White,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp),
        )
        Text(
            text = trackArtist.orEmpty(),
            color = Muted,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
    Spacer(Modifier.height(32.dp))
    ActionButton(text = stringResource(R.string.dj_stop), enabled = true, onClick = onStop)
}

@Composable
private fun ActionButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(if (enabled) White else Color(0xFF27272A))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = if (enabled) Dark else Color(0xFF71717A),
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
        )
    }
}
