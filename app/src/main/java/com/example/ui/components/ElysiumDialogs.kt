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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Chain
import com.example.model.TokenAsset
import com.example.model.WalletAccount
import com.example.ui.theme.ElysiumCardAlt
import com.example.ui.theme.ElysiumCardBg
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumPillActive
import com.example.ui.theme.ElysiumTextMuted
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary
import com.example.ui.theme.ElysiumTextTertiary

@Composable
fun WalletSelectorDialog(
    wallets: List<WalletAccount>,
    selectedWalletId: String,
    onSelectWallet: (WalletAccount) -> Unit,
    onCreateNewWallet: () -> Unit = {},
    onRestoreWallet: () -> Unit = {},
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ElysiumCardBg)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
                .testTag("wallet_selector_dialog")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Switch Wallet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElysiumTextPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ElysiumTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(wallets) { wallet ->
                        val isSelected = wallet.id == selectedWalletId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) ElysiumPillActive else ElysiumCardAlt)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ElysiumMint.copy(alpha = 0.5f) else ElysiumCardBorder,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    onSelectWallet(wallet)
                                    onDismiss()
                                }
                                .padding(14.dp)
                                .testTag("wallet_option_${wallet.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = wallet.name,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ElysiumTextPrimary
                                    )
                                    Text(
                                        text = wallet.shortAddress,
                                        fontSize = 12.sp,
                                        color = ElysiumTextSecondary,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = String.format("$%,.2f", wallet.totalBalanceUsd),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = ElysiumTextPrimary
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = ElysiumMint,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElysiumCardAlt)
                            .border(1.dp, ElysiumMint.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                            .clickable {
                                onDismiss()
                                onCreateNewWallet()
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+ Create Wallet",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElysiumMint
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElysiumCardAlt)
                            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(14.dp))
                            .clickable {
                                onDismiss()
                                onRestoreWallet()
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Import Phrase",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElysiumTextPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TokenSelectorDialog(
    tokens: List<TokenAsset>,
    selectedTokenId: String,
    onSelectToken: (TokenAsset) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ElysiumCardBg)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
                .testTag("token_selector_dialog")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Token",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElysiumTextPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ElysiumTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(tokens) { token ->
                        val isSelected = token.id == selectedTokenId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) ElysiumPillActive else ElysiumCardAlt)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ElysiumMint.copy(alpha = 0.5f) else ElysiumCardBorder,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    onSelectToken(token)
                                    onDismiss()
                                }
                                .padding(12.dp)
                                .testTag("token_option_${token.symbol.lowercase()}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    TokenLogo(iconType = token.iconType, size = 36.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = token.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ElysiumTextPrimary
                                        )
                                        Text(
                                            text = token.symbol,
                                            fontSize = 12.sp,
                                            color = ElysiumTextSecondary
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = token.balanceDisplay,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ElysiumTextPrimary
                                    )
                                    Text(
                                        text = token.fiatDisplay,
                                        fontSize = 11.sp,
                                        color = ElysiumTextTertiary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChainSelectorDialog(
    chains: List<Chain>,
    selectedChainId: String,
    onSelectChain: (Chain) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(ElysiumCardBg)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
                .testTag("chain_selector_dialog")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Network",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ElysiumTextPrimary
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ElysiumTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(chains) { chain ->
                        val isSelected = chain.id == selectedChainId
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) ElysiumPillActive else ElysiumCardAlt)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) ElysiumMint.copy(alpha = 0.5f) else ElysiumCardBorder,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable {
                                    onSelectChain(chain)
                                    onDismiss()
                                }
                                .padding(14.dp)
                                .testTag("chain_option_${chain.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ChainLogo(iconType = chain.iconType, size = 26.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = chain.name,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ElysiumTextPrimary
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = ElysiumMint,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
