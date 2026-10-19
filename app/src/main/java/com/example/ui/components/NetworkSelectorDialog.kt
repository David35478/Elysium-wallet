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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.EvmNetworkConfig
import com.example.ui.theme.ElysiumCardAlt
import com.example.ui.theme.ElysiumCardBg
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumPillActive
import com.example.ui.theme.ElysiumPrimaryFixed
import com.example.ui.theme.ElysiumSecondaryFixedDim
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary
import com.example.ui.theme.ElysiumTextTertiary

@Composable
fun NetworkSelectorDialog(
    networks: List<EvmNetworkConfig>,
    selectedNetworkId: String,
    onSelectNetwork: (EvmNetworkConfig) -> Unit,
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
                .testTag("network_selector_dialog")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Select Network",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElysiumTextPrimary
                        )
                        Text(
                            text = "One wallet • Multiple EVM networks",
                            fontSize = 12.sp,
                            color = ElysiumTextTertiary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
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
                    items(networks) { network ->
                        val isSelected = network.id.equals(selectedNetworkId, ignoreCase = true)
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
                                    onSelectNetwork(network)
                                    onDismiss()
                                }
                                .padding(14.dp)
                                .testTag("network_option_${network.id}")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    ChainLogo(iconType = network.iconType, size = 30.dp)
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = network.name,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = ElysiumTextPrimary
                                            )
                                            if (network.isTestnet) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(6.dp))
                                                        .background(Color(0x268DF5DC))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "TESTNET",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = ElysiumPrimaryFixed,
                                                        letterSpacing = 0.5.sp
                                                    )
                                                }
                                            }
                                        }

                                        Text(
                                            text = "Chain ID: ${network.chainId} (${network.chainIdHex}) • ${network.nativeCurrencySymbol}",
                                            fontSize = 11.sp,
                                            color = ElysiumSecondaryFixedDim,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected Network",
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
