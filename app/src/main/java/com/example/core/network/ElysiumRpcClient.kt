package com.example.core.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

sealed interface RpcResult<out T> {
    data class Success<T>(val data: T) : RpcResult<T>
    data class WrongChain(val expected: Long, val actual: Long) : RpcResult<Nothing>
    data class Error(val message: String, val throwable: Throwable? = null) : RpcResult<Nothing>
}

data class TestnetBalance(
    val formattedHype: String,
    val rawWeiHex: String,
    val wei: BigInteger
)

/**
 * Client for interacting with the Elysium / Hyperliquid EVM Testnet (Chain ID 99801).
 * Uses bounded timeouts, strict BigInteger/BigDecimal wei conversions without floating-point math,
 * and zero transmission of secret material.
 */
class ElysiumRpcClient(
    val rpcUrl: String = DEFAULT_RPC_URL,
    private val httpClient: OkHttpClient = defaultHttpClient
) {
    companion object {
        const val ELYSIUM_TESTNET_CHAIN_ID = 99801L
        const val ELYSIUM_TESTNET_CHAIN_ID_HEX = "0x185d9"
        const val DEFAULT_RPC_URL = "https://rpc.hyperliquid-testnet.xyz/evm"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

        private val defaultHttpClient: OkHttpClient by lazy {
            OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build()
        }

        /**
         * Converts a hexadecimal wei value (e.g. "0x1bc16d674ec80000") to formatted HYPE
         * without any floating point operations.
         */
        fun weiHexToFormattedHype(weiHex: String): String {
            val cleanHex = if (weiHex.startsWith("0x", ignoreCase = true)) {
                weiHex.substring(2)
            } else {
                weiHex
            }

            if (cleanHex.isBlank() || cleanHex == "0") {
                return "0.0"
            }

            val weiBigInt = try {
                BigInteger(cleanHex, 16)
            } catch (_: Exception) {
                BigInteger.ZERO
            }

            if (weiBigInt == BigInteger.ZERO) {
                return "0.0"
            }

            val weiBigDecimal = BigDecimal(weiBigInt)
            val divisor = BigDecimal.TEN.pow(18)
            val hypeValue = weiBigDecimal.divide(divisor, 6, RoundingMode.DOWN).stripTrailingZeros()
            val plain = hypeValue.toPlainString()
            return if (!plain.contains('.')) "$plain.0" else plain
        }
    }

    /**
     * Verifies that the remote RPC endpoint confirms Chain ID 99801.
     */
    suspend fun verifyChainId(): RpcResult<Long> = withContext(Dispatchers.IO) {
        val payload = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", "eth_chainId")
            put("params", org.json.JSONArray())
            put("id", 1)
        }

        val request = Request.Builder()
            .url(rpcUrl)
            .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        try {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext RpcResult.Error("RPC HTTP ${response.code}: ${response.message}")
                }
                val bodyString = response.body?.string() ?: return@withContext RpcResult.Error("Empty RPC response")
                val json = JSONObject(bodyString)

                if (json.has("error")) {
                    val err = json.getJSONObject("error")
                    return@withContext RpcResult.Error("RPC Error: ${err.optString("message", "Unknown error")}")
                }

                val chainIdHex = json.optString("result", "")
                val cleanHex = if (chainIdHex.startsWith("0x", ignoreCase = true)) {
                    chainIdHex.substring(2)
                } else {
                    chainIdHex
                }

                val chainId = cleanHex.toLongOrNull(16) ?: return@withContext RpcResult.Error("Invalid chainId format: $chainIdHex")

                if (chainId != ELYSIUM_TESTNET_CHAIN_ID) {
                    return@withContext RpcResult.WrongChain(
                        expected = ELYSIUM_TESTNET_CHAIN_ID,
                        actual = chainId
                    )
                }

                RpcResult.Success(chainId)
            }
        } catch (e: IOException) {
            RpcResult.Error("Network error: ${e.localizedMessage ?: "Failed to connect to RPC"}", e)
        } catch (e: Exception) {
            RpcResult.Error("Unexpected RPC error: ${e.localizedMessage}", e)
        }
    }

    /**
     * Queries the native HYPE balance for the given derived EVM address.
     * Never accepts or transmits private keys or recovery phrases.
     */
    suspend fun getNativeHypeBalance(address: String): RpcResult<TestnetBalance> = withContext(Dispatchers.IO) {
        if (!isValidEvmAddress(address)) {
            return@withContext RpcResult.Error("Invalid EVM address: $address")
        }

        val params = org.json.JSONArray().apply {
            put(address)
            put("latest")
        }

        val payload = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", "eth_getBalance")
            put("params", params)
            put("id", 2)
        }

        val request = Request.Builder()
            .url(rpcUrl)
            .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        try {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext RpcResult.Error("RPC HTTP ${response.code}: ${response.message}")
                }
                val bodyString = response.body?.string() ?: return@withContext RpcResult.Error("Empty RPC response")
                val json = JSONObject(bodyString)

                if (json.has("error")) {
                    val err = json.getJSONObject("error")
                    return@withContext RpcResult.Error("RPC Error: ${err.optString("message", "Unknown error")}")
                }

                val weiHex = json.optString("result", "0x0")
                val cleanHex = if (weiHex.startsWith("0x", ignoreCase = true)) {
                    weiHex.substring(2)
                } else {
                    weiHex
                }
                val wei = if (cleanHex.isBlank()) BigInteger.ZERO else BigInteger(cleanHex, 16)
                val formattedHype = weiHexToFormattedHype(weiHex)

                RpcResult.Success(
                    TestnetBalance(
                        formattedHype = formattedHype,
                        rawWeiHex = weiHex,
                        wei = wei
                    )
                )
            }
        } catch (e: IOException) {
            RpcResult.Error("Network error: ${e.localizedMessage ?: "Failed to connect to RPC"}", e)
        } catch (e: Exception) {
            RpcResult.Error("Unexpected balance query error: ${e.localizedMessage}", e)
        }
    }

    private fun isValidEvmAddress(address: String): Boolean {
        return address.startsWith("0x", ignoreCase = true) && address.length == 42
    }
}
