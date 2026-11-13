package com.example.gestiondearriendos.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Títulos con tipografía serif (más elegante) y textos con sans-serif (más legible).
private val Titulos = FontFamily.Serif
private val Cuerpo = FontFamily.SansSerif

val Typography = Typography(
    displaySmall = TextStyle(
        fontFamily = Titulos, fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp, lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Titulos, fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp, lineHeight = 34.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Titulos, fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp, lineHeight = 30.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Titulos, fontWeight = FontWeight.SemiBold,
        fontSize = 21.sp, lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Cuerpo, fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp, lineHeight = 22.sp, letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Cuerpo, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Cuerpo, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.3.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Cuerpo, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Cuerpo, fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.4.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Cuerpo, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Cuerpo, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 1.sp
    )
)
