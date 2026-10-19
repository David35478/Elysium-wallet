package com.example

import com.example.core.network.ElysiumRpcClient
import com.example.core.network.RpcResult
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.math.BigInteger

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ElysiumRpcClientTest {

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

    @Test
    fun `verifyChainId succeeds when RPC confirms chain ID 99801`() = runBlocking {
        // 99801 in hex is 0x185d9
        val mockJson = """{"jsonrpc":"2.0","id":1,"result":"0x185d9"}"""
        val client = ElysiumRpcClient(httpClient = createMockClient(mockJson))

        val result = client.verifyChainId()
        assertTrue("Expected Success, got $result", result is RpcResult.Success)
        assertEquals(99801L, (result as RpcResult.Success).data)
    }

    @Test
    fun `verifyChainId returns WrongChain when RPC returns different chain ID`() = runBlocking {
        // Ethereum mainnet chain ID 1
        val mockJson = """{"jsonrpc":"2.0","id":1,"result":"0x1"}"""
        val client = ElysiumRpcClient(httpClient = createMockClient(mockJson))

        val result = client.verifyChainId()
        assertTrue("Expected WrongChain, got $result", result is RpcResult.WrongChain)
        val wrong = result as RpcResult.WrongChain
        assertEquals(99801L, wrong.expected)
        assertEquals(1L, wrong.actual)
    }

    @Test
    fun `getNativeHypeBalance parses zero balance accurately`() = runBlocking {
        val mockJson = """{"jsonrpc":"2.0","id":2,"result":"0x0"}"""
        val client = ElysiumRpcClient(httpClient = createMockClient(mockJson))

        val result = client.getNativeHypeBalance("0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045")
        assertTrue("Expected Success, got $result", result is RpcResult.Success)
        val data = (result as RpcResult.Success).data
        assertEquals("0.0", data.formattedHype)
        assertEquals(BigInteger.ZERO, data.wei)
    }

    @Test
    fun `getNativeHypeBalance parses integer wei accurately without floating-point error`() = runBlocking {
        // 2 HYPE = 2 * 10^18 wei = 2000000000000000000 = 0x1bc16d674ec80000
        val mockJson = """{"jsonrpc":"2.0","id":2,"result":"0x1bc16d674ec80000"}"""
        val client = ElysiumRpcClient(httpClient = createMockClient(mockJson))

        val result = client.getNativeHypeBalance("0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045")
        assertTrue("Expected Success, got $result", result is RpcResult.Success)
        val data = (result as RpcResult.Success).data
        assertEquals("2.0", data.formattedHype)
    }

    @Test
    fun `getNativeHypeBalance parses fractional wei accurately`() = runBlocking {
        // 1.5 HYPE = 1500000000000000000 wei = 0x14d1120d7b160000
        val mockJson = """{"jsonrpc":"2.0","id":2,"result":"0x14d1120d7b160000"}"""
        val client = ElysiumRpcClient(httpClient = createMockClient(mockJson))

        val result = client.getNativeHypeBalance("0xd8dA6BF26964aF9D7eEd9e03E53415D37aA96045")
        assertTrue("Expected Success, got $result", result is RpcResult.Success)
        val data = (result as RpcResult.Success).data
        assertEquals("1.5", data.formattedHype)
    }

    @Test
    fun `getNativeHypeBalance rejects invalid EVM address without calling RPC`() = runBlocking {
        val mockJson = """{"jsonrpc":"2.0","id":2,"result":"0x1"}"""
        val client = ElysiumRpcClient(httpClient = createMockClient(mockJson))

        val result = client.getNativeHypeBalance("not-an-address")
        assertTrue("Expected Error for invalid address", result is RpcResult.Error)
    }

    @Test
    fun `weiHexToFormattedHype static precision tests`() {
        assertEquals("0.0", ElysiumRpcClient.weiHexToFormattedHype("0x0"))
        assertEquals("0.0", ElysiumRpcClient.weiHexToFormattedHype(""))
        // 1 wei rounds down to 0.0 at 6 decimal places
        assertEquals("0.0", ElysiumRpcClient.weiHexToFormattedHype("0x1"))
        // 10^18 wei = 1 HYPE
        assertEquals("1.0", ElysiumRpcClient.weiHexToFormattedHype("0xde0b6b3a7640000"))
    }
}
