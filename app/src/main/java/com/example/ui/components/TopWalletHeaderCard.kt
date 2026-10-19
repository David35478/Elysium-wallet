package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Paid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WalletAccount
import com.example.ui.theme.ElysiumAmber
import com.example.ui.theme.ElysiumGreenPositive
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumPrimaryFixed
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextTertiary
import com.example.ui.theme.hyperliquidCardBrush

@Composable
fun TopWalletHeaderCard(
    wallet: WalletAccount,
    onSwitchWalletClick: () -> Unit,
    onCopyAddressClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit,
    onSwapClick: () -> Unit,
    onBuySellClick: () -> Unit,
    testnetNetworkName: String = "Elysium Testnet (99801)",
    testnetHypeBalance: String? = null,
    isTestnetLoading: Boolean = false,
    testnetErrorMessage: String? = null,
    onRefreshClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "spin_refresh")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "rotation_angle"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(34.dp))
            .background(hyperliquidCardBrush())
            .border(
                width = 1.dp,
                color = Color(0x33B0FFED),
                shape = RoundedCornerShape(34.dp)
            )
            .padding(horizontal = 20.dp, vertical = 18.dp)
            .testTag("top_balance_card")
    ) {
        Column {
            // Top Row: Wallet Switcher & History Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    // Wallet Selector Title
                    Row(
                        modifier = Modifier
                            .clickable(onClick = onSwitchWalletClick)
                            .testTag("wallet_selector_title"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = wallet.name.ifBlank { "Wallet 1" },
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElysiumTextPrimary,
                            letterSpacing = (-0.2).sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Switch Wallet",
                            tint = Color(0xB3FFFFFF),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Address & Copy Button
                    Row(
                        modifier = Modifier
                            .clickable(onClick = onCopyAddressClick)
                            .testTag("copy_address_button"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = wallet.shortAddress.ifBlank {
                                if (wallet.fullAddress.length > 10) "${wallet.fullAddress.take(6)}...${wallet.fullAddress.takeLast(4)}" else wallet.fullAddress
                            },
                            fontSize = 13.sp,
                            color = Color(0x99FFFFFF)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Outlined.ContentCopy,
                            contentDescription = "Copy Address",
                            tint = Color(0x80FFFFFF),
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Refresh Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x1AFFFFFF))
                            .border(1.dp, Color(0x26FFFFFF), CircleShape)
                            .clickable(onClick = onRefreshClick)
                            .testTag("refresh_balance_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh Balance",
                            tint = ElysiumPrimaryFixed,
                            modifier = Modifier
                                .size(18.dp)
                                .then(if (isTestnetLoading) Modifier.rotate(rotation) else Modifier)
                        )
                    }

                    // History / Activity Button
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0x1AFFFFFF))
                            .border(1.dp, Color(0x26FFFFFF), CircleShape)
                            .clickable(onClick = onHistoryClick)
                            .testTag("transaction_history_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Transaction History",
                            tint = Color(0xE6FFFFFF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Testnet Network Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x268DF5DC))
                    .border(1.dp, Color(0x408DF5DC), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
                    .testTag("network_badge_pill")
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (testnetErrorMessage != null) ElysiumAmber else ElysiumGreenPositive)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = testnetNetworkName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElysiumPrimaryFixed,
                    letterSpacing = 0.3.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Live Native HYPE Balance
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.testTag("hype_balance_row")
            ) {
                Text(
                    text = testnetHypeBalance ?: "0.0",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElysiumTextPrimary,
                    letterSpacing = (-0.5).sp,
                    lineHeight = 40.sp,
                    modifier = Modifier.testTag("total_balance_text")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "HYPE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElysiumPrimaryFixed,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                if (isTestnetLoading) {
                    Spacer(modifier = Modifier.width(10.dp))
                    CircularProgressIndicator(
                        color = ElysiumPrimaryFixed,
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle: Testnet Native Asset (No USD value fabricated)
            Text(
                text = "Native Elysium Testnet Token • Verified RPC Chain ID 99801",
                fontSize = 12.sp,
                color = ElysiumTextTertiary
            )

            // RPC Error Feedback (if any) with Retry Action
            AnimatedVisibility(visible = testnetErrorMessage != null) {
                testnetErrorMessage?.let { errMsg ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElysiumAmber.copy(alpha = 0.15f))
                            .border(1.dp, ElysiumAmber.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .clickable(onClick = onRefreshClick),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = ElysiumAmber,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$errMsg (Tap to retry)",
                            fontSize = 11.sp,
                            color = ElysiumAmber,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4 Action Buttons Row: Send, Receive, Swap, Buy/Sell
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GlassPillButton(
                    label = "Send",
                    testTag = "action_send_button",
                    onClick = onSendClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.CallMade,
                        contentDescription = "Send",
                        tint = Color(0xF2FFFFFF),
                        modifier = Modifier.size(20.dp)
                    )
                }

                GlassPillButton(
                    label = "Receive",
                    testTag = "action_receive_button",
                    onClick = onReceiveClick
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.CallReceived,
                        contentDescription = "Receive",
                        tint = Color(0xF2FFFFFF),
                        modifier = Modifier.size(20.dp)
                    )
                }

                GlassPillButton(
                    label = "Swap",
                    testTag = "action_swap_button",
                    onClick = onSwapClick
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Swap",
                        tint = Color(0xF2FFFFFF),
                        modifier = Modifier.size(22.dp)
                    )
                }

                GlassPillButton(
                    label = "Buy/Sell",
                    testTag = "action_buy_sell_button",
                    onClick = onBuySellClick
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Paid,
                        contentDescription = "Buy or Sell",
                        tint = Color(0xF2FFFFFF),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
