package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChainIconType
import com.example.model.TokenIconType
import com.example.ui.theme.ElysiumBnbYellow
import com.example.ui.theme.ElysiumBtcOrange
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumSolanaPurple
import com.example.ui.theme.ElysiumTetherGreen

@Composable
fun TokenLogo(
    iconType: TokenIconType,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    when (iconType) {
        TokenIconType.ETH -> EthLogo(modifier = modifier, size = size)
        TokenIconType.BNB -> BnbLogo(modifier = modifier, size = size)
        TokenIconType.HYPE -> HypeLogo(modifier = modifier, size = size)
        TokenIconType.BTC -> BtcLogo(modifier = modifier, size = size)
        TokenIconType.USDT -> UsdtLogo(modifier = modifier, size = size)
        TokenIconType.SOL -> SolLogo(modifier = modifier, size = size)
        TokenIconType.USDC -> UsdcLogo(modifier = modifier, size = size)
    }
}

@Composable
fun ChainLogo(
    iconType: ChainIconType,
    modifier: Modifier = Modifier,
    size: Dp = 20.dp
) {
    when (iconType) {
        ChainIconType.ETH -> EthLogo(modifier = modifier, size = size, isPill = true)
        ChainIconType.BNB -> BnbLogo(modifier = modifier, size = size)
        ChainIconType.HYPERLIQUID -> HypeLogo(modifier = modifier, size = size)
        ChainIconType.SOLANA -> SolLogo(modifier = modifier, size = size)
        ChainIconType.ARBITRUM -> ArbitrumLogo(modifier = modifier, size = size)
    }
}

@Composable
fun EthLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    isPill: Boolean = false
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(if (isPill) Color(0xFF1E2430) else Color(0xFF2A2E39)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.55f)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f

            // Top right facet
            val pathTopRight = Path().apply {
                moveTo(cx, 0f)
                lineTo(w, h * 0.51f)
                lineTo(cx, h * 0.69f)
                close()
            }
            drawPath(pathTopRight, Color.White.copy(alpha = 0.95f))

            // Top left facet
            val pathTopLeft = Path().apply {
                moveTo(cx, 0f)
                lineTo(0f, h * 0.51f)
                lineTo(cx, h * 0.69f)
                close()
            }
            drawPath(pathTopLeft, Color.White.copy(alpha = 0.60f))

            // Bottom right facet
            val pathBottomRight = Path().apply {
                moveTo(cx, h * 0.75f)
                lineTo(w, h * 0.55f)
                lineTo(cx, h)
                close()
            }
            drawPath(pathBottomRight, Color.White.copy(alpha = 0.95f))

            // Bottom left facet
            val pathBottomLeft = Path().apply {
                moveTo(cx, h * 0.75f)
                lineTo(0f, h * 0.55f)
                lineTo(cx, h)
                close()
            }
            drawPath(pathBottomLeft, Color.White.copy(alpha = 0.60f))
        }
    }
}

@Composable
fun BnbLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(ElysiumBnbYellow),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.6f)) {
            val w = this.size.width
            val h = this.size.height
            val cx = w / 2f
            val cy = h / 2f
            val s = w * 0.22f

            // Center diamond
            val centerDiamond = Path().apply {
                moveTo(cx, cy - s)
                lineTo(cx + s, cy)
                lineTo(cx, cy + s)
                lineTo(cx - s, cy)
                close()
            }
            drawPath(centerDiamond, Color(0xFF1E2026))

            // Top diamond
            val topDiamond = Path().apply {
                moveTo(cx, cy - s * 2.2f)
                lineTo(cx + s * 0.7f, cy - s * 1.5f)
                lineTo(cx, cy - s * 0.8f)
                lineTo(cx - s * 0.7f, cy - s * 1.5f)
                close()
            }
            drawPath(topDiamond, Color(0xFF1E2026))

            // Bottom diamond
            val bottomDiamond = Path().apply {
                moveTo(cx, cy + s * 2.2f)
                lineTo(cx + s * 0.7f, cy + s * 1.5f)
                lineTo(cx, cy + s * 0.8f)
                lineTo(cx - s * 0.7f, cy + s * 1.5f)
                close()
            }
            drawPath(bottomDiamond, Color(0xFF1E2026))

            // Left diamond
            val leftDiamond = Path().apply {
                moveTo(cx - s * 2.2f, cy)
                lineTo(cx - s * 1.5f, cy - s * 0.7f)
                lineTo(cx - s * 0.8f, cy)
                lineTo(cx - s * 1.5f, cy + s * 0.7f)
                close()
            }
            drawPath(leftDiamond, Color(0xFF1E2026))

            // Right diamond
            val rightDiamond = Path().apply {
                moveTo(cx + s * 2.2f, cy)
                lineTo(cx + s * 1.5f, cy - s * 0.7f)
                lineTo(cx + s * 0.8f, cy)
                lineTo(cx + s * 1.5f, cy + s * 0.7f)
                close()
            }
            drawPath(rightDiamond, Color(0xFF1E2026))
        }
    }
}

@Composable
fun UsdtLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(ElysiumTetherGreen),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "₮",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.55f).sp
        )
    }
}

@Composable
fun HypeLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFF0F443F), Color(0xFF072624))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.55f)) {
            val w = this.size.width
            val h = this.size.height

            // Stylized H lettermark in mint
            val path = Path().apply {
                // Left vertical
                moveTo(w * 0.18f, h * 0.15f)
                lineTo(w * 0.38f, h * 0.15f)
                lineTo(w * 0.38f, h * 0.85f)
                lineTo(w * 0.18f, h * 0.85f)
                close()

                // Right vertical
                moveTo(w * 0.62f, h * 0.15f)
                lineTo(w * 0.82f, h * 0.15f)
                lineTo(w * 0.82f, h * 0.85f)
                lineTo(w * 0.62f, h * 0.85f)
                close()

                // Center bar
                moveTo(w * 0.38f, h * 0.42f)
                lineTo(w * 0.62f, h * 0.42f)
                lineTo(w * 0.62f, h * 0.58f)
                lineTo(w * 0.38f, h * 0.58f)
                close()
            }
            drawPath(path, ElysiumMint)
        }
    }
}

@Composable
fun SolLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF141026)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size * 0.6f)) {
            val w = this.size.width
            val h = this.size.height
            val barH = h * 0.2f
            val grad = Brush.horizontalGradient(
                colors = listOf(Color(0xFF00FFA3), Color(0xFFDC1FFF))
            )

            // Top parallelogram
            val p1 = Path().apply {
                moveTo(0f, 0f)
                lineTo(w * 0.85f, 0f)
                lineTo(w, barH)
                lineTo(w * 0.15f, barH)
                close()
            }
            drawPath(p1, grad)

            // Middle parallelogram
            val p2 = Path().apply {
                moveTo(w * 0.15f, h * 0.4f)
                lineTo(w, h * 0.4f)
                lineTo(w * 0.85f, h * 0.4f + barH)
                lineTo(0f, h * 0.4f + barH)
                close()
            }
            drawPath(p2, grad)

            // Bottom parallelogram
            val p3 = Path().apply {
                moveTo(0f, h * 0.8f)
                lineTo(w * 0.85f, h * 0.8f)
                lineTo(w, h * 0.8f + barH)
                lineTo(w * 0.15f, h * 0.8f + barH)
                close()
            }
            drawPath(p3, grad)
        }
    }
}

@Composable
fun BtcLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(ElysiumBtcOrange),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "₿",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.6f).sp
        )
    }
}

@Composable
fun UsdcLogo(
    modifier: Modifier = Modifier,
    size: Dp = 44.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF2775CA)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.55f).sp
        )
    }
}

@Composable
fun ArbitrumLogo(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF28A0F0)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "A",
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = (size.value * 0.6f).sp
        )
    }
}
