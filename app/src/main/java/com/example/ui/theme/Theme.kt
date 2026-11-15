package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.data.model.QuizThemeStyle

fun colorSchemeForTheme(style: QuizThemeStyle): ColorScheme = when (style) {
    QuizThemeStyle.DARK_GAMING -> darkColorScheme(
        primary = DarkGamingPrimary,
        onPrimary = Color.White,
        primaryContainer = Color(0xFF31226E),
        onPrimaryContainer = Color(0xFFE9E2FF),
        secondary = DarkGamingSecondary,
        onSecondary = Color(0xFF002026),
        secondaryContainer = Color(0xFF00404D),
        onSecondaryContainer = Color(0xFFB3F7FF),
        tertiary = GoldAccent,
        onTertiary = Color(0xFF281C00),
        background = DarkGamingBg,
        onBackground = Color(0xFFF5F4FF),
        surface = DarkGamingSurface,
        onSurface = Color(0xFFF5F4FF),
        surfaceVariant = DarkGamingSurfaceVar,
        onSurfaceVariant = Color(0xFFC4BFE2),
        error = CrimsonAlert,
        onError = Color.White
    )
    QuizThemeStyle.NEON_PURPLE -> darkColorScheme(
        primary = NeonPurplePrimary,
        onPrimary = Color.White,
        primaryContainer = Color(0xFF591469),
        onPrimaryContainer = Color(0xFFFFD6FA),
        secondary = NeonPurpleSecondary,
        onSecondary = Color(0xFF001E2E),
        secondaryContainer = Color(0xFF143E5C),
        onSecondaryContainer = Color(0xFFCDEEFF),
        tertiary = GoldAccent,
        onTertiary = Color(0xFF281C00),
        background = NeonPurpleBg,
        onBackground = Color(0xFFFAF2FF),
        surface = NeonPurpleSurface,
        onSurface = Color(0xFFFAF2FF),
        surfaceVariant = NeonPurpleSurfaceVar,
        onSurfaceVariant = Color(0xFFD9C0EC),
        error = CrimsonAlert,
        onError = Color.White
    )
    QuizThemeStyle.CYBER_BLUE -> darkColorScheme(
        primary = CyberBluePrimary,
        onPrimary = Color(0xFF00202B),
        primaryContainer = Color(0xFF004C66),
        onPrimaryContainer = Color(0xFFC7F3FF),
        secondary = CyberBlueSecondary,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFF1B3A66),
        onSecondaryContainer = Color(0xFFD6E6FF),
        tertiary = EmeraldSuccess,
        onTertiary = Color(0xFF002917),
        background = CyberBlueBg,
        onBackground = Color(0xFFEAF7FF),
        surface = CyberBlueSurface,
        onSurface = Color(0xFFEAF7FF),
        surfaceVariant = CyberBlueSurfaceVar,
        onSurfaceVariant = Color(0xFFAAC7E2),
        error = CrimsonAlert,
        onError = Color.White
    )
    QuizThemeStyle.GALAXY -> darkColorScheme(
        primary = GalaxyPrimary,
        onPrimary = Color(0xFF1E1348),
        primaryContainer = Color(0xFF392B75),
        onPrimaryContainer = Color(0xFFECE6FF),
        secondary = GalaxySecondary,
        onSecondary = Color(0xFF3D0722),
        secondaryContainer = Color(0xFF63193F),
        onSecondaryContainer = Color(0xFFFFD8EA),
        tertiary = GoldAccent,
        onTertiary = Color(0xFF281C00),
        background = GalaxyBg,
        onBackground = Color(0xFFF3F4FF),
        surface = GalaxySurface,
        onSurface = Color(0xFFF3F4FF),
        surfaceVariant = GalaxySurfaceVar,
        onSurfaceVariant = Color(0xFFC2C6EC),
        error = CrimsonAlert,
        onError = Color.White
    )
    QuizThemeStyle.GOLDEN_PREMIUM -> darkColorScheme(
        primary = GoldenPrimary,
        onPrimary = Color(0xFF2A1D00),
        primaryContainer = Color(0xFF523C08),
        onPrimaryContainer = Color(0xFFFFE7A8),
        secondary = GoldenSecondary,
        onSecondary = Color(0xFF2B1600),
        secondaryContainer = Color(0xFF543008),
        onSecondaryContainer = Color(0xFFFFDFBA),
        tertiary = CyanElectric,
        onTertiary = Color(0xFF002026),
        background = GoldenBg,
        onBackground = Color(0xFFFFF8EC),
        surface = GoldenSurface,
        onSurface = Color(0xFFFFF8EC),
        surfaceVariant = GoldenSurfaceVar,
        onSurfaceVariant = Color(0xFFD8CAAE),
        error = CrimsonAlert,
        onError = Color.White
    )
    QuizThemeStyle.MINIMAL_WHITE -> lightColorScheme(
        primary = MinimalPrimary,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFE0E2FF),
        onPrimaryContainer = Color(0xFF151257),
        secondary = MinimalSecondary,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFD5F1FF),
        onSecondaryContainer = Color(0xFF002D44),
        tertiary = GoldAccent,
        onTertiary = Color(0xFF281C00),
        background = MinimalBg,
        onBackground = Color(0xFF141624),
        surface = MinimalSurface,
        onSurface = Color(0xFF141624),
        surfaceVariant = MinimalSurfaceVar,
        onSurfaceVariant = Color(0xFF4F5570),
        error = Color(0xFFD32F2F),
        onError = Color.White
    )
}

@Composable
fun QuizFriendTheme(
    themeStyle: QuizThemeStyle = QuizThemeStyle.DARK_GAMING,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorSchemeForTheme(themeStyle),
        typography = QuizFriendTypography,
        content = content
    )
}
