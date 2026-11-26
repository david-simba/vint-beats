package com.davidsimba.vintbeats.core.designsystem.components.feedback

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog as MaterialAlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.core.designsystem.theme.VintageBgBase
import com.davidsimba.vintbeats.core.designsystem.theme.VintageGrayMid
import com.davidsimba.vintbeats.core.designsystem.theme.VintageRedLight
import com.davidsimba.vintbeats.core.designsystem.theme.VintageWhite
import com.davidsimba.vintbeats.core.designsystem.theme.VintageWhiteWarm

@Composable
fun AlertDialog(
    title: String,
    message: String? = null,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmColor: Color = VintageRedLight,
) {
    MaterialAlertDialog(
        onDismissRequest = onDismiss,
        containerColor = VintageBgBase,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = title,
                color = VintageWhiteWarm,
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
            )
        },
        text = message?.let {
            {
                Text(text = it, color = VintageGrayMid, fontSize = 14.sp)
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = confirmLabel, color = confirmColor, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = dismissLabel, color = VintageWhite.copy(alpha = 0.5f))
            }
        },
    )
}
