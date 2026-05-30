package com.davidsimba.vintbeats.shared.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.davidsimba.vintbeats.R

val SoraFontFamily = FontFamily(
    Font(R.font.manrope_extralight, FontWeight.ExtraLight),
    Font(R.font.manrope_light, FontWeight.Light),
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
    Font(R.font.manrope_extrabold, FontWeight.ExtraBold),
)

val VintBeatsTypography = Typography(
    displayLarge = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Bold, fontSize = 57.sp),
    displayMedium = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Bold, fontSize = 45.sp),
    displaySmall = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 36.sp),
    headlineLarge = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 28.sp),
    headlineSmall = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 24.sp),
    titleLarge = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 22.sp),
    titleMedium = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    titleSmall = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    bodyLarge = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Normal, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = SoraFontFamily, fontWeight = FontWeight.Medium, fontSize = 11.sp),
)
