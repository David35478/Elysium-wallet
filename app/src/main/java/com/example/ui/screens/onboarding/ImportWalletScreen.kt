package com.example.ui.screens.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.ViewTimeline
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.crypto.CryptoManager
import com.example.ui.theme.ElysiumBgDark
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumGreenPositive
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
import com.example.ui.theme.ElysiumTealDarkest
import com.example.ui.theme.ElysiumTextMuted
import com.example.ui.theme.ElysiumTextTertiary

@Composable
fun ImportWalletScreen(
    onBackClick: () -> Unit,
    onImportSuccess: (walletName: String, mnemonic: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var phraseInput by remember { mutableStateOf("") }
    var targetLength by remember { mutableIntStateOf(12) }
    var isHidden by remember { mutableStateOf(false) }
    var selectedProtocol by remember { mutableStateOf("recovery") } // recovery or privkey
    val clipboardManager = LocalClipboardManager.current

    val words = remember(phraseInput) {
        val trimmed = phraseInput.trim()
        if (trimmed.isEmpty()) emptyList() else trimmed.split("\\s+".toRegex()).filter { it.isNotEmpty() }
    }
    val wordCount = words.size

    val isValid = remember(words, targetLength) {
        if (words.size != targetLength) false
        else CryptoManager.validateMnemonic(phraseInput)
    }

    val progressPct = remember(wordCount, targetLength) {
        (wordCount.toFloat() / targetLength.toFloat()).coerceIn(0f, 1f)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElysiumBgDark)
            .statusBarsPadding()
            .testTag("onboarding_import_wallet_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(ElysiumSurfaceContainerHigh)
                        .testTag("import_wallet_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ElysiumOnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "Import Wallet",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.size(38.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Visual Header Branding Graphic
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(ElysiumSurfaceContainerLow)
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(ElysiumSurfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = ElysiumPrimaryFixed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Restore Access",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Elysium Non-Custodial Key Derivation",
                            fontSize = 11.sp,
                            color = ElysiumOnSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElysiumSecondaryContainer.copy(alpha = 0.4f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(ElysiumPrimaryFixed)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Enclave Ready",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElysiumSecondaryFixed
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Segmented Protocol Selector (Recovery Phrase vs Private Key)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(ElysiumSurfaceContainerLowest)
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedProtocol == "recovery") ElysiumSecondaryContainer else Color.Transparent)
                            .clickable { selectedProtocol = "recovery" },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ViewTimeline,
                                contentDescription = null,
                                tint = if (selectedProtocol == "recovery") ElysiumPrimaryFixed else ElysiumOnSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Recovery Phrase",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedProtocol == "recovery") ElysiumPrimaryFixed else ElysiumOnSurfaceVariant
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedProtocol == "privkey") ElysiumSecondaryContainer else Color.Transparent)
                            .clickable { selectedProtocol = "privkey" },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VpnKey,
                                contentDescription = null,
                                tint = if (selectedProtocol == "privkey") ElysiumPrimaryFixed else ElysiumOnSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Private Key",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (selectedProtocol == "privkey") ElysiumPrimaryFixed else ElysiumOnSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Word Count Selector (12 Words vs 24 Words)
            if (selectedProtocol == "recovery") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Secret Entropy",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ElysiumSurfaceContainerHigh)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "BIP-39",
                                fontSize = 10.sp,
                                color = ElysiumOnSurfaceVariant
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElysiumSurfaceContainerLowest)
                            .padding(2.dp)
                    ) {
                        Row {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (targetLength == 12) ElysiumSurfaceContainerHigh else Color.Transparent)
                                    .clickable { targetLength = 12 }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "12 Words",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (targetLength == 12) ElysiumPrimaryFixed else ElysiumOnSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (targetLength == 24) ElysiumSurfaceContainerHigh else Color.Transparent)
                                    .clickable { targetLength = 24 }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "24 Words",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (targetLength == 24) ElysiumPrimaryFixed else ElysiumOnSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Input Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(ElysiumSurfaceContainerLow)
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(18.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WORDS REGISTERED",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElysiumOnSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(ElysiumSecondaryContainer.copy(alpha = 0.4f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$wordCount of $targetLength",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElysiumPrimaryFixed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isHidden) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ElysiumSurfaceContainerLowest),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "•••• •••• •••• •••• ••••",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = ElysiumOnSurfaceVariant,
                                letterSpacing = 4.sp
                            )
                        }
                    } else {
                        OutlinedTextField(
                            value = phraseInput,
                            onValueChange = { phraseInput = it },
                            placeholder = {
                                Text(
                                    text = if (selectedProtocol == "recovery")
                                        "Enter your secret recovery phrase, with words separated by spaces..."
                                    else
                                        "Paste your raw 64-character hexadecimal private key...",
                                    fontSize = 13.sp,
                                    color = ElysiumTextMuted
                                )
                            },
                            minLines = 4,
                            maxLines = 5,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = ElysiumPrimaryFixed,
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = ElysiumSurfaceContainerLowest,
                                unfocusedContainerColor = ElysiumSurfaceContainerLowest
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("import_phrase_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sub-Textarea Utilities (Toggle Mask & Paste Phrase)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(ElysiumSurfaceContainerHigh)
                                .clickable { isHidden = !isHidden }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = ElysiumOnSurfaceVariant,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isHidden) "Show Words" else "Hide Words",
                                    fontSize = 11.sp,
                                    color = ElysiumOnSurfaceVariant
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(ElysiumSecondaryContainer)
                                .clickable {
                                    val clip = clipboardManager.getText()?.text
                                    if (!clip.isNullOrBlank()) {
                                        phraseInput = clip.trim()
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("import_paste_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = null,
                                    tint = ElysiumPrimaryFixed,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Paste Phrase",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ElysiumPrimaryFixed
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic Progress Bar & Validation Hint
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ElysiumSurfaceContainerLowest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progressPct)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (isValid) ElysiumGreenPositive else ElysiumPrimaryFixed)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = if (isValid) ElysiumGreenPositive else ElysiumSecondaryFixedDim,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = when {
                        isValid -> "Mnemonic phrase structure valid. Cryptographic key ready to derive."
                        wordCount > targetLength -> "Phrase contains ${wordCount - targetLength} extra word(s)."
                        wordCount > 0 -> "Type ${targetLength - wordCount} more word(s) in sequence."
                        else -> "Type your mnemonic in order. Autocomplete verifies locally on-chip."
                    },
                    fontSize = 11.sp,
                    color = if (isValid) ElysiumGreenPositive else ElysiumOnSurfaceVariant,
                    fontWeight = if (isValid) FontWeight.SemiBold else FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Fill Chips Carousel
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "QUICK FILL:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElysiumTextTertiary
                )

                listOf("glance", "matrix", "harbor", "zenith", "vault", "orbit", "crystal").forEach { quickWord ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(ElysiumSurfaceContainerHigh)
                            .clickable {
                                phraseInput = if (phraseInput.isBlank()) quickWord else "$phraseInput $quickWord"
                            }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = quickWord,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Security Protocol Assurance Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ElysiumSurfaceContainerLow)
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(ElysiumSecondaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = ElysiumPrimaryFixed,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Zero-Telemetry Vault Protection",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Elysium never transmits or stores seed phrases on external relays. All keys are derived in hardware via your device's Secure Enclave.",
                            fontSize = 12.sp,
                            color = ElysiumOnSurfaceVariant,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Primary Action CTA
            val ctaBg by animateColorAsState(
                targetValue = if (isValid) ElysiumPrimaryFixed else ElysiumSurfaceContainerHighest,
                label = "cta_bg"
            )
            val ctaText by animateColorAsState(
                targetValue = if (isValid) ElysiumTealDarkest else ElysiumTextMuted,
                label = "cta_text"
            )

            Button(
                onClick = {
                    if (isValid) {
                        onImportSuccess("Restored Wallet", phraseInput.trim())
                    }
                },
                enabled = isValid,
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ctaBg,
                    contentColor = ctaText,
                    disabledContainerColor = ElysiumSurfaceContainerHighest,
                    disabledContentColor = ElysiumTextMuted
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("import_wallet_submit_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DownloadDone,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Import Wallet",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
