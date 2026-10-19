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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.DappChainSwitchRequest
import com.example.model.DappConnectionRequest
import com.example.model.DappSignMessageRequest
import com.example.model.DappTransactionRequest
import com.example.model.EvmNetworkConfig
import com.example.model.WalletAccount
import com.example.ui.theme.ElysiumAmber
import com.example.ui.theme.ElysiumCardAlt
import com.example.ui.theme.ElysiumCardBg
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumGreenPositive
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumPillBg
import com.example.ui.theme.ElysiumPrimaryFixed
import com.example.ui.theme.ElysiumSecondaryFixedDim
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary
import com.example.ui.theme.ElysiumTextTertiary

@Composable
fun DappConnectDialog(
    request: DappConnectionRequest,
    wallet: WalletAccount,
    network: EvmNetworkConfig,
    onConnect: () -> Unit,
    onReject: () -> Unit
) {
    Dialog(onDismissRequest = onReject) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ElysiumCardBg)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
                .testTag("dapp_connect_dialog")
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(ElysiumCardAlt)
                        .border(1.dp, Color(0x338DF5DC), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = ElysiumMint,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Connection Request",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElysiumTextPrimary
                )

                Text(
                    text = request.hostname,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ElysiumMint,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = request.origin,
                    fontSize = 12.sp,
                    color = ElysiumTextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Account & Network context cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Account card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElysiumCardAlt)
                            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Account",
                                fontSize = 11.sp,
                                color = ElysiumTextTertiary
                            )
                            Text(
                                text = wallet.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElysiumTextPrimary
                            )
                            Text(
                                text = wallet.shortAddress,
                                fontSize = 11.sp,
                                color = ElysiumTextSecondary
                            )
                        }
                    }

                    // Network card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElysiumCardAlt)
                            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Network",
                                fontSize = 11.sp,
                                color = ElysiumTextTertiary
                            )
                            Text(
                                text = network.name,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElysiumTextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = if (network.isTestnet) "Testnet (${network.chainId})" else "Mainnet (${network.chainId})",
                                fontSize = 11.sp,
                                color = ElysiumMint
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Permissions List
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ElysiumPillBg)
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "This site will be able to:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElysiumTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = ElysiumGreenPositive, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("View your account address & chain ID", fontSize = 12.sp, color = ElysiumTextSecondary)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.CheckCircle, contentDescription = null, tint = ElysiumGreenPositive, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Request approval for transactions & signing", fontSize = 12.sp, color = ElysiumTextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Security Note
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = ElysiumAmber, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Your private keys are never exposed to this website.",
                        fontSize = 11.sp,
                        color = ElysiumAmber
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("dapp_connect_reject_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElysiumTextSecondary)
                    ) {
                        Text("Reject", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onConnect,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("dapp_connect_confirm_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElysiumMint,
                            contentColor = Color(0xFF111317)
                        )
                    ) {
                        Text("Connect", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DappSignMessageDialog(
    request: DappSignMessageRequest,
    onSign: () -> Unit,
    onReject: () -> Unit
) {
    Dialog(onDismissRequest = onReject) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ElysiumCardBg)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
                .testTag("dapp_sign_message_dialog")
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Header
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ElysiumCardAlt)
                        .border(1.dp, Color(0x338DF5DC), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = ElysiumMint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Sign Message",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElysiumTextPrimary
                )

                Text(
                    text = request.hostname,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = ElysiumMint,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Account Pill
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElysiumCardAlt)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Signer", fontSize = 12.sp, color = ElysiumTextTertiary)
                        Text(
                            text = "${request.accountAddress.take(6)}...${request.accountAddress.takeLast(4)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElysiumTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Message Content Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF0B0D0F))
                        .border(1.dp, ElysiumCardBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Message", fontSize = 11.sp, color = ElysiumTextTertiary)
                        if (request.isOpaque) {
                            Text(
                                "Raw / Hex",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElysiumAmber,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x33FFA800))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = request.displayMessage,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            color = ElysiumTextPrimary
                        )
                    }
                }

                if (request.isOpaque) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = ElysiumAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "This message contains binary or raw data. Exercise caution.",
                            fontSize = 11.sp,
                            color = ElysiumAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("dapp_sign_reject_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElysiumTextSecondary)
                    ) {
                        Text("Reject", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onSign,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("dapp_sign_confirm_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElysiumMint,
                            contentColor = Color(0xFF111317)
                        )
                    ) {
                        Text("Sign", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DappTransactionDialog(
    request: DappTransactionRequest,
    network: EvmNetworkConfig,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val clipboard = LocalClipboardManager.current

    Dialog(onDismissRequest = onReject) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ElysiumCardBg)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
                .testTag("dapp_transaction_dialog")
        ) {
            Column {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (request.isContractInteraction) "Contract Interaction" else "Send Transaction",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElysiumTextPrimary
                        )
                        Text(
                            text = request.hostname,
                            fontSize = 12.sp,
                            color = ElysiumMint
                        )
                    }

                    // Network badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElysiumCardAlt)
                            .border(1.dp, Color(0x338DF5DC), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = network.name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElysiumMint
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Value Display Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0B0D0F))
                        .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Amount", fontSize = 12.sp, color = ElysiumTextTertiary)
                        Text(
                            text = request.formattedValue,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElysiumMint
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Details Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ElysiumCardAlt)
                        .border(1.dp, ElysiumCardBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("From", fontSize = 12.sp, color = ElysiumTextTertiary)
                            Text(
                                text = "${request.fromAddress.take(6)}...${request.fromAddress.takeLast(4)}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElysiumTextPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("To", fontSize = 12.sp, color = ElysiumTextTertiary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (request.toAddress.length >= 10) "${request.toAddress.take(6)}...${request.toAddress.takeLast(4)}" else request.toAddress,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ElysiumTextPrimary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Outlined.ContentCopy,
                                    contentDescription = "Copy address",
                                    tint = ElysiumTextTertiary,
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clickable { clipboard.setText(AnnotatedString(request.toAddress)) }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Estimated Fee", fontSize = 12.sp, color = ElysiumTextTertiary)
                            Text(
                                text = request.estimatedFeeFormatted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElysiumTextPrimary
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Gas Limit", fontSize = 12.sp, color = ElysiumTextTertiary)
                            Text(
                                text = request.gasLimit.toString(),
                                fontSize = 12.sp,
                                color = ElysiumTextSecondary
                            )
                        }
                    }
                }

                if (request.isContractInteraction) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = ElysiumAmber, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Calldata is present. Verify contract legitimacy before signing.",
                            fontSize = 11.sp,
                            color = ElysiumAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("dapp_tx_reject_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElysiumTextSecondary)
                    ) {
                        Text("Reject", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onApprove,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("dapp_tx_confirm_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElysiumMint,
                            contentColor = Color(0xFF111317)
                        )
                    ) {
                        Text("Approve & Sign", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DappChainSwitchDialog(
    request: DappChainSwitchRequest,
    currentNetwork: EvmNetworkConfig,
    onConfirm: () -> Unit,
    onReject: () -> Unit
) {
    Dialog(onDismissRequest = onReject) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ElysiumCardBg)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
                .testTag("dapp_switch_chain_dialog")
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ElysiumCardAlt)
                        .border(1.dp, Color(0x338DF5DC), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SwapHoriz,
                        contentDescription = null,
                        tint = ElysiumMint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Switch Network",
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElysiumTextPrimary
                )

                Text(
                    text = "${request.hostname} requests to switch network",
                    fontSize = 12.sp,
                    color = ElysiumTextTertiary,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Network Transition Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ElysiumCardAlt)
                        .border(1.dp, ElysiumCardBorder, RoundedCornerShape(14.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Current Network", fontSize = 12.sp, color = ElysiumTextTertiary)
                            Text(currentNetwork.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = ElysiumTextPrimary)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Target Network", fontSize = 12.sp, color = ElysiumTextTertiary)
                            Text(
                                text = request.targetNetwork?.name ?: "Chain ID ${request.targetChainId}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElysiumMint
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Target Chain ID", fontSize = 12.sp, color = ElysiumTextTertiary)
                            Text(request.targetChainId.toString(), fontSize = 12.sp, color = ElysiumTextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("dapp_switch_chain_reject_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ElysiumTextSecondary)
                    ) {
                        Text("Reject", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("dapp_switch_chain_confirm_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElysiumMint,
                            contentColor = Color(0xFF111317)
                        )
                    ) {
                        Text("Switch Network", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
