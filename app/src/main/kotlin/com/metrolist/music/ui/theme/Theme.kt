/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.theme

import android.graphics.Bitmap
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.palette.graphics.Palette
import com.materialkolor.PaletteStyle
import com.materialkolor.dynamiccolor.ColorSpec
import com.materialkolor.rememberDynamicColorScheme
import com.materialkolor.score.Score

// Sentinel value stored in preferences for the "default / dynamic" palette.
// Kept unchanged so existing user preferences continue to work.
val DefaultThemeColor = Color(0xFFED5564)

// "Aurora" brand color used whenever the default palette is selected
// and no album-art color is available.
val AuroraSeedColor = Color(0xFF6C5CE7)

private val AuroraShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp),
    extraLarge = RoundedCornerShape(34.dp),
)

private val AuroraTypography: Typography = Typography().let { base ->
    base.copy(
        displayLarge = base.displayLarge.copy(fontWeight = FontWeight.Black, letterSpacing = (-0.5).sp),
        displayMedium = base.displayMedium.copy(fontWeight = FontWeight.Black, letterSpacing = (-0.25).sp),
        displaySmall = base.displaySmall.copy(fontWeight = FontWeight.ExtraBold),
        headlineLarge = base.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.25).sp),
        headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.Bold),
        headlineSmall = base.headlineSmall.copy(fontWeight = FontWeight.Bold),
        titleLarge = base.titleLarge.copy(fontWeight = FontWeight.Bold),
        titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        titleSmall = base.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = base.labelLarge.copy(fontWeight = FontWeight.SemiBold),
        labelMedium = base.labelMedium.copy(fontWeight = FontWeight.SemiBold),
    )
}

@Composable
fun MetrolistTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    pureBlack: Boolean = false,
    themeColor: Color = DefaultThemeColor,
    content: @Composable () -> Unit,
) {
    // The default palette now maps to the Aurora brand color instead of the wallpaper colors.
    val seedColor = if (themeColor == DefaultThemeColor) AuroraSeedColor else themeColor

    val baseColorScheme = rememberDynamicColorScheme(
        seedColor = seedColor,
        isDark = darkTheme,
        specVersion = ColorSpec.SpecVersion.SPEC_2025,
        style = PaletteStyle.Vibrant,
    )

    val colorScheme = remember(baseColorScheme, pureBlack, darkTheme) {
        when {
            darkTheme && pureBlack -> baseColorScheme.pureBlack(true)
            darkTheme -> baseColorScheme.midnight()
            else -> baseColorScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        shapes = AuroraShapes,
        typography = AuroraTypography,
        content = content,
    )
}

/**
 * Deepens dark-theme surfaces toward black for the "Aurora" night look,
 * keeping the tonal hierarchy between surface container levels.
 */
private fun ColorScheme.midnight(): ColorScheme {
    fun Color.deepen(fraction: Float) = lerp(this, Color.Black, fraction)
    return copy(
        background = background.deepen(0.45f),
        surface = surface.deepen(0.45f),
        surfaceDim = surfaceDim.deepen(0.45f),
        surfaceContainerLowest = surfaceContainerLowest.deepen(0.45f),
        surfaceContainerLow = surfaceContainerLow.deepen(0.35f),
        surfaceContainer = surfaceContainer.deepen(0.3f),
        surfaceContainerHigh = surfaceContainerHigh.deepen(0.25f),
        surfaceContainerHighest = surfaceContainerHighest.deepen(0.2f),
    )
}

fun Bitmap.extractThemeColor(): Color = Color(
    Palette.from(this)
        .maximumColorCount(8)
        .generate()
        .rankedColors(1, DefaultThemeColor.toArgb())
        .first()
)

internal fun Palette.rankedColors(
    desiredColorCount: Int,
    fallbackColor: Int,
): List<Int> = Score.score(
    swatches.associate { it.rgb to it.population },
    desiredColorCount,
    fallbackColor,
    true,
)

fun ColorScheme.pureBlack(apply: Boolean) =
    if (apply) copy(
        surface = Color.Black,
        background = Color.Black
    ) else this

val ColorSaver = object : Saver<Color, Int> {
    override fun restore(value: Int): Color = Color(value)
    override fun SaverScope.save(value: Color): Int = value.toArgb()
}
