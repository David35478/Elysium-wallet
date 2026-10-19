package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TokenAsset
import com.example.ui.theme.ElysiumCanvas
import com.example.ui.theme.ElysiumCardBg
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumPillBg
import com.example.ui.theme.ElysiumTextMuted
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSubtle
import com.example.ui.theme.ElysiumTextTertiary

@Composable
fun SwapCardsSection(
    payToken: TokenAsset,
    payAmount: String,
    payFiatEstimate: String,
    receiveToken: TokenAsset,
    receiveAmount: String,
    receiveFiatEstimate: String,
    onPayTokenClick: () -> Unit,
    onReceiveTokenClick: () -> Unit,
    onMaxClick: () -> Unit,
    onInvertSwap: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // PAY CARD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(122.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(ElysiumCardBg)
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(22.dp))
                    .padding(16.dp)
                    .testTag("pay_card")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pay",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElysiumTextMuted
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (payAmount.isEmpty()) "0" else payAmount,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Medium,
                                color = ElysiumTextPrimary,
                                letterSpacing = (-0.3).sp,
                                lineHeight = 32.sp,
                                modifier = Modifier.testTag("pay_amount_display")
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = payFiatEstimate,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ElysiumTextSubtle
                            )
                        }

                        Column(
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Pay Token Pill
                            TokenSelectPill(
                                token = payToken,
                                onClick = onPayTokenClick,
                                testTag = "pay_token_selector_pill"
                            )

                            // Max Balance Row
                            Row(
                                modifier = Modifier
                                    .clickable(onClick = onMaxClick)
                                    .padding(end = 4.dp)
                                    .testTag("max_balance_button"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = String.format("%.4f", payToken.balance),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF737885)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Max",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ElysiumTextPrimary.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // RECEIVE CARD
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(122.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(ElysiumCardBg)
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(22.dp))
                    .padding(16.dp)
                    .testTag("receive_card")
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Receive",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = ElysiumTextMuted
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (receiveAmount.isEmpty()) "0" else receiveAmount,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Medium,
                                color = ElysiumTextPrimary,
                                letterSpacing = (-0.3).sp,
                                lineHeight = 32.sp,
                                modifier = Modifier.testTag("receive_amount_display")
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = receiveFiatEstimate,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = ElysiumTextSubtle
                            )
                        }

                        // Receive Token Pill
                        TokenSelectPill(
                            token = receiveToken,
                            onClick = onReceiveTokenClick,
                            testTag = "receive_token_selector_pill"
                        )
                    }
                }
            }
        }

        // Overlapping Center Vertical Swap Button
        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(
            targetValue = if (isPressed) 0.88f else 1.0f,
            label = "swap_btn_scale"
        )

        Box(
            modifier = Modifier
                .scale(scale)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF111317))
                .border(3.dp, ElysiumCanvas, CircleShape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onInvertSwap
                )
                .testTag("vertical_swap_toggle_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SwapVert,
                contentDescription = "Swap Pay and Receive",
                tint = ElysiumTextTertiary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun TokenSelectPill(
    token: TokenAsset,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(ElysiumPillBg)
            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TokenLogo(
                iconType = token.iconType,
                size = 20.dp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = token.symbol,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = ElysiumTextPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = ElysiumTextTertiary,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
