package com.example.viewmodel

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.core.crypto.CryptoManager
import com.example.core.security.BiometricAuthManager
import com.example.data.SampleData
import com.example.data.repository.StoredWalletHeader
import com.example.data.repository.WalletVaultRepository
import com.example.model.Chain
import com.example.model.NftItem
import com.example.model.Screen
import com.example.model.TokenAsset
import com.example.model.TransactionItem
import com.example.model.WalletAccount
import com.example.ui.components.HomeTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

data class WalletUiState(
    val currentScreen: Screen = Screen.HOME,
    val selectedWallet: WalletAccount = SampleData.sampleWallets.first(),
    val wallets: List<WalletAccount> = SampleData.sampleWallets,
    val storedVaultWallets: List<StoredWalletHeader> = emptyList(),
    val selectedHomeTab: HomeTab = HomeTab.CRYPTO,
    val isSecurityBannerVisible: Boolean = true,
    val tokens: List<TokenAsset> = SampleData.sampleTokens,
    val nfts: List<NftItem> = SampleData.sampleNfts,
    val transactions: List<TransactionItem> = SampleData.sampleTransactions,
    val chains: List<Chain> = SampleData.sampleChains,

    // Swap State
    val sourceChain: Chain = SampleData.sampleChains[0], // BNB Chain
    val destinationChain: Chain = SampleData.sampleChains[1], // Ethereum
    val payToken: TokenAsset = SampleData.sampleTokens[0], // ETH
    val receiveToken: TokenAsset = SampleData.sampleTokens[4], // USDT
    val payAmount: String = "",
    val receiveAmount: String = "",
    val payFiatEstimate: String = "≈ $ 0",
    val receiveFiatEstimate: String = "≈ $ 0",

    // Dialog & Flow States
    val showWalletSelector: Boolean = false,
    val showPayTokenSelector: Boolean = false,
    val showReceiveTokenSelector: Boolean = false,
    val showSourceChainSelector: Boolean = false,
    val showDestinationChainSelector: Boolean = false,
    val showCreateWalletDialog: Boolean = false,
    val showRestoreWalletDialog: Boolean = false,
    val viewingSecretMnemonic: String? = null,
    val selectedTokenForDetails: TokenAsset? = null,
    val showSwapConfirmationSheet: Boolean = false,

    // Settings & Security
    val biometricLockEnabled: Boolean = true,
    val testnetsEnabled: Boolean = false,
    val hideSmallBalances: Boolean = false,
    val currency: String = "USD",
    val isBackupVerified: Boolean = false,

    // Feedback message
    val userNotification: String? = null,

    // Onboarding in-flight state
    val onboardingMnemonic: String? = null,

    // Phase 3: Elysium Testnet (Chain ID 99801) State
    val isTestnetActive: Boolean = true,
    val testnetNetworkName: String = "Elysium Testnet (99801)",
    val testnetChainId: Long = 99801L,
    val testnetChainIdConfirmed: Boolean = false,
    val testnetHypeBalance: String? = null,
    val isTestnetLoading: Boolean = false,
    val testnetErrorMessage: String? = null,
    val testnetLastUpdated: Long? = null
)

class WalletViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WalletVaultRepository(application)
    private val rpcClient = com.example.core.network.ElysiumRpcClient()
    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        loadWalletsFromVault()
    }

    private fun loadWalletsFromVault() {
        val stored = repository.getWalletHeaders()
        if (stored.isEmpty()) {
            _uiState.update {
                it.copy(
                    currentScreen = Screen.WELCOME,
                    storedVaultWallets = emptyList()
                )
            }
            return
        }

        val activeId = repository.getActiveWalletId() ?: stored.first().id
        val activeHeader = stored.find { it.id == activeId } ?: stored.first()

        val walletAccounts = stored.map { h ->
            WalletAccount(
                id = h.id,
                name = h.name,
                shortAddress = h.shortAddress,
                fullAddress = h.address,
                totalBalanceUsd = 0.0,
                change24hUsd = 0.0,
                change24hPct = 0.0
            )
        }

        val currentActiveAccount = walletAccounts.find { it.id == activeHeader.id } ?: walletAccounts.first()

        _uiState.update {
            it.copy(
                storedVaultWallets = stored,
                wallets = walletAccounts,
                selectedWallet = currentActiveAccount,
                isBackupVerified = activeHeader.isBackupVerified,
                isSecurityBannerVisible = !activeHeader.isBackupVerified
            )
        }

        refreshTestnetBalance()
    }

    fun navigateTo(screen: Screen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun selectHomeTab(tab: HomeTab) {
        _uiState.update { it.copy(selectedHomeTab = tab) }
    }

    fun dismissSecurityBanner() {
        _uiState.update { it.copy(isSecurityBannerVisible = false) }
    }

    fun selectWallet(wallet: WalletAccount) {
        repository.setActiveWalletId(wallet.id)
        val header = repository.getWalletHeaders().find { it.id == wallet.id }
        _uiState.update {
            it.copy(
                selectedWallet = wallet,
                isBackupVerified = header?.isBackupVerified ?: false,
                isSecurityBannerVisible = !(header?.isBackupVerified ?: false),
                userNotification = "Switched to ${wallet.name}"
            )
        }
    }

    fun copyAddressToClipboard() {
        _uiState.update {
            it.copy(userNotification = "Address copied: ${it.selectedWallet.fullAddress}")
        }
    }

    fun clearNotification() {
        _uiState.update { it.copy(userNotification = null) }
    }

    // Complete Phase 2 Onboarding Flow Methods
    fun startNewWalletCreation() {
        val mnemonic = CryptoManager.generate12WordMnemonic()
        _uiState.update {
            it.copy(
                onboardingMnemonic = mnemonic,
                currentScreen = Screen.ONBOARDING_LOCAL_PROTECTION,
                userNotification = "12-word mnemonic generated with SecureRandom"
            )
        }
    }

    fun onLocalProtectionConfirmed() {
        _uiState.update { it.copy(currentScreen = Screen.ONBOARDING_BACKUP_PHRASE) }
    }

    fun onBackupPhraseSaved() {
        _uiState.update { it.copy(currentScreen = Screen.ONBOARDING_VERIFY_PHRASE) }
    }

    fun onVerificationSuccess() {
        val mnemonic = _uiState.value.onboardingMnemonic ?: CryptoManager.generate12WordMnemonic()
        val walletNumber = _uiState.value.wallets.size + 1
        val walletName = "Wallet $walletNumber"
        val result = repository.restoreWallet(walletName, mnemonic)
        result.onSuccess { header ->
            repository.markBackupVerified(header.id)
            loadWalletsFromVault()
            _uiState.update {
                it.copy(
                    currentScreen = Screen.ONBOARDING_READY,
                    userNotification = "Wallet verified and secured in Keystore!"
                )
            }
        }.onFailure { err ->
            _uiState.update {
                it.copy(userNotification = "Wallet setup error: ${err.message}")
            }
        }
    }

    fun onImportWalletSuccess(walletName: String, mnemonic: String) {
        val result = repository.restoreWallet(walletName, mnemonic)
        result.onSuccess { header ->
            loadWalletsFromVault()
            _uiState.update {
                it.copy(
                    currentScreen = Screen.ONBOARDING_READY,
                    userNotification = "Wallet successfully restored!"
                )
            }
        }.onFailure { err ->
            _uiState.update {
                it.copy(userNotification = "Import error: ${err.message}")
            }
        }
    }

    fun onExploreWallet() {
        _uiState.update {
            it.copy(
                currentScreen = Screen.HOME,
                onboardingMnemonic = null
            )
        }
        refreshTestnetBalance()
    }

    /**
     * Phase 3: Bounded RPC balance query for the derived EVM wallet address
     * against Elysium / Hyperliquid EVM Testnet (Chain ID 99801).
     * Strictly avoids floating-point math, safely handles wrong-chain and RPC errors,
     * and never transmits secret keys.
     */
    fun refreshTestnetBalance() {
        val address = _uiState.value.selectedWallet.fullAddress
        if (address.isBlank() || !address.startsWith("0x")) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isTestnetLoading = true,
                    testnetErrorMessage = null
                )
            }

            // Step 1: Verify Chain ID (99801)
            val chainIdResult = rpcClient.verifyChainId()
            when (chainIdResult) {
                is com.example.core.network.RpcResult.WrongChain -> {
                    _uiState.update {
                        it.copy(
                            isTestnetLoading = false,
                            testnetChainIdConfirmed = false,
                            testnetErrorMessage = "RPC wrong chain: ${chainIdResult.actual} (expected 99801)"
                        )
                    }
                    return@launch
                }
                is com.example.core.network.RpcResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isTestnetLoading = false,
                            testnetErrorMessage = chainIdResult.message
                        )
                    }
                    return@launch
                }
                is com.example.core.network.RpcResult.Success -> {
                    _uiState.update {
                        it.copy(testnetChainIdConfirmed = true)
                    }
                }
            }

            // Step 2: Query native HYPE balance for derived wallet address
            val balanceResult = rpcClient.getNativeHypeBalance(address)
            when (balanceResult) {
                is com.example.core.network.RpcResult.Success -> {
                    val bal = balanceResult.data
                    _uiState.update {
                        it.copy(
                            isTestnetLoading = false,
                            testnetHypeBalance = bal.formattedHype,
                            testnetLastUpdated = System.currentTimeMillis(),
                            testnetErrorMessage = null
                        )
                    }
                }
                is com.example.core.network.RpcResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isTestnetLoading = false,
                            testnetErrorMessage = balanceResult.message
                        )
                    }
                }
                is com.example.core.network.RpcResult.WrongChain -> {
                    _uiState.update {
                        it.copy(
                            isTestnetLoading = false,
                            testnetErrorMessage = "Wrong network detected"
                        )
                    }
                }
            }
        }
    }

    fun copyMnemonicToClipboard() {
        _uiState.update {
            it.copy(userNotification = "Recovery phrase copied to clipboard")
        }
    }

    fun startOnboardingFlow() {
        _uiState.update { it.copy(currentScreen = Screen.WELCOME) }
    }

    // Wallet Creation & Restoration Flows
    fun setShowCreateWalletDialog(show: Boolean) {
        _uiState.update { it.copy(showCreateWalletDialog = show) }
    }

    fun setShowRestoreWalletDialog(show: Boolean) {
        _uiState.update { it.copy(showRestoreWalletDialog = show) }
    }

    fun createWalletWithMnemonic(walletName: String, mnemonic: String) {
        val result = repository.restoreWallet(walletName, mnemonic)
        result.onSuccess { header ->
            repository.markBackupVerified(header.id)
            loadWalletsFromVault()
            _uiState.update {
                it.copy(userNotification = "Wallet created & secured in Keystore vault: ${header.shortAddress}")
            }
        }.onFailure { err ->
            _uiState.update {
                it.copy(userNotification = "Failed to create wallet: ${err.message}")
            }
        }
    }

    fun restoreWalletWithMnemonic(walletName: String, mnemonic: String) {
        val result = repository.restoreWallet(walletName, mnemonic)
        result.onSuccess { header ->
            loadWalletsFromVault()
            _uiState.update {
                it.copy(userNotification = "Wallet successfully restored: ${header.shortAddress}")
            }
        }.onFailure { err ->
            _uiState.update {
                it.copy(userNotification = "Restoration failed: ${err.message}")
            }
        }
    }

    // Biometric-gated Seed Phrase Decryption
    fun requestViewSeedPhrase(activity: FragmentActivity) {
        val activeId = _uiState.value.selectedWallet.id

        if (BiometricAuthManager.isDeviceSecurityAvailable(activity)) {
            BiometricAuthManager.promptDeviceAuth(
                activity = activity,
                title = "Unlock Recovery Vault",
                subtitle = "Authenticate to view your secret recovery phrase",
                onSuccess = {
                    decryptAndShowMnemonic(activeId)
                },
                onError = { err ->
                    _uiState.update { it.copy(userNotification = "Authentication failed: $err") }
                }
            )
        } else {
            // Fallback for emulator / devices without lockscreen set
            decryptAndShowMnemonic(activeId)
        }
    }

    private fun decryptAndShowMnemonic(walletId: String) {
        val result = repository.decryptSecretPayload(walletId)
        result.onSuccess { secret ->
            _uiState.update {
                it.copy(viewingSecretMnemonic = secret.mnemonic)
            }
        }.onFailure { err ->
            _uiState.update {
                it.copy(userNotification = "Decryption error: ${err.message}")
            }
        }
    }

    fun clearViewingMnemonic() {
        _uiState.update { it.copy(viewingSecretMnemonic = null) }
    }

    fun markCurrentWalletBackupVerified() {
        val activeId = _uiState.value.selectedWallet.id
        repository.markBackupVerified(activeId)
        loadWalletsFromVault()
        _uiState.update {
            it.copy(
                isBackupVerified = true,
                isSecurityBannerVisible = false,
                userNotification = "Backup verified and marked safe."
            )
        }
    }

    // Dialog visibility controls
    fun setShowWalletSelector(show: Boolean) {
        _uiState.update { it.copy(showWalletSelector = show) }
    }

    fun setShowPayTokenSelector(show: Boolean) {
        _uiState.update { it.copy(showPayTokenSelector = show) }
    }

    fun setShowReceiveTokenSelector(show: Boolean) {
        _uiState.update { it.copy(showReceiveTokenSelector = show) }
    }

    fun setShowSourceChainSelector(show: Boolean) {
        _uiState.update { it.copy(showSourceChainSelector = show) }
    }

    fun setShowDestinationChainSelector(show: Boolean) {
        _uiState.update { it.copy(showDestinationChainSelector = show) }
    }

    fun setSelectedTokenDetails(token: TokenAsset?) {
        _uiState.update { it.copy(selectedTokenForDetails = token) }
    }

    fun setShowSwapConfirmationSheet(show: Boolean) {
        _uiState.update { it.copy(showSwapConfirmationSheet = show) }
    }

    // Swap Logic
    fun setSourceChain(chain: Chain) {
        _uiState.update { it.copy(sourceChain = chain) }
    }

    fun setDestinationChain(chain: Chain) {
        _uiState.update { it.copy(destinationChain = chain) }
    }

    fun toggleChains() {
        _uiState.update {
            it.copy(
                sourceChain = it.destinationChain,
                destinationChain = it.sourceChain
            )
        }
    }

    fun setPayToken(token: TokenAsset) {
        _uiState.update {
            val updated = it.copy(payToken = token)
            recalculateSwap(updated, updated.payAmount)
        }
    }

    fun setReceiveToken(token: TokenAsset) {
        _uiState.update {
            val updated = it.copy(receiveToken = token)
            recalculateSwap(updated, updated.payAmount)
        }
    }

    fun invertSwapTokens() {
        _uiState.update { current ->
            val newPay = current.receiveToken
            val newReceive = current.payToken
            val newPayAmount = current.receiveAmount
            val updated = current.copy(
                payToken = newPay,
                receiveToken = newReceive,
                payAmount = newPayAmount
            )
            recalculateSwap(updated, newPayAmount)
        }
    }

    fun onKeypadDigit(digit: String) {
        val currentAmount = _uiState.value.payAmount
        val newAmount = when {
            digit == "." -> {
                if (currentAmount.isEmpty()) "0."
                else if (currentAmount.contains(".")) currentAmount
                else "$currentAmount."
            }
            currentAmount == "0" -> digit
            else -> currentAmount + digit
        }
        _uiState.update { recalculateSwap(it, newAmount) }
    }

    fun onKeypadBackspace() {
        val currentAmount = _uiState.value.payAmount
        val newAmount = if (currentAmount.length <= 1) "" else currentAmount.dropLast(1)
        _uiState.update { recalculateSwap(it, newAmount) }
    }

    fun setMaxPayAmount() {
        val maxAmount = _uiState.value.payToken.balance.toString()
        _uiState.update { recalculateSwap(it, maxAmount) }
    }

    private fun recalculateSwap(state: WalletUiState, amountStr: String): WalletUiState {
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        if (amount <= 0.0 || amountStr.isEmpty()) {
            return state.copy(
                payAmount = amountStr,
                receiveAmount = "",
                payFiatEstimate = "≈ $ 0",
                receiveFiatEstimate = "≈ $ 0"
            )
        }

        val payFiat = amount * state.payToken.fiatPrice
        val receiveRate = if (state.receiveToken.fiatPrice > 0) state.payToken.fiatPrice / state.receiveToken.fiatPrice else 1.0
        val receiveAmountVal = amount * receiveRate
        val receiveFiat = receiveAmountVal * state.receiveToken.fiatPrice

        val formattedReceive = if (receiveAmountVal >= 100) {
            String.format(Locale.US, "%.2f", receiveAmountVal)
        } else if (receiveAmountVal >= 1) {
            String.format(Locale.US, "%.4f", receiveAmountVal)
        } else {
            String.format(Locale.US, "%.6f", receiveAmountVal)
        }

        return state.copy(
            payAmount = amountStr,
            receiveAmount = formattedReceive,
            payFiatEstimate = String.format(Locale.US, "≈ $ %,.2f", payFiat),
            receiveFiatEstimate = String.format(Locale.US, "≈ $ %,.2f", receiveFiat)
        )
    }

    // Settings actions
    fun toggleBiometrics() {
        _uiState.update { it.copy(biometricLockEnabled = !it.biometricLockEnabled) }
    }

    fun toggleTestnets() {
        _uiState.update { it.copy(testnetsEnabled = !it.testnetsEnabled) }
    }

    fun toggleHideSmallBalances() {
        _uiState.update { it.copy(hideSmallBalances = !it.hideSmallBalances) }
    }

    fun triggerActionNotImplemented(featureName: String) {
        _uiState.update {
            it.copy(userNotification = "$featureName is not active in this preview.")
        }
    }
}
