package com.example.model

import java.math.BigInteger

data class DappBookmark(
    val id: String,
    val title: String,
    val url: String,
    val iconUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class DappHistoryItem(
    val id: String,
    val title: String,
    val url: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class DappPermission(
    val origin: String,
    val accountAddress: String,
    val grantedTimestamp: Long = System.currentTimeMillis(),
    val permissions: List<String> = listOf("eth_accounts")
)

data class FeaturedDapp(
    val id: String,
    val name: String,
    val category: String,
    val url: String,
    val description: String,
    val isSample: Boolean = true
)

data class DappConnectionRequest(
    val requestId: String,
    val origin: String,
    val hostname: String,
    val iconUrl: String? = null,
    val targetChainId: Long? = null
)

data class DappSignMessageRequest(
    val requestId: String,
    val origin: String,
    val hostname: String,
    val accountAddress: String,
    val rawMessage: String,
    val displayMessage: String,
    val isOpaque: Boolean = false
)

data class DappTransactionRequest(
    val requestId: String,
    val origin: String,
    val hostname: String,
    val fromAddress: String,
    val toAddress: String,
    val valueWei: BigInteger,
    val formattedValue: String,
    val data: String,
    val gasLimit: BigInteger,
    val gasPrice: BigInteger,
    val estimatedFeeFormatted: String,
    val isContractInteraction: Boolean = false
)

data class DappChainSwitchRequest(
    val requestId: String,
    val origin: String,
    val hostname: String,
    val targetChainId: Long,
    val targetNetwork: EvmNetworkConfig?
)
