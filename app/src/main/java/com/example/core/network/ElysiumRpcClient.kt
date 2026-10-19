package com.example.core.network

import com.example.model.EvmNetworkConfig
import okhttp3.OkHttpClient
import java.math.BigInteger

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
 * Client for interacting specifically with Kinetiq Elysium Testnet (Chain ID 99801 / 0x185d9).
 * Uses official RPC: https://testnet-rpc.elysium.kinetiq.xyz
 * Delegates to the unified EvmRpcClient architecture.
 */
class ElysiumRpcClient(
    val rpcUrl: String = DEFAULT_RPC_URL,
    httpClient: OkHttpClient? = null
) {
    companion object {
        const val ELYSIUM_TESTNET_CHAIN_ID = 99801L
        const val ELYSIUM_TESTNET_CHAIN_ID_HEX = "0x185d9"
        const val DEFAULT_RPC_URL = "https://testnet-rpc.elysium.kinetiq.xyz"

        fun weiHexToFormattedHype(weiHex: String): String {
            return EvmRpcClient.weiToFormattedAmount(weiHex, 18)
        }
    }

    private val evmNetwork = EvmNetworkConfig(
        id = "elysium_testnet",
        name = "Elysium Testnet",
        chainId = ELYSIUM_TESTNET_CHAIN_ID,
        chainIdHex = ELYSIUM_TESTNET_CHAIN_ID_HEX,
        rpcUrl = rpcUrl,
        nativeCurrencyName = "HYPE",
        nativeCurrencySymbol = "HYPE",
        nativeCurrencyDecimals = 18,
        explorerUrl = "https://elysium.kinetiq.xyz/testnet-explorer",
        isTestnet = true,
        isEnabled = true
    )

    private val delegateClient = if (httpClient != null) {
        EvmRpcClient(evmNetwork, httpClient)
    } else {
        EvmRpcClient(evmNetwork)
    }

    suspend fun verifyChainId(): RpcResult<Long> = delegateClient.verifyChainId()

    suspend fun getLatestBlockNumber(): RpcResult<Long> = delegateClient.getLatestBlockNumber()

    suspend fun getNativeHypeBalance(address: String): RpcResult<TestnetBalance> {
        val balanceResult = delegateClient.getBalance(address)
        return when (balanceResult) {
            is RpcResult.Success -> {
                val data = balanceResult.data
                RpcResult.Success(
                    TestnetBalance(
                        formattedHype = data.formattedBalance,
                        rawWeiHex = data.rawWeiHex,
                        wei = data.wei
                    )
                )
            }
            is RpcResult.WrongChain -> RpcResult.WrongChain(balanceResult.expected, balanceResult.actual)
            is RpcResult.Error -> RpcResult.Error(balanceResult.message, balanceResult.throwable)
        }
    }
}
