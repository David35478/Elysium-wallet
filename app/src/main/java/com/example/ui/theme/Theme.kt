package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ElysiumColorScheme = darkColorScheme(
    primary = ElysiumMint,
    onPrimary = ElysiumTealDarkest,
    primaryContainer = ElysiumTealMid,
    onPrimaryContainer = ElysiumMintLight,
    secondary = ElysiumMintLight,
    onSecondary = ElysiumTealDeep,
    background = ElysiumCanvas,
    onBackground = ElysiumTextPrimary,
    surface = ElysiumCardBg,
    onSurface = ElysiumTextPrimary,
    surfaceVariant = ElysiumCardAlt,
    onSurfaceVariant = ElysiumTextSecondary,
    outline = ElysiumCardBorder,
    outlineVariant = ElysiumCardBorder
)

@Composable
fun ElysiumTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ElysiumColorScheme,
        typography = Typography,
        content = content
    )
}
