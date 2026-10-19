package com.example.ui.screens.walletsetup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.crypto.CryptoManager
import com.example.ui.theme.ElysiumCardAlt
import com.example.ui.theme.ElysiumCardBg
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumGreenPositive
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumTealDarkest
import com.example.ui.theme.ElysiumTextMuted
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary

@Composable
fun RestoreWalletDialog(
    onDismiss: () -> Unit,
    onWalletRestored: (walletName: String, mnemonic: String) -> Unit
) {
    var walletName by remember { mutableStateOf("Restored Wallet") }
    var mnemonicInput by remember { mutableStateOf("") }

    val cleanedMnemonic by remember(mnemonicInput) {
        derivedStateOf { CryptoManager.cleanMnemonic(mnemonicInput) }
    }

    val wordCount by remember(cleanedMnemonic) {
        derivedStateOf {
            if (cleanedMnemonic.isEmpty()) 0 else cleanedMnemonic.split(" ").size
        }
    }

    val isValidMnemonic by remember(cleanedMnemonic) {
        derivedStateOf { CryptoManager.validateMnemonic(cleanedMnemonic) }
    }

    val derivedAddressPreview by remember(isValidMnemonic, cleanedMnemonic) {
        derivedStateOf {
            if (isValidMnemonic) {
                try {
                    CryptoManager.deriveEvmAccount(cleanedMnemonic).address
                } catch (_: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .background(ElysiumCardBg)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(26.dp))
                .padding(20.dp)
                .testTag("restore_wallet_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x2697FFE5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Download,
                                contentDescription = null,
                                tint = ElysiumMint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Restore From Phrase",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
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
                    text = "Enter your 12 or 24-word secret recovery phrase separated by spaces. Words are case-insensitive.",
                    fontSize = 13.sp,
                    color = ElysiumTextSecondary,
                    lineHeight = 18.sp
                )

                OutlinedTextField(
                    value = walletName,
                    onValueChange = { walletName = it },
                    label = { Text("Wallet Name", color = ElysiumTextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ElysiumTextPrimary,
                        unfocusedTextColor = ElysiumTextPrimary,
                        focusedBorderColor = ElysiumMint,
                        unfocusedBorderColor = ElysiumCardBorder,
                        focusedContainerColor = ElysiumCardAlt,
                        unfocusedContainerColor = ElysiumCardAlt
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("restore_wallet_name_input")
                )

                // Mnemonic Text Input Area
                Column {
                    OutlinedTextField(
                        value = mnemonicInput,
                        onValueChange = { mnemonicInput = it },
                        label = { Text("Recovery Phrase (12 or 24 words)", color = ElysiumTextSecondary) },
                        minLines = 4,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = ElysiumTextPrimary,
                            unfocusedTextColor = ElysiumTextPrimary,
                            focusedBorderColor = if (isValidMnemonic) ElysiumGreenPositive else ElysiumMint,
                            unfocusedBorderColor = ElysiumCardBorder,
                            focusedContainerColor = ElysiumCardAlt,
                            unfocusedContainerColor = ElysiumCardAlt
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("restore_mnemonic_input")
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$wordCount / 12 words",
                            fontSize = 12.sp,
                            color = if (wordCount == 12 || wordCount == 24) ElysiumMint else ElysiumTextMuted
                        )

                        if (wordCount > 0 && !isValidMnemonic && (wordCount == 12 || wordCount == 24)) {
                            Text(
                                text = "Invalid phrase checksum",
                                fontSize = 12.sp,
                                color = Color(0xFFEF4444)
                            )
                        } else if (isValidMnemonic) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    tint = ElysiumGreenPositive,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Valid BIP-39 phrase",
                                    fontSize = 12.sp,
                                    color = ElysiumGreenPositive,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Live Address Preview
                derivedAddressPreview?.let { address ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ElysiumCardAlt)
                            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
                            .padding(12.dp)
                            .testTag("derived_address_preview_box")
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Outlined.Key,
                                    contentDescription = null,
                                    tint = ElysiumMint,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Derived EVM Address (m/44'/60'/0'/0/0):",
                                    fontSize = 12.sp,
                                    color = ElysiumTextMuted
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = address,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElysiumTextPrimary
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        onWalletRestored(walletName.ifBlank { "Restored Wallet" }, cleanedMnemonic)
                        onDismiss()
                    },
                    enabled = isValidMnemonic,
                    shape = RoundedCornerShape(22.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElysiumMint,
                        contentColor = ElysiumTealDarkest,
                        disabledContainerColor = ElysiumCardAlt,
                        disabledContentColor = ElysiumTextMuted
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_restore_wallet_button")
                ) {
                    Text("Restore & Encrypt to Vault", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}
