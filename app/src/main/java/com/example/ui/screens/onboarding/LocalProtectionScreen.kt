package com.example.ui.screens.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElysiumBgDark
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumOnSurface
import com.example.ui.theme.ElysiumOnSurfaceVariant
import com.example.ui.theme.ElysiumPrimaryFixed
import com.example.ui.theme.ElysiumPrimaryFixedDim
import com.example.ui.theme.ElysiumSecondaryContainer
import com.example.ui.theme.ElysiumSecondaryFixed
import com.example.ui.theme.ElysiumSurfaceContainer
import com.example.ui.theme.ElysiumSurfaceContainerHigh
import com.example.ui.theme.ElysiumSurfaceContainerHighest
import com.example.ui.theme.ElysiumSurfaceContainerLow
import com.example.ui.theme.ElysiumSurfaceContainerLowest
import com.example.ui.theme.ElysiumTealDarkest
import com.example.ui.theme.ElysiumTextMuted
import com.example.ui.theme.ElysiumTextTertiary

@Composable
fun LocalProtectionScreen(
    onBackClick: () -> Unit,
    onCreateWalletClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var termsAccepted by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElysiumBgDark)
            .statusBarsPadding()
            .testTag("onboarding_local_protection_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ElysiumSurfaceContainerHigh)
                        .testTag("local_protection_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ElysiumOnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "Security Setup",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.size(38.dp))
            }

            // Progress Tracker: Step 1 / 3
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(ElysiumPrimaryFixed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "GENERATION SEQUENCE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElysiumSecondaryFixed,
                        letterSpacing = 0.5.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElysiumSurfaceContainerHigh)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Step 1 / 3",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            // 3 Segmented Progress Bars
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(ElysiumPrimaryFixed)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(ElysiumSurfaceContainerHighest)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(ElysiumSurfaceContainerHighest)
                )
            }

            // Luxury Hero Card (Dark Teal Gradient Atmosphere)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ElysiumSecondaryContainer,
                                ElysiumSurfaceContainerHigh,
                                ElysiumSurfaceContainerLowest
                            )
                        )
                    )
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElysiumSurfaceContainerLowest.copy(alpha = 0.7f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "HARDWARE ENCLAVE READY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElysiumSecondaryFixed,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = ElysiumPrimaryFixed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Create Your Wallet",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )

                    Text(
                        text = "Generate a new self-custody wallet and take control of your digital assets with institutional-grade privacy.",
                        fontSize = 13.sp,
                        color = ElysiumOnSurfaceVariant,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Derivation curve badge
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElysiumSurfaceContainerLowest.copy(alpha = 0.5f))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Derivation Curve",
                                fontSize = 12.sp,
                                color = ElysiumOnSurfaceVariant
                            )
                            Text(
                                text = "BIP-44 • EVM (m/44'/60'/0'/0/0)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElysiumSecondaryFixed
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section Label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "SECURITY ARCHITECTURE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElysiumSecondaryFixed,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Self-Custodial",
                    fontSize = 11.sp,
                    color = ElysiumTextTertiary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Feature 1: True Self-Custody
            FeatureCard(
                icon = Icons.Default.Shield,
                title = "True Self-Custody",
                tag = "Private",
                description = "Only you hold the private keys and access to your tokens. Zero intermediary exposure."
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Feature 2: Biometric & Passcode
            FeatureCard(
                icon = Icons.Default.Fingerprint,
                title = "Biometric & Passcode",
                tag = "Secure Enclave",
                description = "Secured locally on your device hardware enclave with instant biometric authorization."
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Feature 3: Cross-Chain Ready
            FeatureCard(
                icon = Icons.Default.Public,
                title = "Cross-Chain Ready",
                tag = "Multi-L1/L2",
                description = "Native instant routing for Arbitrum, Ethereum, Hyperliquid, and BNB Chain."
            )

            Spacer(modifier = Modifier.height(18.dp))

            // High-Stakes Legal & Security Checkbox Acknowledgment
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ElysiumSurfaceContainer)
                    .clickable { termsAccepted = !termsAccepted }
                    .padding(14.dp)
                    .testTag("ack_terms_container")
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (termsAccepted) ElysiumPrimaryFixed else ElysiumSurfaceContainerHighest)
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (termsAccepted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = ElysiumTealDarkest,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "I understand that if I lose my recovery phrase, Elysium cannot restore my wallet or recover my digital assets.",
                        fontSize = 13.sp,
                        color = Color.White,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Interactive Action Button
            val ctaBgColor by animateColorAsState(
                targetValue = if (termsAccepted) ElysiumPrimaryFixed else ElysiumSecondaryContainer.copy(alpha = 0.45f),
                label = "cta_bg"
            )
            val ctaTextColor by animateColorAsState(
                targetValue = if (termsAccepted) ElysiumTealDarkest else ElysiumTextMuted,
                label = "cta_text"
            )

            Button(
                onClick = onCreateWalletClick,
                enabled = termsAccepted,
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ctaBgColor,
                    contentColor = ctaTextColor,
                    disabledContainerColor = ElysiumSecondaryContainer.copy(alpha = 0.45f),
                    disabledContentColor = ElysiumTextMuted
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("local_protection_create_wallet_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Create Wallet",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = ElysiumSecondaryFixed,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Zero server knowledge • Cryptographically isolated",
                    fontSize = 11.sp,
                    color = ElysiumTextTertiary
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    tag: String,
    description: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ElysiumSurfaceContainerLow)
            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(ElysiumSecondaryContainer.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ElysiumPrimaryFixed,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElysiumSecondaryContainer.copy(alpha = 0.3f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = tag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElysiumPrimaryFixedDim
                        )
                    }
                }

                Text(
                    text = description,
                    fontSize = 12.sp,
                    color = ElysiumOnSurfaceVariant,
                    lineHeight = 16.sp,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}
