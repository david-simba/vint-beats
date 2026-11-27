package com.davidsimba.vintbeats.dj

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.davidsimba.vintbeats.core.designsystem.theme.Fondo

/** Pantalla del DJ: por ahora solo el fondo, hasta definir su contenido. */
@Composable
fun DjScreen() {
    Box(modifier = Modifier.fillMaxSize().background(Fondo))
}
