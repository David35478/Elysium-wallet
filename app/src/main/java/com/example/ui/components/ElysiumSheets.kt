package com.example.ui.components

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.TokenAsset
import com.example.ui.theme.ElysiumAmber
import com.example.ui.theme.ElysiumCardAlt
import com.example.ui.theme.ElysiumCardBg
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumGreenPositive
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumPillBg
import com.example.ui.theme.ElysiumTealDarkest
import com.example.ui.theme.ElysiumTextMuted
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary
import com.example.ui.theme.ElysiumTextTertiary

@Composable
fun TokenDetailDialog(
    token: TokenAsset,
    onDismiss: () -> Unit,
    onSwapClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(ElysiumCardBg)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(26.dp))
                .padding(20.dp)
                .testTag("token_detail_dialog")
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Asset Details",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElysiumTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ElysiumTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TokenLogo(iconType = token.iconType, size = 56.dp)

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = token.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElysiumTextPrimary
                )

                Text(
                    text = "${token.symbol} • ${token.chain}",
                    fontSize = 13.sp,
                    color = ElysiumTextTertiary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Balance Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ElysiumCardAlt)
                        .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Your Balance", fontSize = 12.sp, color = ElysiumTextMuted)
                            Text(
                                text = token.balanceDisplay,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElysiumTextPrimary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Value (USD)", fontSize = 12.sp, color = ElysiumTextMuted)
                            Text(
                                text = token.fiatDisplay,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElysiumMint,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onSwapClick,
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElysiumMint,
                        contentColor = ElysiumTealDarkest
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("token_detail_swap_button")
                ) {
                    Text(
                        text = "Trade ${token.symbol} in Swap",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun SeedBackupDialog(
    onDismiss: () -> Unit,
    onVerified: () -> Unit
) {
    val sampleWords = listOf(
        "elysium", "crystal", "hyper", "liquid",
        "orbital", "beacon", "velvet", "matrix",
        "quantum", "falcon", "zenith", "aurora"
    )

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(ElysiumCardBg)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(26.dp))
                .padding(20.dp)
                .testTag("seed_backup_dialog")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Security,
                            contentDescription = null,
                            tint = ElysiumAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Secret Recovery Phrase",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElysiumTextPrimary
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ElysiumTextSecondary
                        )
                    }
                }

                Text(
                    text = "Sample mock phrase for preview. In the production app, your secrets will be secured in Android Keystore.",
                    fontSize = 12.sp,
                    color = ElysiumTextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Words Grid (3 columns x 4 rows)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ElysiumCardAlt)
                        .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (row in 0 until 4) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (col in 0 until 3) {
                                val index = row * 3 + col
                                val word = sampleWords[index]
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(ElysiumPillBg)
                                        .padding(vertical = 6.dp, horizontal = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}. $word",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = ElysiumTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onVerified()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElysiumMint,
                        contentColor = ElysiumTealDarkest
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_seed_backup_button")
                ) {
                    Text(
                        text = "I Saved My Phrase",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun SwapConfirmationDialog(
    payToken: TokenAsset,
    payAmount: String,
    receiveToken: TokenAsset,
    receiveAmount: String,
    sourceChainName: String,
    destChainName: String,
    onDismiss: () -> Unit,
    onConfirmed: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(ElysiumCardBg)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(26.dp))
                .padding(20.dp)
                .testTag("swap_confirmation_dialog")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Review Swap Order",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElysiumTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ElysiumTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(ElysiumCardAlt)
                        .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("You Pay", fontSize = 13.sp, color = ElysiumTextMuted)
                            Text(
                                text = "$payAmount ${payToken.symbol} ($sourceChainName)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElysiumTextPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("You Receive", fontSize = 13.sp, color = ElysiumTextMuted)
                            Text(
                                text = "$receiveAmount ${receiveToken.symbol} ($destChainName)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElysiumMint
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Slippage Tolerance", fontSize = 12.sp, color = ElysiumTextTertiary)
                            Text("0.5%", fontSize = 12.sp, color = ElysiumTextSecondary)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Estimated Network Fee", fontSize = 12.sp, color = ElysiumTextTertiary)
                            Text("≈ $0.15 (Sample)", fontSize = 12.sp, color = ElysiumTextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        onConfirmed()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElysiumMint,
                        contentColor = ElysiumTealDarkest
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("confirm_sample_swap_button")
                ) {
                    Text(
                        text = "Simulate Swap (Sample Only)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
