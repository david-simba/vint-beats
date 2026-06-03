package com.davidsimba.vintbeats.shared.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.shared.theme.VintageBgBase
import com.davidsimba.vintbeats.shared.theme.VintageGrayMid
import com.davidsimba.vintbeats.shared.theme.VintageRedLight
import com.davidsimba.vintbeats.shared.theme.VintageWhite
import com.davidsimba.vintbeats.shared.theme.VintageWhiteWarm

@Composable
fun VintAlertDialog(
    title: String,
    message: String? = null,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmColor: Color = VintageRedLight,
) {
    AlertDialog(
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
