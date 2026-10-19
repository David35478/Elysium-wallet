package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.network.EvmRpcClient
import com.example.core.network.NetworkRegistry
import com.example.core.network.RpcResult
import com.example.model.EvmNetworkConfig
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.math.BigInteger

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NetworkRegistryTest {

    private lateinit var context: Context
    private lateinit var registry: NetworkRegistry

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences("elysium_network_prefs", Context.MODE_PRIVATE).edit().clear().commit()
        registry = NetworkRegistry(context)
    }

    @Test
    fun `default selected network is Elysium Testnet with chain ID 99801`() {
        val selected = registry.getSelectedNetwork()
        assertEquals("elysium_testnet", selected.id)
        assertEquals("Elysium Testnet", selected.name)
        assertEquals(99801L, selected.chainId)
        assertEquals("0x185d9", selected.chainIdHex)
        assertEquals("https://testnet-rpc.elysium.kinetiq.xyz", selected.rpcUrl)
        assertEquals("HYPE", selected.nativeCurrencySymbol)
        assertEquals(18, selected.nativeCurrencyDecimals)
        assertTrue(selected.isTestnet)
        assertTrue(selected.isEnabled)
    }

    @Test
    fun `HyperEVM Testnet is registered separately with chain ID 998`() {
        val hyperevm = registry.getNetworkById("hyperevm_testnet")
        assertNotNull(hyperevm)
        assertEquals("HyperEVM Testnet", hyperevm?.name)
        assertEquals(998L, hyperevm?.chainId)
        assertEquals("0x3e6", hyperevm?.chainIdHex)
        assertFalse(hyperevm?.chainId == registry.getSelectedNetwork().chainId)
    }

    @Test
    fun `switching selected network persists independently of wallet identity`() {
        val switched = registry.setSelectedNetworkId("hyperevm_testnet")
        assertTrue(switched)
        assertEquals(998L, registry.getSelectedNetwork().chainId)

        // Switching back to Elysium Testnet
        registry.setSelectedNetworkId("elysium_testnet")
        assertEquals(99801L, registry.getSelectedNetwork().chainId)
    }

    @Test
    fun `registerCustomNetwork validates URLs and chain IDs`() {
        // Invalid URL
        val invalidUrl = EvmNetworkConfig(
            id = "custom_test",
            name = "Bad URL Net",
            chainId = 12345L,
            chainIdHex = "0x3039",
            rpcUrl = "ftp://invalid-rpc.com",
            nativeCurrencyName = "Test",
            nativeCurrencySymbol = "TEST",
            explorerUrl = ""
        )
        assertTrue(registry.registerCustomNetwork(invalidUrl).isFailure)

        // Zero/Negative Chain ID
        val badChain = invalidUrl.copy(rpcUrl = "https://rpc.valid.com", chainId = 0L)
        assertTrue(registry.registerCustomNetwork(badChain).isFailure)

        // Duplicate Chain ID (99801 already exists)
        val dupChain = invalidUrl.copy(rpcUrl = "https://rpc.valid.com", chainId = 99801L)
        assertTrue(registry.registerCustomNetwork(dupChain).isFailure)

        // Valid Custom Network
        val valid = EvmNetworkConfig(
            id = "custom_valid",
            name = "Valid Custom Net",
            chainId = 77777L,
            chainIdHex = "0x12fd1",
            rpcUrl = "https://rpc.valid.com",
            nativeCurrencyName = "Valid Token",
            nativeCurrencySymbol = "VLD",
            explorerUrl = "https://explorer.valid.com"
        )
        val registerResult = registry.registerCustomNetwork(valid)
        assertTrue(registerResult.isSuccess)
        assertNotNull(registry.getNetworkById("custom_valid"))
        assertEquals(77777L, registry.getNetworkByChainId(77777L)?.chainId)
    }

    @Test
    fun `EvmRpcClient detects wrong chain when RPC responds with 998 for Elysium Testnet`() = runBlocking {
        // Mock RPC returning 998 (0x3e6) for HyperEVM instead of Elysium 99801 (0x185d9)
        val mockJson = """{"jsonrpc":"2.0","id":1,"result":"0x3e6"}"""
        val mockClient = createMockClient(mockJson)

        val client = EvmRpcClient(NetworkRegistry.ELYSIUM_TESTNET, mockClient)
        val result = client.verifyChainId()

        assertTrue("Expected WrongChain", result is RpcResult.WrongChain)
        val wrong = result as RpcResult.WrongChain
        assertEquals(99801L, wrong.expected)
        assertEquals(998L, wrong.actual)
    }

    @Test
    fun `EvmRpcClient succeeds when RPC returns 0x185d9 (99801)`() = runBlocking {
        val mockJson = """{"jsonrpc":"2.0","id":1,"result":"0x185d9"}"""
        val mockClient = createMockClient(mockJson)

        val client = EvmRpcClient(NetworkRegistry.ELYSIUM_TESTNET, mockClient)
        val result = client.verifyChainId()

        assertTrue("Expected Success", result is RpcResult.Success)
        assertEquals(99801L, (result as RpcResult.Success).data)
    }

    @Test
    fun `EvmRpcClient exact BigInteger wei conversions without float arithmetic`() {
        // Zero
        assertEquals("0.0", EvmRpcClient.weiToFormattedAmount("0x0", 18))
        assertEquals("0.0", EvmRpcClient.weiToFormattedAmount("", 18))

        // 1 HYPE = 10^18 wei = 0xde0b6b3a7640000
        assertEquals("1.0", EvmRpcClient.weiToFormattedAmount("0xde0b6b3a7640000", 18))

        // 5.25 HYPE = 5250000000000000000 wei = 0x48dbbf2f2ecd0000
        assertEquals("5.25", EvmRpcClient.weiToFormattedAmount("0x48dbbf2f2ecd0000", 18))

        // Parses hex quantity correctly to BigInteger
        assertEquals(BigInteger("1000000000000000000"), EvmRpcClient.parseHexQuantityToBigInteger("0xde0b6b3a7640000"))
    }

    private fun createMockClient(responseJson: String, statusCode: Int = 200): OkHttpClient {
        val interceptor = Interceptor { chain ->
            val request = chain.request()
            Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(statusCode)
                .message(if (statusCode == 200) "OK" else "Error")
                .body(responseJson.toResponseBody("application/json; charset=utf-8".toMediaType()))
                .build()
        }
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }
}
