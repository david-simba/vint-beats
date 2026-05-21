package com.davidsimba.vintbeats.feature.cassette.ui

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.davidsimba.vintbeats.shared.theme.VintageBlackMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class CassetteConfigViewModel @Inject constructor() : ViewModel() {

    private val _cassetteColor = MutableStateFlow(VintageBlackMid)
    val cassetteColor: StateFlow<Color> = _cassetteColor.asStateFlow()

    private val _lineColor = MutableStateFlow(VintageRedLight)
    val lineColor: StateFlow<Color> = _lineColor.asStateFlow()

    private val _isRainbow = MutableStateFlow(true)
    val isRainbow: StateFlow<Boolean> = _isRainbow.asStateFlow()

    fun updateCassetteColor(color: Color) { _cassetteColor.value = color }
    fun updateLineColor(color: Color) { _lineColor.value = color }
    fun updateStyle(isRainbow: Boolean) { _isRainbow.value = isRainbow }
}
