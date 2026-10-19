package com.example.ui.screens.onboarding

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ElysiumBgDark
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumGreenPositive
import com.example.ui.theme.ElysiumOnSurface
import com.example.ui.theme.ElysiumOnSurfaceVariant
import com.example.ui.theme.ElysiumPrimaryFixed
import com.example.ui.theme.ElysiumPrimaryFixedDim
import com.example.ui.theme.ElysiumSecondaryContainer
import com.example.ui.theme.ElysiumSecondaryFixed
import com.example.ui.theme.ElysiumSecondaryFixedDim
import com.example.ui.theme.ElysiumSurfaceContainer
import com.example.ui.theme.ElysiumSurfaceContainerHigh
import com.example.ui.theme.ElysiumSurfaceContainerHighest
import com.example.ui.theme.ElysiumSurfaceContainerLow
import com.example.ui.theme.ElysiumTealDarkest
import com.example.ui.theme.ElysiumTextMuted
import com.example.ui.theme.ElysiumTextTertiary

@Composable
fun VerifyPhraseScreen(
    mnemonic: String,
    onBackClick: () -> Unit,
    onVerificationSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val words = remember(mnemonic) { mnemonic.split(" ").filter { it.isNotBlank() } }

    // Exactly match the reference slots: #4, #7, #11 (1-based)
    val targetSlots = remember { listOf(4, 7, 11) }
    var currentStepIndex by remember { mutableIntStateOf(0) }
    val filledSlots = remember { mutableStateMapOf<Int, String>() }
    var feedbackMessage by remember { mutableStateOf("Tap target word for position #4") }
    var isFeedbackError by remember { mutableStateOf(false) }

    // Scrambled word pool
    val scrambledPool = remember(words) { words.shuffled() }

    val isAllComplete = currentStepIndex >= targetSlots.size

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElysiumBgDark)
            .statusBarsPadding()
            .testTag("onboarding_verify_phrase_screen")
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
                        .testTag("verify_phrase_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ElysiumOnSurface,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Text(
                    text = "Verify Recovery Phrase",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.size(38.dp))
            }

            // Progress & Micro-Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElysiumSurfaceContainerHigh)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ElysiumPrimaryFixedDim)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "STEP 3 OF 3",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = ElysiumOnSurfaceVariant,
                            letterSpacing = 0.5.sp
                        )
                    }
                }

                // 3 Progress Segments (all 3 active)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(width = 24.dp, height = 4.dp).clip(RoundedCornerShape(2.dp)).background(ElysiumPrimaryFixed))
                    Box(modifier = Modifier.size(width = 24.dp, height = 4.dp).clip(RoundedCornerShape(2.dp)).background(ElysiumPrimaryFixed))
                    Box(modifier = Modifier.size(width = 24.dp, height = 4.dp).clip(RoundedCornerShape(2.dp)).background(ElysiumPrimaryFixed))
                }
            }

            Text(
                text = "Verify Your Backup",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = "Tap the words in the correct order to confirm you wrote them down safely.",
                fontSize = 14.sp,
                color = ElysiumOnSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Verification Chamber Container (12-Slot Seed Verification Grid)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(ElysiumSurfaceContainerLow)
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(20.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = ElysiumPrimaryFixedDim,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Key Derivation Grid",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElysiumOnSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isAllComplete) ElysiumSecondaryContainer else ElysiumSurfaceContainer)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isAllComplete) "Verified 12/12" else "Awaiting #${targetSlots.getOrNull(currentStepIndex) ?: 12}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isAllComplete) ElysiumPrimaryFixed else ElysiumOnSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 12-Slot Grid (4 rows x 3 cols)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        for (row in 0 until 4) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                for (col in 0 until 3) {
                                    val slotNumber = row * 3 + col + 1
                                    val isTarget = targetSlots.contains(slotNumber)
                                    val isCurrentTarget = !isAllComplete && targetSlots.getOrNull(currentStepIndex) == slotNumber
                                    val isTargetAnswered = filledSlots.containsKey(slotNumber)

                                    val wordText = when {
                                        !isTarget -> if (slotNumber - 1 < words.size) words[slotNumber - 1] else ""
                                        isTargetAnswered -> filledSlots[slotNumber] ?: ""
                                        isCurrentTarget -> "Select word"
                                        else -> "---"
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when {
                                                    isCurrentTarget -> ElysiumSecondaryContainer.copy(alpha = 0.5f)
                                                    isTargetAnswered || !isTarget -> ElysiumSurfaceContainerHigh
                                                    else -> ElysiumSurfaceContainer
                                                }
                                            )
                                            .border(
                                                1.dp,
                                                if (isCurrentTarget) ElysiumPrimaryFixed.copy(alpha = 0.4f) else Color.Transparent,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(horizontal = 8.dp, vertical = 7.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = String.format("%02d", slotNumber),
                                                fontSize = 10.sp,
                                                color = if (isCurrentTarget) ElysiumPrimaryFixed else ElysiumTextTertiary
                                            )
                                            Text(
                                                text = wordText,
                                                fontSize = 11.sp,
                                                fontWeight = if (isCurrentTarget) FontWeight.Bold else FontWeight.SemiBold,
                                                color = when {
                                                    isCurrentTarget -> ElysiumPrimaryFixed
                                                    isTargetAnswered || !isTarget -> ElysiumOnSurface
                                                    else -> ElysiumTextTertiary
                                                },
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Icon(
                                                imageVector = when {
                                                    isTargetAnswered || !isTarget -> Icons.Default.CheckCircle
                                                    isCurrentTarget -> Icons.Default.HourglassTop
                                                    else -> Icons.Default.Lock
                                                },
                                                contentDescription = null,
                                                tint = when {
                                                    isTargetAnswered || !isTarget -> ElysiumPrimaryFixedDim
                                                    isCurrentTarget -> ElysiumPrimaryFixed
                                                    else -> ElysiumTextTertiary
                                                },
                                                modifier = Modifier.size(11.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Real-time Live Security Feedback Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isFeedbackError) Color(0x33EF4444) else ElysiumSurfaceContainer)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isAllComplete) Icons.Default.CheckCircle else Icons.Default.Security,
                                    contentDescription = null,
                                    tint = if (isAllComplete) ElysiumGreenPositive else if (isFeedbackError) Color(0xFFEF4444) else ElysiumSecondaryFixed,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = feedbackMessage,
                                    fontSize = 12.sp,
                                    fontWeight = if (isAllComplete) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (isAllComplete) ElysiumGreenPositive else if (isFeedbackError) Color(0xFFEF4444) else ElysiumOnSurfaceVariant
                                )
                            }

                            if (currentStepIndex > 0) {
                                Text(
                                    text = "Reset",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ElysiumPrimaryFixed,
                                    modifier = Modifier
                                        .clickable {
                                            currentStepIndex = 0
                                            filledSlots.clear()
                                            feedbackMessage = "Tap target word for position #4"
                                            isFeedbackError = false
                                        }
                                        .padding(4.dp)
                                        .testTag("verify_reset_btn")
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Word Bank Pool (3 columns)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WORD BANK POOL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElysiumSecondaryFixed,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Scrambled seed entropy",
                    fontSize = 11.sp,
                    color = ElysiumSecondaryFixedDim
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 12 Scrambled Chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                for (row in 0 until 4) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (col in 0 until 3) {
                            val idx = row * 3 + col
                            val chipWord = scrambledPool[idx]
                            val isUsed = filledSlots.values.contains(chipWord)

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(
                                        if (isUsed) ElysiumSurfaceContainer.copy(alpha = 0.4f)
                                        else ElysiumSurfaceContainerHigh
                                    )
                                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(22.dp))
                                    .clickable(enabled = !isUsed && !isAllComplete) {
                                        val currentTargetSlot = targetSlots[currentStepIndex]
                                        val expectedWord = words[currentTargetSlot - 1]

                                        if (chipWord == expectedWord) {
                                            filledSlots[currentTargetSlot] = chipWord
                                            currentStepIndex++
                                            isFeedbackError = false

                                            if (currentStepIndex < targetSlots.size) {
                                                val nextSlot = targetSlots[currentStepIndex]
                                                feedbackMessage = "Verified #$currentTargetSlot. Now tap word for #$nextSlot"
                                            } else {
                                                feedbackMessage = "Word sequence matches securely!"
                                            }
                                        } else {
                                            isFeedbackError = true
                                            feedbackMessage = "\"$chipWord\" does not match position #$currentTargetSlot"
                                        }
                                    }
                                    .testTag("word_chip_$chipWord"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = chipWord,
                                        fontSize = 13.sp,
                                        fontWeight = if (isUsed) FontWeight.Normal else FontWeight.SemiBold,
                                        color = if (isUsed) ElysiumTextMuted else ElysiumOnSurface
                                    )
                                    if (isUsed) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = ElysiumPrimaryFixedDim,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Security Hardware Isolation Visual Badge
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ElysiumSurfaceContainerHigh),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Memory,
                            contentDescription = null,
                            tint = ElysiumPrimaryFixed,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Secure Enclave Ready",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Master root key derivation validated in sandboxed memory.",
                            fontSize = 12.sp,
                            color = ElysiumOnSurfaceVariant,
                            lineHeight = 16.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Action CTA
            val ctaBg by animateColorAsState(
                targetValue = if (isAllComplete) ElysiumPrimaryFixed else ElysiumSurfaceContainerHighest,
                label = "cta_bg"
            )
            val ctaText by animateColorAsState(
                targetValue = if (isAllComplete) ElysiumTealDarkest else ElysiumOnSurfaceVariant.copy(alpha = 0.6f),
                label = "cta_text"
            )

            Button(
                onClick = onVerificationSuccess,
                enabled = isAllComplete,
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ctaBg,
                    contentColor = ctaText,
                    disabledContainerColor = ElysiumSurfaceContainerHighest,
                    disabledContentColor = ElysiumOnSurfaceVariant.copy(alpha = 0.6f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("confirm_and_finalize_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isAllComplete) Icons.Default.CheckCircle else Icons.Default.LockOpen,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirm & Finalize",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Never share your recovery phrase with anyone.",
                fontSize = 12.sp,
                color = ElysiumOnSurfaceVariant,
                modifier = Modifier
                    .padding(top = 8.dp, bottom = 24.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}
