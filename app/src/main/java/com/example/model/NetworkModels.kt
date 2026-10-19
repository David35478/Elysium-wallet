package com.example.model

/**
 * Authoritative, immutable EVM network configuration object.
 * Used across the network registry, RPC client, UI badges, and transaction signing.
 */
data class EvmNetworkConfig(
    val id: String,
    val name: String,
    val chainId: Long,
    val chainIdHex: String,
    val rpcUrl: String,
    val nativeCurrencyName: String,
    val nativeCurrencySymbol: String,
    val nativeCurrencyDecimals: Int = 18,
    val explorerUrl: String,
    val isTestnet: Boolean = true,
    val isEnabled: Boolean = true,
    val iconType: ChainIconType = ChainIconType.HYPERLIQUID
)

enum class NetworkConnectionStatus {
    CONNECTED,
    CHECKING,
    WRONG_CHAIN,
    UNAVAILABLE
}

data class NetworkBalance(
    val formattedBalance: String,
    val rawWeiHex: String,
    val wei: java.math.BigInteger,
    val symbol: String,
    val chainId: Long,
    val blockNumber: Long? = null
)
