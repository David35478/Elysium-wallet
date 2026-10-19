package com.example

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Screen
import com.example.ui.components.ChainSelectorDialog
import com.example.ui.components.ElysiumBottomBar
import com.example.ui.components.SecureSeedViewerDialog
import com.example.ui.components.SeedBackupDialog
import com.example.ui.components.SwapConfirmationDialog
import com.example.ui.components.TokenDetailDialog
import com.example.ui.components.TokenSelectorDialog
import com.example.ui.components.WalletSelectorDialog
import com.example.ui.screens.ActivityScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SwapScreen
import com.example.ui.screens.walletsetup.CreateWalletDialog
import com.example.ui.screens.walletsetup.RestoreWalletDialog
import com.example.ui.screens.onboarding.WelcomeScreen
import com.example.ui.screens.onboarding.LocalProtectionScreen
import com.example.ui.screens.onboarding.BackUpPhraseScreen
import com.example.ui.screens.onboarding.VerifyPhraseScreen
import com.example.ui.screens.onboarding.ImportWalletScreen
import com.example.ui.screens.onboarding.YourWalletIsReadyScreen
import com.example.ui.theme.ElysiumCanvas
import com.example.ui.theme.ElysiumTheme
import com.example.viewmodel.WalletViewModel

class MainActivity : FragmentActivity() {

    private val viewModel: WalletViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ElysiumTheme {
                ElysiumWalletApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun ElysiumWalletApp(
    viewModel: WalletViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Display user messages / notifications
    LaunchedEffect(uiState.userNotification) {
        uiState.userNotification?.let { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearNotification()
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .padding(bottom = if (uiState.currentScreen == Screen.SWAP) 80.dp else 90.dp)
                    .testTag("app_snackbar_host")
            )
        },
        containerColor = ElysiumCanvas,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(ElysiumCanvas)
        ) {
            // Main Screen Content with Transitions
            AnimatedContent(
                targetState = uiState.currentScreen,
                transitionSpec = {
                    if (targetState == Screen.SWAP || targetState == Screen.SETTINGS) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut()
                        )
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut()
                        )
                    }
                },
                label = "screen_navigation"
            ) { screen ->
                when (screen) {
                    Screen.WELCOME -> WelcomeScreen(
                        onAddNewWalletClick = { viewModel.startNewWalletCreation() },
                        onImportWalletClick = { viewModel.navigateTo(Screen.ONBOARDING_IMPORT) }
                    )
                    Screen.ONBOARDING_LOCAL_PROTECTION -> LocalProtectionScreen(
                        onBackClick = { viewModel.navigateTo(Screen.WELCOME) },
                        onCreateWalletClick = { viewModel.onLocalProtectionConfirmed() }
                    )
                    Screen.ONBOARDING_BACKUP_PHRASE -> BackUpPhraseScreen(
                        mnemonic = uiState.onboardingMnemonic ?: "",
                        onBackClick = { viewModel.navigateTo(Screen.ONBOARDING_LOCAL_PROTECTION) },
                        onSavedClick = { viewModel.onBackupPhraseSaved() },
                        onCopyClick = { viewModel.copyMnemonicToClipboard() }
                    )
                    Screen.ONBOARDING_VERIFY_PHRASE -> VerifyPhraseScreen(
                        mnemonic = uiState.onboardingMnemonic ?: "",
                        onBackClick = { viewModel.navigateTo(Screen.ONBOARDING_BACKUP_PHRASE) },
                        onVerificationSuccess = { viewModel.onVerificationSuccess() }
                    )
                    Screen.ONBOARDING_IMPORT -> ImportWalletScreen(
                        onBackClick = { viewModel.navigateTo(Screen.WELCOME) },
                        onImportSuccess = { name, mnemonic -> viewModel.onImportWalletSuccess(name, mnemonic) }
                    )
                    Screen.ONBOARDING_READY -> YourWalletIsReadyScreen(
                        walletAccount = uiState.selectedWallet,
                        isBackupVerified = uiState.isBackupVerified,
                        isBiometricEnabled = uiState.biometricLockEnabled,
                        onExploreWalletClick = { viewModel.onExploreWallet() },
                        onOpenSecuritySettingsClick = { viewModel.navigateTo(Screen.SETTINGS) },
                        onCopyAddress = { address -> viewModel.copyAddressToClipboard() }
                    )
                    Screen.HOME -> HomeScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                    Screen.SWAP -> SwapScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                    Screen.ACTIVITY -> ActivityScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                    Screen.SETTINGS -> SettingsScreen(
                        uiState = uiState,
                        viewModel = viewModel
                    )
                }
            }

            // Bottom Navigation Bar (Shown only on Home, Activity, Settings)
            if (uiState.currentScreen == Screen.HOME || uiState.currentScreen == Screen.ACTIVITY || uiState.currentScreen == Screen.SETTINGS) {
                ElysiumBottomBar(
                    currentScreen = uiState.currentScreen,
                    onNavigate = { destination ->
                        viewModel.navigateTo(destination)
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                )
            }

            // DIALOGS & OVERLAYS
            if (uiState.showWalletSelector) {
                WalletSelectorDialog(
                    wallets = uiState.wallets,
                    selectedWalletId = uiState.selectedWallet.id,
                    onSelectWallet = { wallet -> viewModel.selectWallet(wallet) },
                    onCreateNewWallet = { viewModel.setShowCreateWalletDialog(true) },
                    onRestoreWallet = { viewModel.setShowRestoreWalletDialog(true) },
                    onDismiss = { viewModel.setShowWalletSelector(false) }
                )
            }

            if (uiState.showPayTokenSelector) {
                TokenSelectorDialog(
                    tokens = uiState.tokens,
                    selectedTokenId = uiState.payToken.id,
                    onSelectToken = { token ->
                        viewModel.setPayToken(token)
                        viewModel.setShowPayTokenSelector(false)
                    },
                    onDismiss = { viewModel.setShowPayTokenSelector(false) }
                )
            }

            if (uiState.showReceiveTokenSelector) {
                TokenSelectorDialog(
                    tokens = uiState.tokens,
                    selectedTokenId = uiState.receiveToken.id,
                    onSelectToken = { token ->
                        viewModel.setReceiveToken(token)
                        viewModel.setShowReceiveTokenSelector(false)
                    },
                    onDismiss = { viewModel.setShowReceiveTokenSelector(false) }
                )
            }

            if (uiState.showSourceChainSelector) {
                ChainSelectorDialog(
                    chains = uiState.chains,
                    selectedChainId = uiState.sourceChain.id,
                    onSelectChain = { chain ->
                        viewModel.setSourceChain(chain)
                        viewModel.setShowSourceChainSelector(false)
                    },
                    onDismiss = { viewModel.setShowSourceChainSelector(false) }
                )
            }

            if (uiState.showDestinationChainSelector) {
                ChainSelectorDialog(
                    chains = uiState.chains,
                    selectedChainId = uiState.destinationChain.id,
                    onSelectChain = { chain ->
                        viewModel.setDestinationChain(chain)
                        viewModel.setShowDestinationChainSelector(false)
                    },
                    onDismiss = { viewModel.setShowDestinationChainSelector(false) }
                )
            }

            uiState.selectedTokenForDetails?.let { token ->
                TokenDetailDialog(
                    token = token,
                    onDismiss = { viewModel.setSelectedTokenDetails(null) },
                    onSwapClick = {
                        viewModel.setPayToken(token)
                        viewModel.setSelectedTokenDetails(null)
                        viewModel.navigateTo(Screen.SWAP)
                    }
                )
            }

            if (uiState.showSwapConfirmationSheet) {
                SwapConfirmationDialog(
                    payToken = uiState.payToken,
                    payAmount = uiState.payAmount,
                    receiveToken = uiState.receiveToken,
                    receiveAmount = uiState.receiveAmount,
                    sourceChainName = uiState.sourceChain.name,
                    destChainName = uiState.destinationChain.name,
                    onDismiss = { viewModel.setShowSwapConfirmationSheet(false) },
                    onConfirmed = {
                        viewModel.triggerActionNotImplemented("Swap execution (Sample preview)")
                    }
                )
            }

            if (uiState.showCreateWalletDialog) {
                CreateWalletDialog(
                    onDismiss = { viewModel.setShowCreateWalletDialog(false) },
                    onWalletCreated = { name, mnemonic ->
                        viewModel.createWalletWithMnemonic(name, mnemonic)
                    }
                )
            }

            if (uiState.showRestoreWalletDialog) {
                RestoreWalletDialog(
                    onDismiss = { viewModel.setShowRestoreWalletDialog(false) },
                    onWalletRestored = { name, mnemonic ->
                        viewModel.restoreWalletWithMnemonic(name, mnemonic)
                    }
                )
            }

            uiState.viewingSecretMnemonic?.let { mnemonic ->
                SecureSeedViewerDialog(
                    walletName = uiState.selectedWallet.name,
                    mnemonic = mnemonic,
                    isBackupVerified = uiState.isBackupVerified,
                    onDismiss = { viewModel.clearViewingMnemonic() },
                    onMarkVerified = { viewModel.markCurrentWalletBackupVerified() }
                )
            }
        }
    }
}
