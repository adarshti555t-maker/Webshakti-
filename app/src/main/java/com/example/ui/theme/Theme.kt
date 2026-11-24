package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val WebShaktiLightColorScheme = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = OnAccentWhite,
    primaryContainer = NavyElevated,
    onPrimaryContainer = TextPrimary,
    secondary = CyanPulse,
    onSecondary = OnAccentWhite,
    secondaryContainer = NavyElevated,
    onSecondaryContainer = CyanPulse,
    tertiary = DeepSapphire,
    onTertiary = OnAccentWhite,
    background = NavyAbyss,
    onBackground = TextPrimary,
    surface = NavySurface,
    onSurface = TextPrimary,
    surfaceVariant = NavyDeep,
    onSurfaceVariant = TextSecondary,
    surfaceContainerLowest = NavyAbyss,
    surfaceContainerLow = NavyDeep,
    surfaceContainer = NavySurface,
    surfaceContainerHigh = NavyElevated,
    outline = NavyBorder,
    outlineVariant = NavyBorder.copy(alpha = 0.6f),
    error = ErrorCoral,
    onError = OnAccentWhite
)

val WebShaktiShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun WebShaktiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = WebShaktiLightColorScheme,
        typography = WebShaktiTypography,
        shapes = WebShaktiShapes,
        content = content
    )
}
