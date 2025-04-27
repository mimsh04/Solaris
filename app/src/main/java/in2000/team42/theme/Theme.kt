package in2000.team42.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Define custom colors for the solar-themed app
val SolarYellowLight = Color(0xFFFFC107) // Vibrant yellow for sun/energy
val SolarYellowDark = Color(0xFFFFA000) // Darker mustard for dark theme
val SkyBlueLight = Color(0xFF4FC3F7) // Bright sky blue for clean energy
val SkyBlueDark = Color(0xFF0288D1) // Deeper blue for dark theme
val LeafGreenLight = Color(0xFF4CAF50) // Green for sustainability
val LeafGreenDark = Color(0xFF2E7D32) // Muted green for dark theme
val BackgroundLight = Color(0xFFF5F7FA) // Clean off-white for light background
val BackgroundDark = Color(0xFF121212) // Dark gray for dark background
val SurfaceLight = Color(0xFFFFFFFF) // Pure white for surfaces
val SurfaceDark = Color(0xFF1E1E1E) // Slightly lighter dark gray for surfaces
val ErrorColor = Color(0xFFD32F2F) // Soft red for errors

private val DarkColorScheme = darkColorScheme(
    primary = SolarYellowDark,
    onPrimary = Color.Black, // Black text/icons on yellow for contrast
    secondary = SkyBlueDark,
    onSecondary = Color.White, // White text/icons on blue
    tertiary = LeafGreenDark,
    onTertiary = Color.White, // White text/icons on green
    background = BackgroundDark,
    onBackground = Color.White, // White text on dark background
    surface = SurfaceDark,
    onSurface = Color.White, // White text on dark surfaces
    error = ErrorColor,
    onError = Color.White // White text on error red
)

private val LightColorScheme = lightColorScheme(
    primary = SolarYellowLight,
    onPrimary = Color.Black, // Black text/icons on yellow
    secondary = SkyBlueLight,
    onSecondary = Color.Black, // Black text/icons on blue
    tertiary = LeafGreenLight,
    onTertiary = Color.White, // White text/icons on green
    background = BackgroundLight,
    onBackground = Color.Black, // Black text on light background
    surface = SurfaceLight,
    onSurface = Color.Black, // Black text on white surfaces
    error = ErrorColor,
    onError = Color.White // White text on error red
)

@Composable
fun IN2000_team42Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}