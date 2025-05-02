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