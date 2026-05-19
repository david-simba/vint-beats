package com.davidsimba.vintbeats.feature.cassette.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.davidsimba.vintbeats.feature.cassette.domain.CassetteConfig
import com.davidsimba.vintbeats.feature.search.domain.Track
import com.davidsimba.vintbeats.shared.theme.VintageBlackMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class CassetteSharedViewModel @Inject constructor(): ViewModel() {
    private val _cassetteConfig = MutableStateFlow(
        CassetteConfig(
            track = Track("", "", "", null, null),
            cassetteColor = VintageBlackMid,
            lineColor = VintageRedLight,
            isRainbow = true
        )
    )
    val cassetteConfig: StateFlow<CassetteConfig> = _cassetteConfig

    fun selectTrack(track: Track) {
        _cassetteConfig.value = _cassetteConfig.value.copy(track = track)
    }

    fun updateCassetteColor(color: Color) {
        _cassetteConfig.value = _cassetteConfig.value.copy(cassetteColor = color)
    }

    fun updateLineColor(color: Color) {
        _cassetteConfig.value = _cassetteConfig.value.copy(lineColor = color)
    }

    fun updateStyle(isRainbow: Boolean) {
        _cassetteConfig.value = _cassetteConfig.value.copy(isRainbow = isRainbow)
    }
}
