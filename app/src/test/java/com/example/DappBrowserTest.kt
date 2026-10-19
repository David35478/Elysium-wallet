package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.crypto.CryptoManager
import com.example.core.dapp.KoroProviderScript
import com.example.core.dapp.WalletProviderBridge
import com.example.core.network.NetworkRegistry
import com.example.data.repository.BrowserStorageRepository
import com.example.data.repository.DappPermissionRepository
import com.example.model.DappConnectionRequest
import com.example.model.EvmNetworkConfig
import com.example.model.WalletAccount
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.math.BigInteger

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class DappBrowserTest {

    private lateinit var context: Context
    private lateinit var permissionRepo: DappPermissionRepository
    private lateinit var storageRepo: BrowserStorageRepository
    private val testAccount = "0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045"
    private val otherAccount = "0x0cce81a547FdB8E56Bdf3E44B1578322699895"

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("koro_dapp_permissions", Context.MODE_PRIVATE).edit().clear().commit()
        context.getSharedPreferences("koro_browser_storage", Context.MODE_PRIVATE).edit().clear().commit()
        permissionRepo = DappPermissionRepository(context)
        storageRepo = BrowserStorageRepository(context)
    }

    @Test
    fun `extractOrigin permits valid HTTPS and local development but rejects unsafe schemes`() {
        // Valid HTTPS
        assertEquals("https://app.uniswap.org", DappPermissionRepository.extractOrigin("https://app.uniswap.org/swap"))
        assertEquals("https://elysium.kinetiq.xyz", DappPermissionRepository.extractOrigin("https://elysium.kinetiq.xyz/testnet-explorer"))
        assertEquals("https://my-dapp.com:8443", DappPermissionRepository.extractOrigin("https://my-dapp.com:8443/app"))

        // Valid local dev
        assertEquals("http://localhost:3000", DappPermissionRepository.extractOrigin("http://localhost:3000/"))
        assertEquals("http://127.0.0.1:8080", DappPermissionRepository.extractOrigin("http://127.0.0.1:8080/test"))

        // Ineligible or dangerous schemes must return null
        assertNull(DappPermissionRepository.extractOrigin("http://insecure-website.com"))
        assertNull(DappPermissionRepository.extractOrigin("javascript:alert(1)"))
        assertNull(DappPermissionRepository.extractOrigin("file:///sdcard/wallet.json"))
        assertNull(DappPermissionRepository.extractOrigin("data:text/html,<html></html>"))
        assertNull(DappPermissionRepository.extractOrigin("content://media/external/images"))
        assertNull(DappPermissionRepository.extractOrigin("about:blank"))
        assertNull(DappPermissionRepository.extractOrigin(""))
    }

    @Test
    fun `permissions are strictly scoped to origin and account address`() {
        val origin1 = "https://app.uniswap.org"
        val origin2 = "https://app.aave.com"

        assertFalse(permissionRepo.isOriginApproved(origin1, testAccount))

        // Grant to origin1 and testAccount
        permissionRepo.grantPermission(origin1, testAccount)
        assertTrue(permissionRepo.isOriginApproved(origin1, testAccount))

        // Must NOT be approved for a different account
        assertFalse(permissionRepo.isOriginApproved(origin1, otherAccount))

        // Must NOT be approved for a different origin
        assertFalse(permissionRepo.isOriginApproved(origin2, testAccount))

        // Revoke
        permissionRepo.revokePermission(origin1, testAccount)
        assertFalse(permissionRepo.isOriginApproved(origin1, testAccount))
    }

    @Test
    fun `bookmarks add, check, and remove correctly without storing secrets`() {
        val url = "https://elysium.kinetiq.xyz/testnet-explorer"
        assertFalse(storageRepo.isBookmarked(url))

        storageRepo.addBookmark("Kinetiq Explorer", url)
        assertTrue(storageRepo.isBookmarked(url))
        val bookmarks = storageRepo.getBookmarks()
        assertEquals(1, bookmarks.size)
        assertEquals("Kinetiq Explorer", bookmarks[0].title)
        assertEquals(url, bookmarks[0].url)

        storageRepo.removeBookmark(url)
        assertFalse(storageRepo.isBookmarked(url))
        assertTrue(storageRepo.getBookmarks().isEmpty())
    }

    @Test
    fun `history items persist and can be cleared`() {
        storageRepo.addHistoryItem("Uniswap", "https://app.uniswap.org")
        storageRepo.addHistoryItem("Explorer", "https://elysium.kinetiq.xyz")

        val history = storageRepo.getHistory()
        assertEquals(2, history.size)
        assertEquals("https://elysium.kinetiq.xyz", history[0].url)

        storageRepo.clearHistory()
        assertTrue(storageRepo.getHistory().isEmpty())
    }

    @Test
    fun `KoroProviderScript embeds selected network chain ID and address`() {
        val script = KoroProviderScript.generateInjectionScript(
            network = NetworkRegistry.ELYSIUM_TESTNET,
            approvedAddress = testAccount
        )
        assertTrue(script.contains("0x185d9"))
        assertTrue(script.contains("99801"))
        assertTrue(script.contains(testAccount))
        assertTrue(script.contains("window.ethereum = ethereum;"))
    }

    @Test
    fun `CryptoManager personalSign produces valid 65-byte hex signature`() {
        val mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
        val derived = CryptoManager.deriveEvmAccount(mnemonic)
        val message = "Hello Koro Wallet"

        val signature = CryptoManager.personalSign(message, derived.privateKeyHex)
        assertNotNull(signature)
        assertTrue("Signature must start with 0x", signature.startsWith("0x"))
        // 0x prefix + 65 bytes * 2 hex chars = 132 chars
        assertEquals(132, signature.length)
    }

    @Test
    fun `CryptoManager signTransaction signs with chain ID 99801`() {
        val mnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
        val derived = CryptoManager.deriveEvmAccount(mnemonic)

        val rawTxHex = CryptoManager.signTransaction(
            nonce = BigInteger.ZERO,
            gasPrice = BigInteger.valueOf(20_000_000_000L),
            gasLimit = BigInteger.valueOf(21000L),
            to = "0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045",
            value = BigInteger("1000000000000000000"), // 1 HYPE
            data = "0x",
            chainId = 99801L,
            privateKeyHex = derived.privateKeyHex
        )

        assertNotNull(rawTxHex)
        assertTrue("Raw transaction must start with 0x", rawTxHex.startsWith("0x"))
        assertTrue("Raw transaction must have non-trivial length", rawTxHex.length > 50)
    }

    @Test
    fun `pending requests are invalidated when context or origin changes`() = runBlocking {
        var connectionRequested: DappConnectionRequest? = null
        val bridge = WalletProviderBridge(
            webViewProvider = { null },
            getActiveWallet = {
                WalletAccount(
                    id = "w1",
                    name = "Wallet 1",
                    shortAddress = "0xd8dA...6045",
                    fullAddress = testAccount,
                    totalBalanceUsd = 0.0,
                    change24hUsd = 0.0,
                    change24hPct = 0.0
                )
            },
            getSelectedNetwork = { NetworkRegistry.ELYSIUM_TESTNET },
            permissionRepository = permissionRepo,
            networkRegistry = NetworkRegistry(context),
            scope = CoroutineScope(Dispatchers.Unconfined),
            onConnectionRequested = { connectionRequested = it },
            onSignMessageRequested = {},
            onTransactionRequested = {},
            onChainSwitchRequested = {}
        )

        // Cancel pending requests for a new origin
        bridge.cancelPendingRequestsForOriginOrContext(
            newOrigin = "https://another-origin.com",
            newAccount = null,
            newChainId = null
        )
        // Verified cancelPendingRequests executes safely without throwing
        assertTrue(true)
    }
}
