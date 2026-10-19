package com.example.core.network

import com.example.model.EvmNetworkConfig
import com.example.model.NetworkBalance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.math.BigDecimal
import java.math.BigInteger
import java.math.RoundingMode
import java.util.concurrent.TimeUnit

/**
 * Reusable EVM JSON-RPC client.
 * Connects to any standard EVM RPC endpoint configured in EvmNetworkConfig.
 * Strictly uses BigInteger / BigDecimal for raw wei conversions (zero floating-point math),
 * enforces bounded 10-second timeouts, and guarantees zero leakage of secret keys.
 */
class EvmRpcClient(
    val networkConfig: EvmNetworkConfig = NetworkRegistry.ELYSIUM_TESTNET,
    private val httpClient: OkHttpClient = defaultHttpClient
) {
    companion object {
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
         * Converts hexadecimal wei string to a human-readable decimal string
         * with exact arithmetic, up to 6 displayed decimals, stripping trailing zeros.
         */
        fun weiToFormattedAmount(weiHex: String, decimals: Int = 18): String {
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
            val divisor = BigDecimal.TEN.pow(decimals)
            val tokenValue = weiBigDecimal.divide(divisor, 6, RoundingMode.DOWN).stripTrailingZeros()
            val plain = tokenValue.toPlainString()
            return if (!plain.contains('.')) "$plain.0" else plain
        }

        fun parseHexQuantityToLong(hex: String): Long? {
            val clean = if (hex.startsWith("0x", ignoreCase = true)) hex.substring(2) else hex
            return clean.toLongOrNull(16)
        }

        fun parseHexQuantityToBigInteger(hex: String): BigInteger? {
            val clean = if (hex.startsWith("0x", ignoreCase = true)) hex.substring(2) else hex
            return try {
                if (clean.isBlank()) BigInteger.ZERO else BigInteger(clean, 16)
            } catch (_: Exception) {
                null
            }
        }
    }

    val rpcUrl: String get() = networkConfig.rpcUrl

    /**
     * Executes standard JSON-RPC 2.0 call.
     */
    private suspend fun executeRpcCall(method: String, params: JSONArray, id: Int = 1): RpcResult<JSONObject> = withContext(Dispatchers.IO) {
        val payload = JSONObject().apply {
            put("jsonrpc", "2.0")
            put("method", method)
            put("params", params)
            put("id", id)
        }

        val request = Request.Builder()
            .url(networkConfig.rpcUrl)
            .post(payload.toString().toRequestBody(JSON_MEDIA_TYPE))
            .build()

        try {
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext RpcResult.Error("RPC HTTP ${response.code}: ${response.message}")
                }
                val bodyString = response.body?.string() ?: return@withContext RpcResult.Error("Empty RPC response body")
                val json = JSONObject(bodyString)

                if (json.has("error")) {
                    val err = json.getJSONObject("error")
                    return@withContext RpcResult.Error("RPC Error: ${err.optString("message", "Unknown RPC error")}")
                }

                RpcResult.Success(json)
            }
        } catch (e: IOException) {
            RpcResult.Error("Network error: ${e.localizedMessage ?: "Failed to connect to ${networkConfig.name}"}", e)
        } catch (e: Exception) {
            RpcResult.Error("Unexpected RPC error: ${e.localizedMessage}", e)
        }
    }

    /**
     * Calls "eth_chainId", converts hexadecimal result, and verifies against configured chain ID.
     */
    suspend fun verifyChainId(): RpcResult<Long> {
        val callResult = executeRpcCall("eth_chainId", JSONArray(), 1)
        if (callResult !is RpcResult.Success) {
            return callResult as RpcResult<Long>
        }

        val chainIdHex = callResult.data.optString("result", "")
        val chainId = parseHexQuantityToLong(chainIdHex)
            ?: return RpcResult.Error("Invalid chainId format in RPC response: '$chainIdHex'")

        if (chainId != networkConfig.chainId) {
            return RpcResult.WrongChain(
                expected = networkConfig.chainId,
                actual = chainId
            )
        }

        return RpcResult.Success(chainId)
    }

    /**
     * Calls "eth_blockNumber" to verify endpoint liveness and retrieve latest block height.
     */
    suspend fun getLatestBlockNumber(): RpcResult<Long> {
        val callResult = executeRpcCall("eth_blockNumber", JSONArray(), 2)
        if (callResult !is RpcResult.Success) {
            return callResult as RpcResult<Long>
        }

        val blockHex = callResult.data.optString("result", "0x0")
        val blockNumber = parseHexQuantityToLong(blockHex)
            ?: return RpcResult.Error("Invalid blockNumber hex: '$blockHex'")

        return RpcResult.Success(blockNumber)
    }

    /**
     * Queries native asset balance for the given derived EVM address.
     * Uses eth_getBalance(address, "latest") and converts wei to native currency using configured decimals.
     */
    suspend fun getBalance(address: String): RpcResult<NetworkBalance> {
        if (!isValidEvmAddress(address)) {
            return RpcResult.Error("Invalid EVM address: $address")
        }

        val params = JSONArray().apply {
            put(address)
            put("latest")
        }

        val callResult = executeRpcCall("eth_getBalance", params, 3)
        if (callResult !is RpcResult.Success) {
            return callResult as RpcResult<NetworkBalance>
        }

        val weiHex = callResult.data.optString("result", "0x0")
        val wei = parseHexQuantityToBigInteger(weiHex) ?: BigInteger.ZERO
        val formatted = weiToFormattedAmount(weiHex, networkConfig.nativeCurrencyDecimals)

        return RpcResult.Success(
            NetworkBalance(
                formattedBalance = formatted,
                rawWeiHex = weiHex,
                wei = wei,
                symbol = networkConfig.nativeCurrencySymbol,
                chainId = networkConfig.chainId
            )
        )
    }

    /**
     * Calls "eth_getTransactionCount" for nonce calculation.
     */
    suspend fun getTransactionCount(address: String): RpcResult<BigInteger> {
        if (!isValidEvmAddress(address)) {
            return RpcResult.Error("Invalid EVM address: $address")
        }
        val params = JSONArray().apply {
            put(address)
            put("latest")
        }
        val callResult = executeRpcCall("eth_getTransactionCount", params, 4)
        if (callResult !is RpcResult.Success) {
            return callResult as RpcResult<BigInteger>
        }
        val nonceHex = callResult.data.optString("result", "0x0")
        val nonce = parseHexQuantityToBigInteger(nonceHex) ?: BigInteger.ZERO
        return RpcResult.Success(nonce)
    }

    /**
     * Calls "eth_call" for read-only smart contract interaction.
     */
    suspend fun call(to: String, data: String): RpcResult<String> {
        val txObj = JSONObject().apply {
            put("to", to)
            put("data", data)
        }
        val params = JSONArray().apply {
            put(txObj)
            put("latest")
        }
        val callResult = executeRpcCall("eth_call", params, 5)
        if (callResult !is RpcResult.Success) {
            return callResult as RpcResult<String>
        }
        val resultHex = callResult.data.optString("result", "0x")
        return RpcResult.Success(resultHex)
    }

    /**
     * Calls "eth_estimateGas" to simulate execution gas.
     */
    suspend fun estimateGas(to: String, data: String, from: String? = null): RpcResult<BigInteger> {
        val txObj = JSONObject().apply {
            put("to", to)
            put("data", data)
            if (from != null) put("from", from)
        }
        val params = JSONArray().apply {
            put(txObj)
        }
        val callResult = executeRpcCall("eth_estimateGas", params, 6)
        if (callResult !is RpcResult.Success) {
            return callResult as RpcResult<BigInteger>
        }
        val gasHex = callResult.data.optString("result", "0x5208") // 21000 default
        val gas = parseHexQuantityToBigInteger(gasHex) ?: BigInteger.valueOf(21000)
        return RpcResult.Success(gas)
    }

    /**
     * Calls "eth_getTransactionReceipt" to inspect confirmed transaction status.
     */
    suspend fun getTransactionReceipt(txHash: String): RpcResult<String?> {
        val params = JSONArray().apply {
            put(txHash)
        }
        val callResult = executeRpcCall("eth_getTransactionReceipt", params, 7)
        if (callResult !is RpcResult.Success) {
            return callResult as RpcResult<String?>
        }
        val result = if (callResult.data.isNull("result")) null else callResult.data.optString("result", null)
        return RpcResult.Success(result)
    }

    /**
     * Validates network chain ID again immediately before broadcasting a signed transaction.
     */
    suspend fun sendRawTransaction(signedTxHex: String): RpcResult<String> {
        // Re-validate chain ID immediately prior to submission
        val chainCheck = verifyChainId()
        if (chainCheck !is RpcResult.Success) {
            return RpcResult.Error("Safety check failed: Network chain ID could not be confirmed before broadcasting")
        }

        val params = JSONArray().apply {
            put(signedTxHex)
        }
        val callResult = executeRpcCall("eth_sendRawTransaction", params, 8)
        if (callResult !is RpcResult.Success) {
            return callResult as RpcResult<String>
        }
        val txHash = callResult.data.optString("result", "")
        if (txHash.isBlank()) {
            return RpcResult.Error("No transaction hash returned by node")
        }
        return RpcResult.Success(txHash)
    }

    private fun isValidEvmAddress(address: String): Boolean {
        return address.startsWith("0x", ignoreCase = true) && address.length == 42
    }
}
