package com.example.ui.screens.walletsetup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.core.crypto.CryptoManager
import com.example.ui.theme.ElysiumAmber
import com.example.ui.theme.ElysiumAmberBg
import com.example.ui.theme.ElysiumAmberBorder
import com.example.ui.theme.ElysiumCardAlt
import com.example.ui.theme.ElysiumCardBg
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumGreenPositive
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumPillActive
import com.example.ui.theme.ElysiumPillBg
import com.example.ui.theme.ElysiumTealDarkest
import com.example.ui.theme.ElysiumTextMuted
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary
import com.example.ui.theme.ElysiumTextTertiary

enum class CreateStep {
    WARNING,
    WORDS,
    VERIFY
}

@Composable
fun CreateWalletDialog(
    onDismiss: () -> Unit,
    onWalletCreated: (walletName: String, mnemonic: String) -> Unit
) {
    var step by remember { mutableStateOf(CreateStep.WARNING) }
    var walletName by remember { mutableStateOf("Wallet 1") }
    val mnemonic by remember { mutableStateOf(CryptoManager.generate12WordMnemonic()) }
    val words = remember(mnemonic) { mnemonic.split(" ") }

    // Generate 3 random distinct challenge indices (1-indexed: 1..12)
    val challengeIndices = remember(words) {
        (1..12).shuffled().take(3).sorted()
    }

    // Selected answers map: index (1-based) -> user selected word
    val selectedAnswers = remember { mutableStateMapOf<Int, String>() }

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
                .testTag("create_wallet_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with title and close
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
                                imageVector = Icons.Outlined.Security,
                                contentDescription = null,
                                tint = ElysiumMint,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = when (step) {
                                CreateStep.WARNING -> "Create Self-Custody Wallet"
                                CreateStep.WORDS -> "Secret Recovery Phrase"
                                CreateStep.VERIFY -> "Verify Backup Phrase"
                            },
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

                Spacer(modifier = Modifier.height(14.dp))

                AnimatedContent(
                    targetState = step,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "create_step_animation"
                ) { currentStep ->
                    when (currentStep) {
                        CreateStep.WARNING -> {
                            StepWarning(
                                walletName = walletName,
                                onWalletNameChange = { walletName = it },
                                onProceed = { step = CreateStep.WORDS }
                            )
                        }
                        CreateStep.WORDS -> {
                            StepWords(
                                words = words,
                                onProceedToVerify = { step = CreateStep.VERIFY }
                            )
                        }
                        CreateStep.VERIFY -> {
                            StepVerify(
                                words = words,
                                challengeIndices = challengeIndices,
                                selectedAnswers = selectedAnswers,
                                onSelectAnswer = { index, word ->
                                    selectedAnswers[index] = word
                                },
                                onComplete = {
                                    onWalletCreated(walletName, mnemonic)
                                    onDismiss()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepWarning(
    walletName: String,
    onWalletNameChange: (String) -> Unit,
    onProceed: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = walletName,
            onValueChange = onWalletNameChange,
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
                .testTag("create_wallet_name_input")
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(ElysiumAmberBg)
                .border(1.dp, ElysiumAmberBorder, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = ElysiumAmber,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Self-Custodial Security Notice",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElysiumAmber
                    )
                    Text(
                        text = "You are the sole custodian of your funds. In the next step, a 12-word recovery phrase will be generated. Elysium never transmits your keys to servers.",
                        fontSize = 12.sp,
                        color = ElysiumTextSecondary,
                        modifier = Modifier.padding(top = 4.dp),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(ElysiumCardAlt)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = ElysiumMint,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Hardware Keystore Encryption",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElysiumTextPrimary
                    )
                    Text(
                        text = "Your private keys will be derived locally (BIP-44 m/44'/60'/0'/0/0) and encrypted at rest with AES-256-GCM backed by Android Keystore.",
                        fontSize = 12.sp,
                        color = ElysiumTextSecondary,
                        modifier = Modifier.padding(top = 4.dp),
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Button(
            onClick = onProceed,
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElysiumMint,
                contentColor = ElysiumTealDarkest
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("proceed_to_words_button")
        ) {
            Text("Generate Recovery Phrase", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun StepWords(
    words: List<String>,
    onProceedToVerify: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Write down these 12 words in numerical order and keep them safe. You will be tested on 3 random words in the next step to confirm your backup.",
            fontSize = 13.sp,
            color = ElysiumTextSecondary,
            lineHeight = 18.sp
        )

        // 12 Words 3x4 Grid
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(ElysiumCardAlt)
                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(18.dp))
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
                        val word = words[index]
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ElysiumPillBg)
                                .border(1.dp, ElysiumCardBorder, RoundedCornerShape(10.dp))
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}. $word",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElysiumTextPrimary
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = onProceedToVerify,
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElysiumMint,
                contentColor = ElysiumTealDarkest
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("proceed_to_verify_button")
        ) {
            Text("I Wrote It Down • Verify", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun StepVerify(
    words: List<String>,
    challengeIndices: List<Int>, // 1-based indices (e.g. 2, 7, 11)
    selectedAnswers: Map<Int, String>,
    onSelectAnswer: (Int, String) -> Unit,
    onComplete: () -> Unit
) {
    val allCorrect = challengeIndices.all { idx ->
        val expected = words[idx - 1]
        selectedAnswers[idx] == expected
    }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text(
            text = "Select the correct word for each numbered position from your written backup:",
            fontSize = 13.sp,
            color = ElysiumTextSecondary
        )

        challengeIndices.forEach { targetIdx ->
            val correctWord = words[targetIdx - 1]
            // Pick 3 distractor words from other indices
            val options = remember(targetIdx) {
                val distractors = words.filter { it != correctWord }.shuffled().take(3)
                (distractors + correctWord).shuffled()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ElysiumCardAlt)
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Text(
                    text = "Select Word #$targetIdx",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElysiumMint
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    options.forEach { opt ->
                        val isSelected = selectedAnswers[targetIdx] == opt
                        val isAnswerCorrect = isSelected && opt == correctWord

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    when {
                                        isSelected && isAnswerCorrect -> ElysiumGreenPositive.copy(alpha = 0.2f)
                                        isSelected && !isAnswerCorrect -> Color(0x33EF4444)
                                        else -> ElysiumPillBg
                                    }
                                )
                                .border(
                                    1.dp,
                                    when {
                                        isSelected && isAnswerCorrect -> ElysiumGreenPositive
                                        isSelected && !isAnswerCorrect -> Color(0xFFEF4444)
                                        else -> ElysiumCardBorder
                                    },
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectAnswer(targetIdx, opt) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = opt,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = when {
                                    isSelected && isAnswerCorrect -> ElysiumGreenPositive
                                    isSelected && !isAnswerCorrect -> Color(0xFFEF4444)
                                    else -> ElysiumTextPrimary
                                }
                            )
                        }
                    }
                }
            }
        }

        if (allCorrect) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = ElysiumGreenPositive,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Verification successful! All 3 words matched.",
                    fontSize = 12.sp,
                    color = ElysiumGreenPositive,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Button(
            onClick = onComplete,
            enabled = allCorrect,
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
                .testTag("complete_wallet_creation_button")
        ) {
            Text(
                text = if (allCorrect) "Finalize & Encrypt Wallet" else "Verify All 3 Words",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
