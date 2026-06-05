package com.davidsimba.vintbeats.feature.home.ui.components

import kotlin.math.absoluteValue
import com.davidsimba.vintbeats.shared.theme.VintageBlue
import com.davidsimba.vintbeats.shared.theme.VintageBlueDeep
import com.davidsimba.vintbeats.shared.theme.VintageGreen
import com.davidsimba.vintbeats.shared.theme.VintageGreenDeep
import com.davidsimba.vintbeats.shared.theme.VintageOrange
import com.davidsimba.vintbeats.shared.theme.VintageOrangeDeep
import com.davidsimba.vintbeats.shared.theme.VintageRed
import com.davidsimba.vintbeats.shared.theme.VintageRedDeep

internal val accentColors = listOf(VintageRed, VintageOrange, VintageGreen, VintageBlue)
internal val accentDeepColors = listOf(VintageRedDeep, VintageOrangeDeep, VintageGreenDeep, VintageBlueDeep)

internal fun accentColorFor(id: String) = accentColors[id.hashCode().absoluteValue % accentColors.size]
internal fun accentDeepColorFor(id: String) = accentDeepColors[id.hashCode().absoluteValue % accentDeepColors.size]
