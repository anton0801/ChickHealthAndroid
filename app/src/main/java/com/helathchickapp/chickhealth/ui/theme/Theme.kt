package com.helathchickapp.chickhealth.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun ChickHealthTheme(
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


val Pets: ImageVector
    get() {
        if (_Pets != null) return _Pets!!

        _Pets = ImageVector.Builder(
            name = "Pets",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(180f, 485f)
                quadToRelative(-42f, 0f, -71f, -29f)
                reflectiveQuadToRelative(-29f, -71f)
                reflectiveQuadToRelative(29f, -71f)
                reflectiveQuadToRelative(71f, -29f)
                reflectiveQuadToRelative(71f, 29f)
                reflectiveQuadToRelative(29f, 71f)
                reflectiveQuadToRelative(-29f, 71f)
                reflectiveQuadToRelative(-71f, 29f)
                moveToRelative(180f, -160f)
                quadToRelative(-42f, 0f, -71f, -29f)
                reflectiveQuadToRelative(-29f, -71f)
                reflectiveQuadToRelative(29f, -71f)
                reflectiveQuadToRelative(71f, -29f)
                reflectiveQuadToRelative(71f, 29f)
                reflectiveQuadToRelative(29f, 71f)
                reflectiveQuadToRelative(-29f, 71f)
                reflectiveQuadToRelative(-71f, 29f)
                moveToRelative(240f, 0f)
                quadToRelative(-42f, 0f, -71f, -29f)
                reflectiveQuadToRelative(-29f, -71f)
                reflectiveQuadToRelative(29f, -71f)
                reflectiveQuadToRelative(71f, -29f)
                reflectiveQuadToRelative(71f, 29f)
                reflectiveQuadToRelative(29f, 71f)
                reflectiveQuadToRelative(-29f, 71f)
                reflectiveQuadToRelative(-71f, 29f)
                moveToRelative(180f, 160f)
                quadToRelative(-42f, 0f, -71f, -29f)
                reflectiveQuadToRelative(-29f, -71f)
                reflectiveQuadToRelative(29f, -71f)
                reflectiveQuadToRelative(71f, -29f)
                reflectiveQuadToRelative(71f, 29f)
                reflectiveQuadToRelative(29f, 71f)
                reflectiveQuadToRelative(-29f, 71f)
                reflectiveQuadToRelative(-71f, 29f)
                moveTo(266f, 885f)
                quadToRelative(-45f, 0f, -75.5f, -34.5f)
                reflectiveQuadTo(160f, 769f)
                quadToRelative(0f, -52f, 35.5f, -91f)
                reflectiveQuadToRelative(70.5f, -77f)
                quadToRelative(29f, -31f, 50f, -67.5f)
                reflectiveQuadToRelative(50f, -68.5f)
                quadToRelative(22f, -26f, 51f, -43f)
                reflectiveQuadToRelative(63f, -17f)
                reflectiveQuadToRelative(63f, 16f)
                reflectiveQuadToRelative(51f, 42f)
                quadToRelative(28f, 32f, 49.5f, 69f)
                reflectiveQuadToRelative(50.5f, 69f)
                quadToRelative(35f, 38f, 70.5f, 77f)
                reflectiveQuadToRelative(35.5f, 91f)
                quadToRelative(0f, 47f, -30.5f, 81.5f)
                reflectiveQuadTo(694f, 885f)
                quadToRelative(-54f, 0f, -107f, -9f)
                reflectiveQuadToRelative(-107f, -9f)
                reflectiveQuadToRelative(-107f, 9f)
                reflectiveQuadToRelative(-107f, 9f)
            }
        }.build()

        return _Pets!!
    }

private var _Pets: ImageVector? = null

val JournalMedical: ImageVector
    get() {
        if (_JournalMedical != null) return _JournalMedical!!

        _JournalMedical = ImageVector.Builder(
            name = "JournalMedical",
            defaultWidth = 16.dp,
            defaultHeight = 16.dp,
            viewportWidth = 16f,
            viewportHeight = 16f
        ).apply {
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(8f, 4f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, 0.5f)
                verticalLineToRelative(0.634f)
                lineToRelative(0.549f, -0.317f)
                arcToRelative(0.5f, 0.5f, 0f, true, true, 0.5f, 0.866f)
                lineTo(9f, 6f)
                lineToRelative(0.549f, 0.317f)
                arcToRelative(0.5f, 0.5f, 0f, true, true, -0.5f, 0.866f)
                lineTo(8.5f, 6.866f)
                verticalLineTo(7.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, -1f, 0f)
                verticalLineToRelative(-0.634f)
                lineToRelative(-0.549f, 0.317f)
                arcToRelative(0.5f, 0.5f, 0f, true, true, -0.5f, -0.866f)
                lineTo(7f, 6f)
                lineToRelative(-0.549f, -0.317f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, -0.866f)
                lineToRelative(0.549f, 0.317f)
                verticalLineTo(4.5f)
                arcTo(0.5f, 0.5f, 0f, false, true, 8f, 4f)
                moveTo(5f, 9.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, -0.5f)
                horizontalLineToRelative(5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, 1f)
                horizontalLineToRelative(-5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, -0.5f, -0.5f)
                moveToRelative(0f, 2f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0.5f, -0.5f)
                horizontalLineToRelative(5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, 1f)
                horizontalLineToRelative(-5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, -0.5f, -0.5f)
            }
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(3f, 0f)
                horizontalLineToRelative(10f)
                arcToRelative(2f, 2f, 0f, false, true, 2f, 2f)
                verticalLineToRelative(12f)
                arcToRelative(2f, 2f, 0f, false, true, -2f, 2f)
                horizontalLineTo(3f)
                arcToRelative(2f, 2f, 0f, false, true, -2f, -2f)
                verticalLineToRelative(-1f)
                horizontalLineToRelative(1f)
                verticalLineToRelative(1f)
                arcToRelative(1f, 1f, 0f, false, false, 1f, 1f)
                horizontalLineToRelative(10f)
                arcToRelative(1f, 1f, 0f, false, false, 1f, -1f)
                verticalLineTo(2f)
                arcToRelative(1f, 1f, 0f, false, false, -1f, -1f)
                horizontalLineTo(3f)
                arcToRelative(1f, 1f, 0f, false, false, -1f, 1f)
                verticalLineToRelative(1f)
                horizontalLineTo(1f)
                verticalLineTo(2f)
                arcToRelative(2f, 2f, 0f, false, true, 2f, -2f)
            }
            path(
                fill = SolidColor(Color.Black)
            ) {
                moveTo(1f, 5f)
                verticalLineToRelative(-0.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 1f, 0f)
                verticalLineTo(5f)
                horizontalLineToRelative(0.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, 1f)
                horizontalLineToRelative(-2f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, -1f)
                close()
                moveToRelative(0f, 3f)
                verticalLineToRelative(-0.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 1f, 0f)
                verticalLineTo(8f)
                horizontalLineToRelative(0.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, 1f)
                horizontalLineToRelative(-2f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, -1f)
                close()
                moveToRelative(0f, 3f)
                verticalLineToRelative(-0.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 1f, 0f)
                verticalLineToRelative(0.5f)
                horizontalLineToRelative(0.5f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, 1f)
                horizontalLineToRelative(-2f)
                arcToRelative(0.5f, 0.5f, 0f, false, true, 0f, -1f)
                close()
            }
        }.build()

        return _JournalMedical!!
    }

private var _JournalMedical: ImageVector? = null

val Analytics: ImageVector
    get() {
        if (_Analytics != null) return _Analytics!!

        _Analytics = ImageVector.Builder(
            name = "Analytics",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f
        ).apply {
            path(
                fill = SolidColor(Color(0xFF000000))
            ) {
                moveTo(120f, 840f)
                verticalLineToRelative(-720f)
                horizontalLineToRelative(720f)
                verticalLineToRelative(720f)
                close()
                moveToRelative(80f, -80f)
                horizontalLineToRelative(560f)
                verticalLineToRelative(-560f)
                horizontalLineTo(200f)
                close()
                moveToRelative(80f, -80f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(-200f)
                horizontalLineToRelative(-80f)
                close()
                moveToRelative(320f, 0f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(-400f)
                horizontalLineToRelative(-80f)
                close()
                moveToRelative(-160f, 0f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(-120f)
                horizontalLineToRelative(-80f)
                close()
                moveToRelative(0f, -200f)
                horizontalLineToRelative(80f)
                verticalLineToRelative(-80f)
                horizontalLineToRelative(-80f)
                close()
                moveTo(200f, 760f)
                verticalLineToRelative(-560f)
                close()
            }
        }.build()

        return _Analytics!!
    }

private var _Analytics: ImageVector? = null

