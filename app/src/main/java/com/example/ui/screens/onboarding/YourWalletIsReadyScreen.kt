package com.example.ui.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WalletAccount
import com.example.ui.theme.ElysiumBgDark
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumGreenPositive
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumOnSurfaceVariant
import com.example.ui.theme.ElysiumPillBg
import com.example.ui.theme.ElysiumPrimaryFixed
import com.example.ui.theme.ElysiumSecondaryContainer
import com.example.ui.theme.ElysiumSecondaryFixed
import com.example.ui.theme.ElysiumSecondaryFixedDim
import com.example.ui.theme.ElysiumSurfaceContainer
import com.example.ui.theme.ElysiumSurfaceContainerHigh
import com.example.ui.theme.ElysiumSurfaceContainerLow
import com.example.ui.theme.ElysiumSurfaceContainerLowest
import com.example.ui.theme.ElysiumTealDarkest
import com.example.ui.theme.ElysiumTealDeep
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary
import com.example.ui.theme.ElysiumTextTertiary
import kotlinx.coroutines.delay

/**
 * Screen F: "Your Wallet Is Ready"
 * Pixel-faithful Jetpack Compose recreation of "elysium_wallet_ready_success.html".
 * Displays glowing mint/teal checkmark emblem, verified EVM address pill with copy button,
 * self-custody and backup security credentials, status cards, and primary "Open Wallet" CTA.
 */
@Composable
fun YourWalletIsReadyScreen(
    walletAccount: WalletAccount,
    isBackupVerified: Boolean = true,
    isBiometricEnabled: Boolean = true,
    onExploreWalletClick: () -> Unit,
    onOpenSecuritySettingsClick: () -> Unit = {},
    onCopyAddress: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    BackHandler {
        onExploreWalletClick()
    }

    val clipboardManager = LocalClipboardManager.current
    var copiedRecently by remember { mutableStateOf(false) }

    LaunchedEffect(copiedRecently) {
        if (copiedRecently) {
            delay(2000)
            copiedRecently = false
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_halo")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Visual formatting of address: truncate visually while retaining full string for copying
    val truncatedAddress = remember(walletAccount.fullAddress) {
        if (walletAccount.fullAddress.length > 14) {
            "${walletAccount.fullAddress.take(8)}...${walletAccount.fullAddress.takeLast(6)}"
        } else {
            walletAccount.fullAddress
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElysiumBgDark)
            .statusBarsPadding()
            .testTag("onboarding_ready_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // Glowing Success Emblem with Layered Teal Radiance
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(130.dp)
                    .testTag("ready_emblem_box")
            ) {
                // Outermost soft ambient glow
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    ElysiumPrimaryFixed.copy(alpha = 0.35f),
                                    ElysiumSecondaryContainer.copy(alpha = 0.15f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Secondary ring
                Box(
                    modifier = Modifier
                        .size(92.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    ElysiumSecondaryContainer.copy(alpha = 0.7f),
                                    ElysiumTealDarkest
                                )
                            )
                        )
                        .border(1.5.dp, ElysiumPrimaryFixed.copy(alpha = 0.5f), CircleShape)
                )

                // Inner core with mint checkmark
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    ElysiumPrimaryFixed,
                                    ElysiumMint
                                )
                            )
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Success checkmark",
                        tint = ElysiumTealDeep,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Security Badge Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(ElysiumPrimaryFixed.copy(alpha = 0.12f))
                    .border(1.dp, ElysiumPrimaryFixed.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(ElysiumGreenPositive)
                )
                Spacer(modifier = Modifier.width(7.dp))
                Text(
                    text = "VAULT SECURED & INITIALIZED",
                    color = ElysiumPrimaryFixed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Heading
            Text(
                text = "Your Wallet Is Ready",
                color = ElysiumTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Supporting text
            Text(
                text = "Your Web3 journey starts here. Your private keys are securely encrypted on this device.",
                color = ElysiumOnSurfaceVariant,
                fontSize = 14.sp,
                lineHeight = 21.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Wallet Card: Wallet 1 | Subtitle: Elysium Native Keyring
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ElysiumSurfaceContainerLow,
                                ElysiumSurfaceContainerLowest
                            )
                        )
                    )
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(24.dp))
                    .padding(20.dp)
                    .testTag("ready_wallet_card")
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = walletAccount.name.ifBlank { "Wallet 1" },
                                color = ElysiumTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Elysium Native Keyring",
                                color = ElysiumTextTertiary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Security Indicators
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            // Self-Custody Active indicator
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(ElysiumGreenPositive.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(ElysiumGreenPositive)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Self-Custody Active",
                                    color = ElysiumGreenPositive,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary indicator: 12-Word Secured or Recovery Phrase Imported
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElysiumSecondaryContainer.copy(alpha = 0.5f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = ElysiumPrimaryFixed,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBackupVerified) "12-Word Secured" else "Recovery Phrase Imported",
                            color = ElysiumPrimaryFixed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // EVM Public Address Container with Working Copy and Feedback
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElysiumPillBg)
                            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(14.dp))
                            .clickable {
                                clipboardManager.setText(AnnotatedString(walletAccount.fullAddress))
                                copiedRecently = true
                                onCopyAddress(walletAccount.fullAddress)
                            }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                            .testTag("ready_copy_address_pill")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "EVM Address",
                                    color = ElysiumTextTertiary,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = truncatedAddress,
                                    color = ElysiumSecondaryFixed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (copiedRecently) ElysiumGreenPositive.copy(alpha = 0.15f)
                                        else ElysiumSurfaceContainerHigh
                                    )
                            ) {
                                Icon(
                                    imageVector = if (copiedRecently) Icons.Filled.Done else Icons.Filled.ContentCopy,
                                    contentDescription = "Copy address",
                                    tint = if (copiedRecently) ElysiumGreenPositive else ElysiumPrimaryFixed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = copiedRecently,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = "Full EVM address copied to clipboard!",
                            color = ElysiumGreenPositive,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(top = 6.dp, start = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Status Cards: Biometrics & Local Key Protection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Biometrics Status Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ElysiumSurfaceContainerLow)
                        .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Fingerprint,
                                contentDescription = null,
                                tint = ElysiumPrimaryFixed,
                                modifier = Modifier.size(20.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isBiometricEnabled) ElysiumGreenPositive else ElysiumTextTertiary)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Biometrics",
                            color = ElysiumTextTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isBiometricEnabled) "Enabled" else "Not Enabled",
                            color = ElysiumTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Local Key Protection Status Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(ElysiumSurfaceContainerLow)
                        .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = ElysiumPrimaryFixed,
                                modifier = Modifier.size(20.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(ElysiumGreenPositive)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Key Protection",
                            color = ElysiumTextTertiary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Android Keystore",
                            color = ElysiumTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Security Highlights Row
            SecurityFeatureRow(
                icon = Icons.Filled.Shield,
                title = "Zero Telemetry — 100% Offline Key",
                desc = "Private key generated on device, never sent to any server or API"
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Primary Action CTA: "Open Wallet"
            Button(
                onClick = onExploreWalletClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElysiumPrimaryFixed,
                    contentColor = ElysiumTealDeep
                ),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("btn_explore_wallet")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Open Wallet",
                        fontSize = 16.sp,
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

            Spacer(modifier = Modifier.height(12.dp))

            // Secondary Action CTA: "Explore Security Settings"
            OutlinedButton(
                onClick = onOpenSecuritySettingsClick,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = ElysiumPrimaryFixed
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElysiumCardBorder),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_explore_security_settings")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Explore Security Settings",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SecurityFeatureRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(ElysiumSurfaceContainerLow)
            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(ElysiumPrimaryFixed.copy(alpha = 0.12f))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ElysiumPrimaryFixed,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = title,
                color = ElysiumTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                color = ElysiumTextTertiary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }
    }
}
