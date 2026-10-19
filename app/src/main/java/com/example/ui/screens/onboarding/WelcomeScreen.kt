package com.example.ui.screens.onboarding

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElysiumBgDark
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumOnSurface
import com.example.ui.theme.ElysiumOnSurfaceVariant
import com.example.ui.theme.ElysiumPrimaryFixed
import com.example.ui.theme.ElysiumSecondaryContainer
import com.example.ui.theme.ElysiumSecondaryFixed
import com.example.ui.theme.ElysiumSecondaryFixedDim
import com.example.ui.theme.ElysiumSurfaceContainer
import com.example.ui.theme.ElysiumSurfaceContainerHigh
import com.example.ui.theme.ElysiumSurfaceContainerHighest
import com.example.ui.theme.ElysiumSurfaceContainerLow
import com.example.ui.theme.ElysiumSurfaceContainerLowest
import com.example.ui.theme.ElysiumTealDeep
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary
import com.example.ui.theme.ElysiumTextTertiary

@Composable
fun WelcomeScreen(
    onAddNewWalletClick: () -> Unit,
    onImportWalletClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElysiumBgDark)
            .statusBarsPadding()
            .testTag("onboarding_welcome_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Top Ambient Glow Decorator & Brand Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ElysiumSurfaceContainerLowest,
                                ElysiumSecondaryContainer.copy(alpha = 0.5f),
                                ElysiumSurfaceContainerLow
                            )
                        )
                    )
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(22.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Elysium Geometric Emblem with Ambient Pulse
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(ElysiumSurfaceContainerHighest.copy(alpha = 0.6f))
                            .border(1.5.dp, ElysiumPrimaryFixed.copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Glowing lettermark E
                        Text(
                            text = "E",
                            fontSize = 38.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = ElysiumPrimaryFixed,
                            letterSpacing = (-1).sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Elysium Wallet",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )

                    Text(
                        text = "Your wallet. Your keys. Your control.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Normal,
                        color = ElysiumSecondaryFixedDim,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Micro-Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElysiumSecondaryContainer.copy(alpha = 0.7f))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
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
                                text = "PASSKEY & ENCLAVE READY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElysiumPrimaryFixed,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Welcome Heading
            Text(
                text = "Your Web3 journey starts here",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "Elysium is a self-custodial wallet. You alone hold the keys to your assets—no middlemen, zero telemetry, and fully encrypted on this device.",
                fontSize = 14.sp,
                color = ElysiumOnSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 8.dp, start = 8.dp, end = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Action: Create New Wallet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(ElysiumPrimaryFixed)
                    .clickable(onClick = onAddNewWalletClick)
                    .padding(horizontal = 20.dp)
                    .testTag("welcome_btn_new_wallet"),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ElysiumTealDeep.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = ElysiumTealDeep,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Create New Wallet",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElysiumTealDeep
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = ElysiumTealDeep,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Action: Import Existing Wallet
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(ElysiumSurfaceContainerHigh)
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(28.dp))
                    .clickable(onClick = onImportWalletClick)
                    .padding(horizontal = 20.dp)
                    .testTag("welcome_btn_import_wallet"),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(ElysiumSurfaceContainerLowest),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = null,
                                tint = ElysiumPrimaryFixed,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Import Existing Wallet",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElysiumPrimaryFixed
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = ElysiumSecondaryFixedDim,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Security Reassurance Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(ElysiumSurfaceContainerLow)
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(ElysiumSecondaryContainer.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = ElysiumPrimaryFixed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Self-custody by design",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Your recovery phrase gives you complete control of your assets. Elysium never holds or stores your keys. Never share them with anyone.",
                            fontSize = 12.sp,
                            color = ElysiumOnSurfaceVariant,
                            lineHeight = 17.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Trust Badges Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElysiumSurfaceContainerLowest.copy(alpha = 0.8f))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = ElysiumPrimaryFixed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Audited Key Derivation",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ElysiumOnSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElysiumSurfaceContainerLowest.copy(alpha = 0.8f))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = ElysiumPrimaryFixed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Zero-Knowledge Vault",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = ElysiumOnSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Text(
                text = "Secure Web3 access • End-to-end encrypted",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = ElysiumPrimaryFixed,
                textAlign = TextAlign.Center
            )

            Text(
                text = "By continuing, you agree to our Terms of Service and Privacy Policy.",
                fontSize = 11.sp,
                color = ElysiumTextTertiary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )
        }
    }
}
