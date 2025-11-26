package amov.a2020157100.ecomap.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext



private val AppColorScheme = lightColorScheme(
    primary = BrandGreen,
    onPrimary = BrandWhite,

    secondary = BrandLightGreen,
    onSecondary = BrandBlack,

    background = BackgroundGray,
    onBackground = TextDarkGray,

    surface = BrandWhite,
    onSurface = TextDarkGray,

    error = StatusError,
    onError = BrandWhite

)


@Composable
fun EcoMapTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),

    content: @Composable () -> Unit
) {
    /*
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }*/

    val colorScheme = AppColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}