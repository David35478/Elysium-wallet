package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.fragment.app.FragmentActivity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Screen
import com.example.ui.theme.ElysiumAmber
import com.example.ui.theme.ElysiumCanvas
import com.example.ui.theme.ElysiumCardAlt
import com.example.ui.theme.ElysiumCardBg
import com.example.ui.theme.ElysiumCardBorder
import com.example.ui.theme.ElysiumMint
import com.example.ui.theme.ElysiumTealDarkest
import com.example.ui.theme.ElysiumTextMuted
import com.example.ui.theme.ElysiumTextPrimary
import com.example.ui.theme.ElysiumTextSecondary
import com.example.ui.theme.ElysiumTextTertiary
import com.example.viewmodel.WalletUiState
import com.example.viewmodel.WalletViewModel

@Composable
fun SettingsScreen(
    uiState: WalletUiState,
    viewModel: WalletViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateTo(Screen.HOME)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(ElysiumCanvas)
            .statusBarsPadding()
            .testTag("settings_screen")
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp, bottom = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HOME) },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ElysiumCardAlt)
                            .border(1.dp, ElysiumCardBorder, CircleShape)
                            .testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ElysiumTextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Text(
                        text = "Settings",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElysiumTextPrimary
                    )
                }
            }

            // Active Wallet Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(ElysiumCardBg)
                        .border(1.dp, ElysiumCardBorder, RoundedCornerShape(24.dp))
                        .padding(18.dp)
                        .testTag("settings_active_wallet_card")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Active Wallet",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = ElysiumTextMuted
                                )
                                Text(
                                    text = uiState.selectedWallet.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElysiumTextPrimary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x1A97FFE5))
                                    .border(1.dp, Color(0x3397FFE5), RoundedCornerShape(14.dp))
                                    .clickable { viewModel.setShowWalletSelector(true) }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .testTag("settings_switch_wallet_button")
                            ) {
                                Text(
                                    text = "Switch",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ElysiumMint
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElysiumCardAlt)
                                .clickable { viewModel.copyAddressToClipboard() }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("settings_copy_full_address_button"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = uiState.selectedWallet.shortAddress,
                                fontSize = 13.sp,
                                color = ElysiumTextSecondary
                            )
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Copy Address",
                                tint = ElysiumTextTertiary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Section: Security & Privacy
            item {
                SettingsSectionHeader(title = "Security & Privacy")
            }

            item {
                SettingsGroup {
                    val activity = LocalContext.current as? FragmentActivity
                    SettingsRowItem(
                        icon = Icons.Outlined.Security,
                        title = "Seed Phrase Backup",
                        subtitle = if (uiState.isBackupVerified) "Verified Safe" else "Not backed up yet",
                        trailingContent = {
                            if (uiState.isBackupVerified) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.CheckCircle,
                                        contentDescription = null,
                                        tint = ElysiumMint,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Safe", fontSize = 12.sp, color = ElysiumMint)
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Outlined.Warning,
                                        contentDescription = null,
                                        tint = ElysiumAmber,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Action Required", fontSize = 12.sp, color = ElysiumAmber)
                                }
                            }
                        },
                        onClick = {
                            activity?.let { viewModel.requestViewSeedPhrase(it) }
                        },
                        testTag = "settings_backup_seed_row"
                    )

                    SettingsRowItem(
                        icon = Icons.Outlined.Fingerprint,
                        title = "Biometric Lock",
                        subtitle = "Require fingerprint / face to open app",
                        trailingContent = {
                            Switch(
                                checked = uiState.biometricLockEnabled,
                                onCheckedChange = { viewModel.toggleBiometrics() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ElysiumTealDarkest,
                                    checkedTrackColor = ElysiumMint,
                                    uncheckedThumbColor = ElysiumTextSecondary,
                                    uncheckedTrackColor = ElysiumCardAlt
                                ),
                                modifier = Modifier.testTag("biometric_switch")
                            )
                        },
                        onClick = { viewModel.toggleBiometrics() },
                        testTag = "settings_biometrics_row"
                    )

                    SettingsRowItem(
                        icon = Icons.Outlined.Lock,
                        title = "Auto-Lock Timer",
                        subtitle = "Immediately upon backgrounding",
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = ElysiumTextTertiary,
                                modifier = Modifier.size(12.dp)
                            )
                        },
                        onClick = { viewModel.triggerActionNotImplemented("Auto-lock configuration") },
                        testTag = "settings_auto_lock_row"
                    )
                }
            }

            // Section: Network & Display
            item {
                SettingsSectionHeader(title = "Network & Display")
            }

            item {
                SettingsGroup {
                    SettingsRowItem(
                        icon = Icons.Outlined.Public,
                        title = "Default Network",
                        subtitle = uiState.sourceChain.name,
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = ElysiumTextTertiary,
                                modifier = Modifier.size(12.dp)
                            )
                        },
                        onClick = { viewModel.setShowSourceChainSelector(true) },
                        testTag = "settings_default_network_row"
                    )

                    SettingsRowItem(
                        icon = Icons.Outlined.Tune,
                        title = "Show Testnets",
                        subtitle = "Sepolia, BSC Testnet, HL Testnet",
                        trailingContent = {
                            Switch(
                                checked = uiState.testnetsEnabled,
                                onCheckedChange = { viewModel.toggleTestnets() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ElysiumTealDarkest,
                                    checkedTrackColor = ElysiumMint,
                                    uncheckedThumbColor = ElysiumTextSecondary,
                                    uncheckedTrackColor = ElysiumCardAlt
                                ),
                                modifier = Modifier.testTag("testnets_switch")
                            )
                        },
                        onClick = { viewModel.toggleTestnets() },
                        testTag = "settings_testnets_row"
                    )

                    SettingsRowItem(
                        icon = Icons.Outlined.VisibilityOff,
                        title = "Hide Small Balances",
                        subtitle = "Filter assets with value under $1.00",
                        trailingContent = {
                            Switch(
                                checked = uiState.hideSmallBalances,
                                onCheckedChange = { viewModel.toggleHideSmallBalances() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ElysiumTealDarkest,
                                    checkedTrackColor = ElysiumMint,
                                    uncheckedThumbColor = ElysiumTextSecondary,
                                    uncheckedTrackColor = ElysiumCardAlt
                                ),
                                modifier = Modifier.testTag("hide_small_balances_switch")
                            )
                        },
                        onClick = { viewModel.toggleHideSmallBalances() },
                        testTag = "settings_hide_small_balances_row"
                    )

                    SettingsRowItem(
                        icon = Icons.Outlined.Language,
                        title = "Base Currency",
                        subtitle = "USD ($)",
                        trailingContent = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = ElysiumTextTertiary,
                                modifier = Modifier.size(12.dp)
                            )
                        },
                        onClick = { viewModel.triggerActionNotImplemented("Currency selection") },
                        testTag = "settings_currency_row"
                    )
                }
            }

            // Section: About
            item {
                SettingsSectionHeader(title = "About")
            }

            item {
                SettingsGroup {
                    SettingsRowItem(
                        icon = Icons.Outlined.Info,
                        title = "Elysium Wallet",
                        subtitle = "Version 1.0.0 (Native Android Build)",
                        trailingContent = {
                            Text(
                                text = "v1.0.0",
                                fontSize = 12.sp,
                                color = ElysiumMint
                            )
                        },
                        onClick = { viewModel.triggerActionNotImplemented("Version info") },
                        testTag = "settings_version_row"
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = ElysiumTextMuted,
        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
    )
}

@Composable
private fun SettingsGroup(
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(ElysiumCardBg)
            .border(1.dp, ElysiumCardBorder, RoundedCornerShape(22.dp))
            .padding(vertical = 4.dp)
    ) {
        content()
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailingContent: @Composable () -> Unit,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ElysiumCardAlt)
                    .border(1.dp, ElysiumCardBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ElysiumMint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ElysiumTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = ElysiumTextSecondary,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
        }

        trailingContent()
    }
}
