package com.davidsimba.vintbeats.feature.home.ui.components

import kotlin.math.absoluteValue
import com.davidsimba.vintbeats.core.designsystem.theme.VintageBlue
import com.davidsimba.vintbeats.core.designsystem.theme.VintageBlueDeep
import com.davidsimba.vintbeats.core.designsystem.theme.VintageGreen
import com.davidsimba.vintbeats.core.designsystem.theme.VintageGreenDeep
import com.davidsimba.vintbeats.core.designsystem.theme.VintageOrange
import com.davidsimba.vintbeats.core.designsystem.theme.VintageOrangeDeep
import com.davidsimba.vintbeats.core.designsystem.theme.VintageRed
import com.davidsimba.vintbeats.core.designsystem.theme.VintageRedDeep

internal val accentColors = listOf(VintageRed, VintageOrange, VintageGreen, VintageBlue)
internal val accentDeepColors = listOf(VintageRedDeep, VintageOrangeDeep, VintageGreenDeep, VintageBlueDeep)

internal fun accentColorFor(id: String) = accentColors[id.hashCode().absoluteValue % accentColors.size]
internal fun accentDeepColorFor(id: String) = accentDeepColors[id.hashCode().absoluteValue % accentDeepColors.size]
