package com.example.ui.screens.onboarding

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.ElysiumAmber
import com.example.ui.theme.ElysiumBgDark
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumOnSurface
import com.example.ui.theme.ElysiumOnSurfaceVariant
import com.example.ui.theme.ElysiumPillBg
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

@Composable
fun BackUpPhraseScreen(
    mnemonic: String,
    onBackClick: () -> Unit,
    onSavedClick: () -> Unit,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val words = remember(mnemonic) { mnemonic.split(" ").filter { it.isNotBlank() } }
    var isHidden by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElysiumBgDark)
            .statusBarsPadding()
            .testTag("onboarding_backup_phrase_screen")
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
                        .testTag("backup_phrase_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ElysiumOnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "Seed Phrase Generation",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.size(38.dp))
            }

            // Progress Tracker: Step 2 of 3
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(ElysiumPrimaryFixed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ENCLAVE DERIVATION ACTIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElysiumPrimaryFixed,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "Step 2 of 3",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElysiumSecondaryFixedDim
                )
            }

            // 3 Segmented Progress Bars (2 of 3 active)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(ElysiumPrimaryFixed)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(ElysiumPrimaryFixed)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(ElysiumSurfaceContainerHighest)
                )
            }

            // Security Caution Header
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(ElysiumSecondaryContainer.copy(alpha = 0.6f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = ElysiumSecondaryFixed,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "ZERO-KNOWLEDGE PROTOCOL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElysiumSecondaryFixed,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Back Up Your Wallet",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "Write down these 12 words in order and store them in a secure offline location. Never share them with anyone.",
                fontSize = 14.sp,
                color = ElysiumOnSurfaceVariant,
                lineHeight = 20.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Interactive Phrase Container (2 columns x 6 rows)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ElysiumSurfaceContainerLow)
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(20.dp))
                    .padding(14.dp)
            ) {
                if (isHidden) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(ElysiumSurfaceContainerHigh),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = ElysiumPrimaryFixed,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Text(
                            text = "Recovery Phrase Hidden",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 10.dp)
                        )

                        Text(
                            text = "Protect your enclave keys from prying eyes and screen recording.",
                            fontSize = 12.sp,
                            color = ElysiumOnSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        Button(
                            onClick = { isHidden = false },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElysiumSurfaceContainerHighest,
                                contentColor = ElysiumPrimaryFixed
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reveal Phrase", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        for (row in 0 until 6) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val idxLeft = row * 2
                                val idxRight = row * 2 + 1

                                WordPill(
                                    number = String.format("%02d", idxLeft + 1),
                                    word = if (idxLeft < words.size) words[idxLeft] else "",
                                    modifier = Modifier.weight(1f)
                                )

                                WordPill(
                                    number = String.format("%02d", idxRight + 1),
                                    word = if (idxRight < words.size) words[idxRight] else "",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Bar: Copy & Toggle Visibility
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(ElysiumSurfaceContainerHigh)
                        .clickable(onClick = onCopyClick)
                        .padding(horizontal = 16.dp)
                        .testTag("backup_copy_clipboard_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = null,
                            tint = ElysiumSecondaryFixed,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Copy to Clipboard",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ElysiumSecondaryFixed
                        )
                    }
                }

                IconButton(
                    onClick = { isHidden = !isHidden },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(ElysiumSurfaceContainerHigh)
                        .testTag("backup_toggle_visibility_btn")
                ) {
                    Icon(
                        imageVector = if (isHidden) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Visibility",
                        tint = ElysiumOnSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Recommended Protocol Card: Offline Metal Stash
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ElysiumSurfaceContainerLow)
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(ElysiumSurfaceContainerHighest),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = ElysiumPrimaryFixed,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "RECOMMENDED PROTOCOL",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElysiumPrimaryFixed,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Offline Physical Storage",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Never store your seed in cloud notes or unencrypted screenshots.",
                            fontSize = 12.sp,
                            color = ElysiumOnSurfaceVariant,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Permanent Asset Custody Warning Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ElysiumSecondaryContainer.copy(alpha = 0.4f))
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ElysiumPrimaryFixed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = ElysiumPrimaryFixed,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Permanent Asset Custody Warning",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Anyone who gets this phrase can permanently take your assets. Elysium support will never ask for it.",
                            fontSize = 12.sp,
                            color = ElysiumSecondaryFixedDim,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary CTA: I've Saved It Securely
            Button(
                onClick = onSavedClick,
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElysiumPrimaryFixed,
                    contentColor = ElysiumTealDarkest
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("backup_phrase_saved_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "I've Saved It Securely",
                        fontSize = 15.sp,
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

            Text(
                text = "Verification step follows immediately",
                fontSize = 12.sp,
                color = ElysiumOnSurfaceVariant,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun WordPill(
    number: String,
    word: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(ElysiumSurfaceContainer)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = number,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ElysiumPrimaryFixed.copy(alpha = 0.8f)
            )
            Text(
                text = word,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(ElysiumSurfaceContainerHighest)
            )
        }
    }
}
