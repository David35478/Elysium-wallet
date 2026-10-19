package com.example.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Canvas & Backgrounds
val ElysiumCanvas = Color(0xFF090B0E)
val ElysiumSurfaceDark = Color(0xFF0C0E12)
val ElysiumCardBg = Color(0xFF13151A)
val ElysiumCardAlt = Color(0xFF181B21)
val ElysiumCardBorder = Color(0x0FFFFFFF) // rgba(255, 255, 255, 0.06)
val ElysiumPillBg = Color(0xFF191C22)
val ElysiumPillActive = Color(0xFF232730)
val ElysiumKeyBg = Color(0xFF171A20)
val ElysiumKeyPressed = Color(0xFF242933)

// Onboarding & Luxury Teal Theme Tokens
val ElysiumBgDark = Color(0xFF111317)
val ElysiumPrimaryFixed = Color(0xFF8DF5DC)
val ElysiumPrimaryFixedDim = Color(0xFF70D9C1)
val ElysiumSecondaryContainer = Color(0xFF274D4A)
val ElysiumSecondaryFixed = Color(0xFFC2EBE5)
val ElysiumSecondaryFixedDim = Color(0xFFA7CEC9)
val ElysiumSurfaceContainerLow = Color(0xFF1A1C1F)
val ElysiumSurfaceContainer = Color(0xFF1E2023)
val ElysiumSurfaceContainerHigh = Color(0xFF282A2D)
val ElysiumSurfaceContainerHighest = Color(0xFF333538)
val ElysiumSurfaceContainerLowest = Color(0xFF0C0E11)
val ElysiumOnSurface = Color(0xFFE2E2E6)
val ElysiumOnSurfaceVariant = Color(0xFFBDC9C4)

// Hyperliquid Brand & Teal/Mint Gradients
val ElysiumTealDeep = Color(0xFF0B3431)
val ElysiumTealMid = Color(0xFF10443F)
val ElysiumTealDarkest = Color(0xFF071918)
val ElysiumTealBottom = Color(0xFF072624)
val ElysiumMint = Color(0xFF97FFE5)
val ElysiumMintLight = Color(0xFFB0FFED)

// Typography & Accents
val ElysiumTextPrimary = Color(0xFFFFFFFF)
val ElysiumTextSecondary = Color(0x99FFFFFF) // 60%
val ElysiumTextTertiary = Color(0x66FFFFFF) // 40%
val ElysiumTextMuted = Color(0xFF7D828E)
val ElysiumTextSubtle = Color(0xFF5D626E)
val ElysiumTextDisabled = Color(0xFF555A67)

// Status & Accents
val ElysiumGreenPositive = Color(0xFF34D399)
val ElysiumAmber = Color(0xFFF59E0B)
val ElysiumAmberBg = Color(0xFF292218)
val ElysiumAmberBorder = Color(0x33D97706)
val ElysiumBnbYellow = Color(0xFFF3BA2F)
val ElysiumTetherGreen = Color(0xFF26A17B)
val ElysiumSolanaPurple = Color(0xFF9945FF)
val ElysiumBtcOrange = Color(0xFFF7931A)
val ElysiumUsdcBlue = Color(0xFF2775CA)

// Glass Pill & Buttons
val ElysiumGlassPill = Color(0x14FFFFFF)
val ElysiumGlassPillBorder = Color(0x1FFFFFFF)
val ElysiumButtonDisabledBg = Color(0xFF171A21)
val ElysiumButtonDisabledText = Color(0xFF555A67)

// Hyperliquid Card Gradients
fun hyperliquidCardBrush(): Brush {
    return Brush.radialGradient(
        colors = listOf(
            Color(0x57B0FFED), // rgba(176, 255, 237, 0.34)
            Color(0xF210443F), // rgba(16, 68, 63, 0.95)
            ElysiumTealDeep,
            ElysiumTealBottom
        ),
        center = Offset(850f, 150f),
        radius = 800f
    )
}

fun hyperliquidHeaderBrush(): Brush {
    return Brush.linearGradient(
        colors = listOf(
            Color(0xFF14524B),
            Color(0xFF0F3E39),
            Color(0xFF0B2E2B),
            Color(0xFF071918)
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, 500f)
    )
}
