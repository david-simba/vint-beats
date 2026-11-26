package com.davidsimba.vintbeats.core.designsystem.components

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.davidsimba.vintbeats.core.designsystem.theme.VintageGrayDeep
import com.davidsimba.vintbeats.core.designsystem.theme.VintageGrayMid
import com.davidsimba.vintbeats.core.designsystem.theme.VintageRedLight
import com.davidsimba.vintbeats.core.designsystem.theme.VintageWhite

@Composable
fun VintSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = VintageWhite,
            checkedTrackColor = VintageRedLight,
            uncheckedThumbColor = VintageGrayMid,
            uncheckedTrackColor = VintageGrayDeep,
            uncheckedBorderColor = VintageGrayDeep,
        ),
    )
}
