package com.davidsimba.vintbeats.core.designsystem.components

import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.davidsimba.vintbeats.core.designsystem.theme.VintageGrayMid
import com.davidsimba.vintbeats.core.designsystem.theme.VintageRedLight
import com.davidsimba.vintbeats.core.designsystem.theme.VintageWhite

@Composable
fun VintCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Checkbox(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = CheckboxDefaults.colors(
            checkedColor = VintageRedLight,
            uncheckedColor = VintageGrayMid,
            checkmarkColor = VintageWhite
        )
    )
}
