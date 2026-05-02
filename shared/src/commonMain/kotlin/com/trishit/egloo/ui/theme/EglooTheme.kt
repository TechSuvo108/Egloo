package com.egloo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.trishit.egloo.ui.theme.EglooColors
import kotlin.invoke

// ─────────────────────────────────────────────
//  Egloo color palette — mirrors the brand spec
// ─────────────────────────────────────────────

object EglooColors {
    // Primary
    val IgNight       = Color(0xFF0E1A2E)
    val IgNightLight  = Color(0xFF1A2D47)
    val PingoTeal     = Color(0xFF1D9E75)
    val PingoTealDark = Color(0xFF0F6E56)
    val ArcticBlue    = Color(0xFF378ADD)
    val ArcticBlueDark= Color(0xFF185FA5)
    val SnowWhite     = Color(0xFFDAF0FA)
    val SnowPure      = Color(0xFFF0F7FC)
    val BeakAmber     = Color(0xFFF5A623)
    val BeakAmberDark = Color(0xFFBA7517)

    // Neutral
    val Surface       = Color(0xFFFFFFFF)
    val SurfaceVariant= Color(0xFFF5F8FA)
    val OnSurface     = Color(0xFF0E1A2E)
    val OnSurfaceMuted= Color(0xFF5F7080)

    // Semantic
    val ErrorRed      = Color(0xFFE24B4A)
    val SuccessGreen  = Color(0xFF1D9E75)
    val WarnAmber     = Color(0xFFF5A623)
}

private val DarkColorScheme = darkColorScheme(
    primary          = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.PingoTeal,
    onPrimary        = Color.White,
    primaryContainer = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.PingoTealDark,
    secondary        = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.ArcticBlue,
    onSecondary      = Color.White,
    background       = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.IgNight,
    surface          = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.IgNightLight,
    onBackground     = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.SnowWhite,
    onSurface        = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.SnowWhite,
    error            = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.ErrorRed,
    tertiary         = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.BeakAmber,
)

private val LightColorScheme = lightColorScheme(
    primary          = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.PingoTeal,
    onPrimary        = Color.White,
    primaryContainer = Color(0xFFB7EDD9),
    secondary        = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.ArcticBlueDark,
    onSecondary      = Color.White,
    background       = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.SnowPure,
    surface          = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.Surface,
    onBackground     = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.IgNight,
    onSurface        = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.IgNight,
    error            = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.ErrorRed,
    tertiary         = _root_ide_package_.com.trishit.egloo.ui.theme.EglooColors.BeakAmberDark,
)

// ─────────────────────────────────────────────
//  Typography
// ─────────────────────────────────────────────

val EglooTypography = Typography(
    displaySmall  = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.W500, letterSpacing = (-0.5).sp),
    headlineLarge = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.W500),
    headlineMedium= TextStyle(fontSize = 18.sp, fontWeight = FontWeight.W500),
    titleLarge    = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.W500),
    titleMedium   = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.W500),
    bodyLarge     = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal, lineHeight = 22.sp),
    bodyMedium    = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 20.sp),
    bodySmall     = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
    labelSmall    = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.W500, letterSpacing = 0.06.sp),
)

// ─────────────────────────────────────────────
//  Theme composable
// ─────────────────────────────────────────────

@Composable
fun EglooTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) _root_ide_package_.com.egloo.ui.theme.DarkColorScheme else _root_ide_package_.com.egloo.ui.theme.LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography  = _root_ide_package_.com.egloo.ui.theme.EglooTypography,
        content     = content
    )
}
