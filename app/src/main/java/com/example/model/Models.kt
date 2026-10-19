package com.example.model

enum class Screen {
    WELCOME,
    ONBOARDING_LOCAL_PROTECTION,
    ONBOARDING_BACKUP_PHRASE,
    ONBOARDING_VERIFY_PHRASE,
    ONBOARDING_IMPORT,
    ONBOARDING_READY,
    HOME,
    SWAP,
    BROWSER,
    ACTIVITY,
    SETTINGS
}

enum class TokenIconType {
    ETH,
    BNB,
    SOL,
    BTC,
    USDT,
    USDC,
    HYPE
}

enum class ChainIconType {
    BNB,
    ETH,
    HYPERLIQUID,
    SOLANA,
    ARBITRUM
}

data class TokenAsset(
    val id: String,
    val symbol: String,
    val name: String,
    val balance: Double,
    val balanceDisplay: String,
    val fiatPrice: Double,
    val fiatValue: Double,
    val fiatDisplay: String,
    val priceChange24hPct: Double,
    val priceChangeDisplay: String,
    val isPositive: Boolean,
    val chain: String,
    val iconType: TokenIconType
)

data class Chain(
    val id: String,
    val name: String,
    val symbol: String,
    val iconType: ChainIconType,
    val isTestnet: Boolean = false
)

data class WalletAccount(
    val id: String,
    val name: String,
    val shortAddress: String,
    val fullAddress: String,
    val totalBalanceUsd: Double,
    val change24hUsd: Double,
    val change24hPct: Double
)

data class NftItem(
    val id: String,
    val name: String,
    val collection: String,
    val tokenId: String,
    val floorPriceEth: String,
    val gradientPrimaryHex: Long,
    val gradientSecondaryHex: Long
)

enum class TxType {
    SWAP,
    SEND,
    RECEIVE
}

data class TransactionItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val amount: String,
    val fiatAmount: String,
    val status: String,
    val timestamp: String,
    val type: TxType
)
