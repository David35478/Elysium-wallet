package com.example.viewmodel

import android.app.Application
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
import com.example.core.network.EvmRpcClient
import com.example.core.network.NetworkRegistry
import com.example.core.network.RpcResult
import com.example.model.EvmNetworkConfig
import com.example.model.NetworkConnectionStatus
import com.example.model.TokenIconType
import com.example.core.dapp.WalletProviderBridge
import com.example.data.repository.BrowserStorageRepository
import com.example.data.repository.DappPermissionRepository
import com.example.model.DappBookmark
import com.example.model.DappChainSwitchRequest
import com.example.model.DappConnectionRequest
import com.example.model.DappHistoryItem
import com.example.model.DappPermission
import com.example.model.DappSignMessageRequest
import com.example.model.DappTransactionRequest
import com.example.model.FeaturedDapp
import android.webkit.WebView
import java.lang.ref.WeakReference
import kotlinx.coroutines.Job
import java.util.Locale

data class WalletUiState(
    val currentScreen: Screen = Screen.HOME,
    val selectedWallet: WalletAccount = SampleData.sampleWallets.first(),
    val wallets: List<WalletAccount> = SampleData.sampleWallets,
    val storedVaultWallets: List<StoredWalletHeader> = emptyList(),
    val selectedHomeTab: HomeTab = HomeTab.CRYPTO,
    val isSecurityBannerVisible: Boolean = true,
    val tokens: List<TokenAsset> = emptyList(),
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

    // Multi-Network EVM Architecture State
    val selectedNetwork: EvmNetworkConfig = NetworkRegistry.ELYSIUM_TESTNET,
    val availableNetworks: List<EvmNetworkConfig> = NetworkRegistry.BUILT_IN_NETWORKS,
    val networkStatus: NetworkConnectionStatus = NetworkConnectionStatus.CHECKING,
    val networkBlockNumber: Long? = null,
    val networkBalanceFormatted: String? = null,
    val isNetworkLoading: Boolean = false,
    val networkErrorMessage: String? = null,
    val networkLastUpdated: Long? = null,
    val showNetworkSelectorDialog: Boolean = false,

    // DApp Browser State
    val browserCurrentUrl: String? = null,
    val browserPageTitle: String? = null,
    val isBrowserLoading: Boolean = false,
    val browserProgress: Int = 0,
    val browserErrorMessage: String? = null,
    val browserBookmarks: List<DappBookmark> = emptyList(),
    val browserHistory: List<DappHistoryItem> = emptyList(),
    val featuredDapps: List<FeaturedDapp> = BrowserStorageRepository.DEFAULT_FEATURED_DAPPS,
    val pendingConnectionRequest: DappConnectionRequest? = null,
    val pendingSignMessageRequest: DappSignMessageRequest? = null,
    val pendingTransactionRequest: DappTransactionRequest? = null,
    val pendingChainSwitchRequest: DappChainSwitchRequest? = null,
    val connectedDappsList: List<DappPermission> = emptyList()
) {
    // Backwards-compatible convenience properties
    val isTestnetActive: Boolean get() = selectedNetwork.isTestnet
    val testnetNetworkName: String get() = "${selectedNetwork.name} (${selectedNetwork.chainId})"
    val testnetChainId: Long get() = selectedNetwork.chainId
    val testnetChainIdConfirmed: Boolean get() = networkStatus == NetworkConnectionStatus.CONNECTED
    val testnetHypeBalance: String? get() = networkBalanceFormatted
    val isTestnetLoading: Boolean get() = isNetworkLoading
    val testnetErrorMessage: String? get() = networkErrorMessage
}

class WalletViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WalletVaultRepository(application)
    private val networkRegistry = NetworkRegistry(application)
    private val browserStorage = BrowserStorageRepository(application)
    private val dappPermissionRepository = DappPermissionRepository(application)
    private var evmRpcClient = EvmRpcClient(networkRegistry.getSelectedNetwork())
    private var networkRefreshJob: Job? = null
    private var webViewRef: WeakReference<WebView>? = null

    val walletProviderBridge: WalletProviderBridge = WalletProviderBridge(
        webViewProvider = { webViewRef?.get() },
        getActiveWallet = { _uiState.value.selectedWallet },
        getSelectedNetwork = { _uiState.value.selectedNetwork },
        permissionRepository = dappPermissionRepository,
        networkRegistry = networkRegistry,
        scope = viewModelScope,
        onConnectionRequested = { request ->
            _uiState.update { it.copy(pendingConnectionRequest = request) }
        },
        onSignMessageRequested = { request ->
            _uiState.update { it.copy(pendingSignMessageRequest = request) }
        },
        onTransactionRequested = { request ->
            _uiState.update { it.copy(pendingTransactionRequest = request) }
        },
        onChainSwitchRequested = { request ->
            _uiState.update { it.copy(pendingChainSwitchRequest = request) }
        }
    )

    private val _uiState = MutableStateFlow(WalletUiState())
    val uiState: StateFlow<WalletUiState> = _uiState.asStateFlow()

    init {
        val initialNetwork = networkRegistry.getSelectedNetwork()
        evmRpcClient = EvmRpcClient(initialNetwork)
        _uiState.update {
            it.copy(
                selectedNetwork = initialNetwork,
                availableNetworks = networkRegistry.getAllNetworks(),
                tokens = generateTokenListForNetwork(initialNetwork, null),
                browserBookmarks = browserStorage.getBookmarks(),
                browserHistory = browserStorage.getHistory(),
                connectedDappsList = dappPermissionRepository.getAllPermissions()
            )
        }
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

        refreshNetworkBalance()
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
        refreshNetworkBalance()
    }

    /**
     * Switches the active EVM network.
     * Persists the selected network separately from wallet identity.
     * Re-validates the new RPC, fetches the fresh native balance, and updates assets.
     */
    fun selectNetwork(network: EvmNetworkConfig) {
        networkRegistry.setSelectedNetworkId(network.id)
        evmRpcClient = EvmRpcClient(network)
        _uiState.update {
            it.copy(
                selectedNetwork = network,
                networkStatus = NetworkConnectionStatus.CHECKING,
                networkBalanceFormatted = null,
                networkErrorMessage = null,
                tokens = generateTokenListForNetwork(network, null),
                userNotification = "Switched to ${network.name}"
            )
        }
        refreshNetworkBalance()
    }

    fun setShowNetworkSelector(show: Boolean) {
        _uiState.update { it.copy(showNetworkSelectorDialog = show) }
    }

    /**
     * Real RPC health check & native balance query for the selected EVM network.
     * 1. Calls eth_chainId and validates response against expected network chainId.
     * 2. If valid, fetches latest block number using eth_blockNumber.
     * 3. Fetches native balance for the active wallet address using eth_getBalance.
     * 4. Converts wei using exact decimal arithmetic with zero floating-point math.
     * 5. Sets honest connection, loading, wrong-chain, or error states.
     */
    fun refreshNetworkBalance() {
        val address = _uiState.value.selectedWallet.fullAddress
        if (address.isBlank() || !address.startsWith("0x")) return

        networkRefreshJob?.cancel()
        networkRefreshJob = viewModelScope.launch {
            val network = _uiState.value.selectedNetwork
            _uiState.update {
                it.copy(
                    isNetworkLoading = true,
                    networkErrorMessage = null,
                    networkStatus = NetworkConnectionStatus.CHECKING
                )
            }

            // Step 1: Real RPC Health Check - Verify Chain ID
            val chainIdResult = evmRpcClient.verifyChainId()
            when (chainIdResult) {
                is RpcResult.WrongChain -> {
                    _uiState.update {
                        it.copy(
                            isNetworkLoading = false,
                            networkStatus = NetworkConnectionStatus.WRONG_CHAIN,
                            networkErrorMessage = "RPC wrong chain: ${chainIdResult.actual} (expected ${network.chainId})"
                        )
                    }
                    return@launch
                }
                is RpcResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isNetworkLoading = false,
                            networkStatus = NetworkConnectionStatus.UNAVAILABLE,
                            networkErrorMessage = "Unable to connect to ${network.name}: ${chainIdResult.message}"
                        )
                    }
                    return@launch
                }
                is RpcResult.Success -> {
                    _uiState.update {
                        it.copy(networkStatus = NetworkConnectionStatus.CONNECTED)
                    }
                }
            }

            // Step 2: Query latest block height
            val blockResult = evmRpcClient.getLatestBlockNumber()
            if (blockResult is RpcResult.Success) {
                _uiState.update {
                    it.copy(networkBlockNumber = blockResult.data)
                }
            }

            // Step 3: Query native balance for the active wallet address
            val balanceResult = evmRpcClient.getBalance(address)
            when (balanceResult) {
                is RpcResult.Success -> {
                    val formatted = balanceResult.data.formattedBalance
                    _uiState.update {
                        it.copy(
                            isNetworkLoading = false,
                            networkBalanceFormatted = formatted,
                            networkLastUpdated = System.currentTimeMillis(),
                            networkErrorMessage = null,
                            tokens = generateTokenListForNetwork(network, formatted)
                        )
                    }
                }
                is RpcResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isNetworkLoading = false,
                            networkErrorMessage = "Balance query error: ${balanceResult.message}"
                        )
                    }
                }
                is RpcResult.WrongChain -> {
                    _uiState.update {
                        it.copy(
                            isNetworkLoading = false,
                            networkStatus = NetworkConnectionStatus.WRONG_CHAIN,
                            networkErrorMessage = "Unexpected wrong chain response"
                        )
                    }
                }
            }
        }
    }

    /**
     * Backwards-compatible alias for refreshing balance.
     */
    fun refreshTestnetBalance() {
        refreshNetworkBalance()
    }

    /**
     * Generates honest token list for the selected network without fabricated data or prices.
     */
    private fun generateTokenListForNetwork(network: EvmNetworkConfig, balance: String?): List<TokenAsset> {
        val cleanBalance = balance ?: "0.0"
        val balanceNumeric = cleanBalance.toDoubleOrNull() ?: 0.0
        val iconType = when (network.nativeCurrencySymbol) {
            "HYPE" -> TokenIconType.HYPE
            "ETH" -> TokenIconType.ETH
            "BNB" -> TokenIconType.BNB
            "SOL" -> TokenIconType.SOL
            else -> TokenIconType.HYPE
        }

        val nativeToken = TokenAsset(
            id = "native_${network.id}",
            symbol = network.nativeCurrencySymbol,
            name = "${network.name} Native",
            balance = balanceNumeric,
            balanceDisplay = "$cleanBalance ${network.nativeCurrencySymbol}",
            fiatPrice = 0.0,
            fiatValue = 0.0,
            fiatDisplay = "—",
            priceChange24hPct = 0.0,
            priceChangeDisplay = if (network.isTestnet) "Testnet Native Asset" else "Native Asset",
            isPositive = true,
            chain = network.name,
            iconType = iconType
        )

        return listOf(nativeToken)
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

    // DApp Browser Actions & Provider Bridge Handlers
    fun setWebViewInstance(webView: WebView) {
        webViewRef = WeakReference(webView)
    }

    fun openDappUrl(url: String) {
        val cleanUrl = url.trim()
        val origin = DappPermissionRepository.extractOrigin(cleanUrl)
        walletProviderBridge.cancelPendingRequestsForOriginOrContext(
            newOrigin = origin,
            newAccount = null,
            newChainId = null
        )
        browserStorage.addHistoryItem(cleanUrl, cleanUrl)
        _uiState.update {
            it.copy(
                browserCurrentUrl = cleanUrl,
                browserErrorMessage = null,
                isBrowserLoading = true,
                browserHistory = browserStorage.getHistory()
            )
        }
    }

    fun closeDappBrowser() {
        walletProviderBridge.cancelPendingRequestsForOriginOrContext(
            newOrigin = "",
            newAccount = null,
            newChainId = null
        )
        _uiState.update {
            it.copy(
                browserCurrentUrl = null,
                browserPageTitle = null,
                browserErrorMessage = null,
                isBrowserLoading = false,
                pendingConnectionRequest = null,
                pendingSignMessageRequest = null,
                pendingTransactionRequest = null,
                pendingChainSwitchRequest = null
            )
        }
    }

    fun onBrowserPageStarted(url: String) {
        val newOrigin = DappPermissionRepository.extractOrigin(url)
        walletProviderBridge.cancelPendingRequestsForOriginOrContext(
            newOrigin = newOrigin,
            newAccount = null,
            newChainId = null
        )
        _uiState.update {
            it.copy(
                browserCurrentUrl = url,
                isBrowserLoading = true,
                browserProgress = 10,
                browserErrorMessage = null
            )
        }
    }

    fun onBrowserPageFinished(url: String, title: String?) {
        if (!title.isNullOrBlank()) {
            browserStorage.addHistoryItem(title, url)
        }
        _uiState.update {
            it.copy(
                browserCurrentUrl = url,
                browserPageTitle = title,
                isBrowserLoading = false,
                browserProgress = 100,
                browserHistory = browserStorage.getHistory()
            )
        }
    }

    fun onBrowserProgressChanged(progress: Int) {
        _uiState.update {
            it.copy(
                browserProgress = progress,
                isBrowserLoading = progress < 100
            )
        }
    }

    fun onBrowserError(error: String) {
        _uiState.update {
            it.copy(
                browserErrorMessage = error,
                isBrowserLoading = false
            )
        }
    }

    fun toggleBookmark(title: String, url: String) {
        if (browserStorage.isBookmarked(url)) {
            browserStorage.removeBookmark(url)
            _uiState.update {
                it.copy(
                    browserBookmarks = browserStorage.getBookmarks(),
                    userNotification = "Bookmark removed"
                )
            }
        } else {
            browserStorage.addBookmark(title, url)
            _uiState.update {
                it.copy(
                    browserBookmarks = browserStorage.getBookmarks(),
                    userNotification = "Bookmark saved"
                )
            }
        }
    }

    fun removeBookmark(url: String) {
        browserStorage.removeBookmark(url)
        _uiState.update {
            it.copy(
                browserBookmarks = browserStorage.getBookmarks(),
                userNotification = "Bookmark removed"
            )
        }
    }

    fun clearBrowserHistory() {
        browserStorage.clearHistory()
        _uiState.update {
            it.copy(
                browserHistory = emptyList(),
                userNotification = "Browsing history cleared"
            )
        }
    }

    // Permission & Approval handlers
    fun approveDappConnection(requestId: String) {
        val req = _uiState.value.pendingConnectionRequest ?: return
        walletProviderBridge.approveConnection(requestId, req.origin, _uiState.value.selectedWallet.fullAddress)
        _uiState.update {
            it.copy(
                pendingConnectionRequest = null,
                connectedDappsList = dappPermissionRepository.getAllPermissions(),
                userNotification = "Connected to ${req.hostname}"
            )
        }
    }

    fun rejectDappConnection(requestId: String) {
        walletProviderBridge.rejectRequest(requestId, "User rejected connection")
        _uiState.update { it.copy(pendingConnectionRequest = null) }
    }

    fun approveDappSignMessage(requestId: String) {
        val req = _uiState.value.pendingSignMessageRequest ?: return
        val activeWallet = _uiState.value.selectedWallet
        viewModelScope.launch {
            val decryptResult = repository.decryptSecretPayload(activeWallet.id)
            if (decryptResult.isFailure) {
                walletProviderBridge.rejectRequest(requestId, "Failed to decrypt wallet keys")
                _uiState.update {
                    it.copy(
                        pendingSignMessageRequest = null,
                        userNotification = "Failed to access keys"
                    )
                }
                return@launch
            }

            val secret = decryptResult.getOrThrow()
            val derived = CryptoManager.deriveEvmAccount(secret.mnemonic)
            try {
                val sigHex = CryptoManager.personalSign(req.rawMessage, derived.privateKeyHex)
                walletProviderBridge.approveSignMessage(requestId, sigHex)
                _uiState.update {
                    it.copy(
                        pendingSignMessageRequest = null,
                        userNotification = "Message signed for ${req.hostname}"
                    )
                }
            } catch (e: Exception) {
                walletProviderBridge.rejectRequest(requestId, "Signing error: ${e.message}")
                _uiState.update {
                    it.copy(
                        pendingSignMessageRequest = null,
                        userNotification = "Signing error: ${e.message}"
                    )
                }
            }
        }
    }

    fun rejectDappSignMessage(requestId: String) {
        walletProviderBridge.rejectRequest(requestId, "User rejected signature")
        _uiState.update { it.copy(pendingSignMessageRequest = null) }
    }

    fun approveDappTransaction(requestId: String) {
        val req = _uiState.value.pendingTransactionRequest ?: return
        val activeWallet = _uiState.value.selectedWallet
        val currentNetwork = _uiState.value.selectedNetwork

        viewModelScope.launch {
            // Step 1: Re-validate active network chain ID
            val chainCheck = evmRpcClient.verifyChainId()
            if (chainCheck !is RpcResult.Success) {
                walletProviderBridge.rejectRequest(requestId, "Network validation failed before broadcast")
                _uiState.update {
                    it.copy(
                        pendingTransactionRequest = null,
                        userNotification = "Network validation failed. Transaction cancelled."
                    )
                }
                return@launch
            }

            // Step 2: Fetch nonce
            val nonceResult = evmRpcClient.getTransactionCount(activeWallet.fullAddress)
            val nonce = when (nonceResult) {
                is RpcResult.Success -> nonceResult.data
                else -> java.math.BigInteger.ZERO
            }

            // Step 3: Decrypt private key
            val decryptResult = repository.decryptSecretPayload(activeWallet.id)
            if (decryptResult.isFailure) {
                walletProviderBridge.rejectRequest(requestId, "Decryption error")
                _uiState.update {
                    it.copy(
                        pendingTransactionRequest = null,
                        userNotification = "Failed to access keys"
                    )
                }
                return@launch
            }

            val secret = decryptResult.getOrThrow()
            val derived = CryptoManager.deriveEvmAccount(secret.mnemonic)

            try {
                val gasPrice = if (req.gasPrice > java.math.BigInteger.ZERO) req.gasPrice else java.math.BigInteger.valueOf(20_000_000_000L)
                val signedRawHex = CryptoManager.signTransaction(
                    nonce = nonce,
                    gasPrice = gasPrice,
                    gasLimit = req.gasLimit,
                    to = req.toAddress,
                    value = req.valueWei,
                    data = req.data,
                    chainId = currentNetwork.chainId,
                    privateKeyHex = derived.privateKeyHex
                )

                // Step 4: Broadcast to RPC
                val broadcastResult = evmRpcClient.sendRawTransaction(signedRawHex)
                when (broadcastResult) {
                    is RpcResult.Success -> {
                        walletProviderBridge.approveTransaction(requestId, broadcastResult.data)
                        _uiState.update {
                            it.copy(
                                pendingTransactionRequest = null,
                                userNotification = "Transaction broadcasted: ${broadcastResult.data.take(10)}..."
                            )
                        }
                    }
                    is RpcResult.Error -> {
                        walletProviderBridge.rejectRequest(requestId, broadcastResult.message)
                        _uiState.update {
                            it.copy(
                                pendingTransactionRequest = null,
                                userNotification = "Broadcast error: ${broadcastResult.message}"
                            )
                        }
                    }
                    is RpcResult.WrongChain -> {
                        walletProviderBridge.rejectRequest(requestId, "Wrong chain ID detected on RPC node")
                        _uiState.update {
                            it.copy(
                                pendingTransactionRequest = null,
                                userNotification = "RPC node wrong chain: transaction blocked"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                walletProviderBridge.rejectRequest(requestId, "Transaction failed: ${e.message}")
                _uiState.update {
                    it.copy(
                        pendingTransactionRequest = null,
                        userNotification = "Transaction failed: ${e.message}"
                    )
                }
            }
        }
    }

    fun rejectDappTransaction(requestId: String) {
        walletProviderBridge.rejectRequest(requestId, "User rejected transaction")
        _uiState.update { it.copy(pendingTransactionRequest = null) }
    }

    fun approveDappChainSwitch(requestId: String) {
        val req = _uiState.value.pendingChainSwitchRequest ?: return
        val target = req.targetNetwork ?: return
        selectNetwork(target)
        walletProviderBridge.approveChainSwitch(requestId, target)
        _uiState.update {
            it.copy(
                pendingChainSwitchRequest = null,
                userNotification = "Switched to ${target.name}"
            )
        }
    }

    fun rejectDappChainSwitch(requestId: String) {
        walletProviderBridge.rejectRequest(requestId, "User rejected chain switch")
        _uiState.update { it.copy(pendingChainSwitchRequest = null) }
    }

    fun revokeDappPermission(origin: String) {
        dappPermissionRepository.revokeOrigin(origin)
        _uiState.update {
            it.copy(
                connectedDappsList = dappPermissionRepository.getAllPermissions(),
                userNotification = "Disconnected $origin"
            )
        }
    }

    fun revokeAllDappPermissions() {
        dappPermissionRepository.clearAllPermissions()
        _uiState.update {
            it.copy(
                connectedDappsList = emptyList(),
                userNotification = "All DApp permissions revoked"
            )
        }
    }
}
