package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.ElysiumAmber
import com.example.ui.theme.ElysiumAmberBg
import com.example.ui.theme.ElysiumAmberBorder
import com.example.ui.theme.ElysiumCardAlt
import com.example.ui.theme.ElysiumCardBg
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumGreenPositive
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumPillBg
import com.example.ui.theme.ElysiumTealDarkest
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary

@Composable
fun SecureSeedViewerDialog(
    walletName: String,
    mnemonic: String,
    isBackupVerified: Boolean,
    onDismiss: () -> Unit,
    onMarkVerified: () -> Unit
) {
    val words = mnemonic.split(" ").filter { it.isNotBlank() }

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
                .testTag("secure_seed_viewer_dialog")
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
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
                                .background(ElysiumAmberBg)
                                .border(1.dp, ElysiumAmberBorder, RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = ElysiumAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Vault Recovery Phrase",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElysiumTextPrimary
                            )
                            Text(
                                text = walletName,
                                fontSize = 12.sp,
                                color = ElysiumTextSecondary
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = ElysiumTextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(ElysiumAmberBg)
                        .border(1.dp, ElysiumAmberBorder, RoundedCornerShape(14.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Do not take screenshots. Anyone with these 12 words can access and transfer all your funds on EVM chains.",
                        fontSize = 11.sp,
                        color = ElysiumAmber,
                        lineHeight = 15.sp
                    )
                }

                // 12 Words Grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(ElysiumCardAlt)
                        .border(1.dp, ElysiumCardBorder, RoundedCornerShape(18.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val rows = (words.size + 2) / 3
                    for (row in 0 until rows) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (col in 0 until 3) {
                                val index = row * 3 + col
                                if (index < words.size) {
                                    val word = words[index]
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(ElysiumPillBg)
                                            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(10.dp))
                                            .padding(vertical = 8.dp, horizontal = 4.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "${index + 1}. $word",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ElysiumTextPrimary
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                if (!isBackupVerified) {
                    Button(
                        onClick = {
                            onMarkVerified()
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
                            .testTag("mark_backup_verified_button")
                    ) {
                        Text("Confirm I Have Saved My Phrase", fontWeight = FontWeight.SemiBold)
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = ElysiumGreenPositive,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Backup status: Verified Safe",
                            fontSize = 12.sp,
                            color = ElysiumGreenPositive,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
