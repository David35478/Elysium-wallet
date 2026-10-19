package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Screen
import com.example.ui.components.ChainSelectorRow
import com.example.ui.components.NumericKeypad
import com.example.ui.components.SwapCardsSection
import com.example.ui.theme.ElysiumButtonDisabledBg
import com.example.ui.theme.ElysiumButtonDisabledText
import com.example.ui.theme.ElysiumCanvas
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumTealDarkest
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.hyperliquidHeaderBrush
import com.example.viewmodel.WalletUiState
import com.example.viewmodel.WalletViewModel

@Composable
fun SwapScreen(
    uiState: WalletUiState,
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    // Handle back button to return to Home
    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    val isAmountValid = uiState.payAmount.isNotEmpty() && (uiState.payAmount.toDoubleOrNull() ?: 0.0) > 0.0

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElysiumCanvas)
            .testTag("swap_screen")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TOP HEADER SECTION (Hyperliquid dark teal gradient)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                    .background(hyperliquidHeaderBrush())
                    .border(
                        width = 1.dp,
                        color = Color(0x3310443F),
                        shape = RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
                    .testTag("swap_header_section")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        // Wallet Name & Selector
                        Row(
                            modifier = Modifier
                                .clickable { viewModel.setShowWalletSelector(true) }
                                .testTag("swap_wallet_name_selector"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.selectedWallet.name,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElysiumTextPrimary,
                                letterSpacing = 0.2.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Switch Wallet",
                                tint = Color(0xB3FFFFFF),
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Address & Copy Button
                        Row(
                            modifier = Modifier
                                .clickable { viewModel.copyAddressToClipboard() }
                                .testTag("swap_copy_address_button"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = uiState.selectedWallet.shortAddress,
                                fontSize = 12.sp,
                                color = Color(0x99FFFFFF)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Copy Address",
                                tint = Color(0x80FFFFFF),
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }

                    // Circular Close Button (Returns to Home)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0x40000000))
                            .border(1.dp, Color(0x1AFFFFFF), CircleShape)
                            .clickable { viewModel.navigateTo(Screen.HOME) }
                            .testTag("swap_close_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Swap",
                            tint = Color(0xCCFFFFFF),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // MAIN CONTENT AREA
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Chain Selector Row (BNB Chain <-> Ethereum)
                    ChainSelectorRow(
                        sourceChain = uiState.sourceChain,
                        destinationChain = uiState.destinationChain,
                        onSourceChainClick = { viewModel.setShowSourceChainSelector(true) },
                        onDestinationChainClick = { viewModel.setShowDestinationChainSelector(true) },
                        onToggleChains = { viewModel.toggleChains() }
                    )

                    // Pay and Receive Cards Relative Area
                    SwapCardsSection(
                        payToken = uiState.payToken,
                        payAmount = uiState.payAmount,
                        payFiatEstimate = uiState.payFiatEstimate,
                        receiveToken = uiState.receiveToken,
                        receiveAmount = uiState.receiveAmount,
                        receiveFiatEstimate = uiState.receiveFiatEstimate,
                        onPayTokenClick = { viewModel.setShowPayTokenSelector(true) },
                        onReceiveTokenClick = { viewModel.setShowReceiveTokenSelector(true) },
                        onMaxClick = { viewModel.setMaxPayAmount() },
                        onInvertSwap = { viewModel.invertSwapTokens() }
                    )

                    // Action Button (CTA)
                    val ctaBgColor by animateColorAsState(
                        targetValue = if (isAmountValid) ElysiumMint else ElysiumButtonDisabledBg,
                        label = "cta_bg_color"
                    )
                    val ctaTextColor by animateColorAsState(
                        targetValue = if (isAmountValid) ElysiumTealDarkest else ElysiumButtonDisabledText,
                        label = "cta_text_color"
                    )

                    Button(
                        onClick = {
                            if (isAmountValid) {
                                viewModel.setShowSwapConfirmationSheet(true)
                            }
                        },
                        enabled = isAmountValid,
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ctaBgColor,
                            disabledContainerColor = ElysiumButtonDisabledBg,
                            contentColor = ctaTextColor,
                            disabledContentColor = ElysiumButtonDisabledText
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("swap_cta_button")
                    ) {
                        Text(
                            text = if (isAmountValid) "Review Swap" else "Enter the amount",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // NUMERIC KEYPAD
                NumericKeypad(
                    onDigitClick = { digit -> viewModel.onKeypadDigit(digit) },
                    onBackspaceClick = { viewModel.onKeypadBackspace() },
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }
    }
}
