package com.example.core.network

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ChainIconType
import com.example.model.EvmNetworkConfig
import org.json.JSONArray
import org.json.JSONObject

/**
 * Centralized, authoritative registry of supported EVM networks.
 * Serves as the single source of truth for network selection, RPC validation,
 * balance fetching, explorer URLs, and transaction safety.
 */
class NetworkRegistry(context: Context? = null) {

    companion object {
        private const val PREFS_NAME = "elysium_network_prefs"
        private const val KEY_SELECTED_NETWORK_ID = "selected_network_id"
        private const val KEY_CUSTOM_NETWORKS = "custom_networks_json"

        /**
         * Built-in Elysium Testnet (Kinetiq Elysium Testnet, Chain ID 99801 / 0x185d9).
         * Primary and default network of Elysium Wallet.
         */
        val ELYSIUM_TESTNET = EvmNetworkConfig(
            id = "elysium_testnet",
            name = "Elysium Testnet",
            chainId = 99801L,
            chainIdHex = "0x185d9",
            rpcUrl = "https://testnet-rpc.elysium.kinetiq.xyz",
            nativeCurrencyName = "HYPE",
            nativeCurrencySymbol = "HYPE",
            nativeCurrencyDecimals = 18,
            explorerUrl = "https://elysium.kinetiq.xyz/testnet-explorer",
            isTestnet = true,
            isEnabled = true,
            iconType = ChainIconType.HYPERLIQUID
        )

        /**
         * Built-in HyperEVM Testnet (Chain ID 998 / 0x3e6).
         * Distinct parent network from Elysium Testnet.
         */
        val HYPEREVM_TESTNET = EvmNetworkConfig(
            id = "hyperevm_testnet",
            name = "HyperEVM Testnet",
            chainId = 998L,
            chainIdHex = "0x3e6",
            rpcUrl = "https://rpc.hyperliquid-testnet.xyz/evm",
            nativeCurrencyName = "HYPE",
            nativeCurrencySymbol = "HYPE",
            nativeCurrencyDecimals = 18,
            explorerUrl = "https://testnet.purrsec.com",
            isTestnet = true,
            isEnabled = true,
            iconType = ChainIconType.HYPERLIQUID
        )

        /**
         * Ethereum Sepolia Testnet (Chain ID 11155111 / 0xaa36a7).
         */
        val SEPOLIA_TESTNET = EvmNetworkConfig(
            id = "sepolia_testnet",
            name = "Sepolia Testnet",
            chainId = 11155111L,
            chainIdHex = "0xaa36a7",
            rpcUrl = "https://rpc.sepolia.org",
            nativeCurrencyName = "Ethereum",
            nativeCurrencySymbol = "ETH",
            nativeCurrencyDecimals = 18,
            explorerUrl = "https://sepolia.etherscan.io",
            isTestnet = true,
            isEnabled = true,
            iconType = ChainIconType.ETH
        )

        /**
         * Arbitrum Sepolia Testnet (Chain ID 421614 / 0x66eee).
         */
        val ARBITRUM_SEPOLIA = EvmNetworkConfig(
            id = "arbitrum_sepolia",
            name = "Arbitrum Sepolia",
            chainId = 421614L,
            chainIdHex = "0x66eee",
            rpcUrl = "https://sepolia-rollup.arbitrum.io/rpc",
            nativeCurrencyName = "Ethereum",
            nativeCurrencySymbol = "ETH",
            nativeCurrencyDecimals = 18,
            explorerUrl = "https://sepolia.arbiscan.io",
            isTestnet = true,
            isEnabled = true,
            iconType = ChainIconType.ARBITRUM
        )

        val BUILT_IN_NETWORKS: List<EvmNetworkConfig> = listOf(
            ELYSIUM_TESTNET,
            HYPEREVM_TESTNET,
            SEPOLIA_TESTNET,
            ARBITRUM_SEPOLIA
        )
    }

    private val prefs: SharedPreferences? = context?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Returns the currently selected network configuration.
     * Defaults to Elysium Testnet (99801).
     */
    fun getSelectedNetwork(): EvmNetworkConfig {
        val selectedId = prefs?.getString(KEY_SELECTED_NETWORK_ID, ELYSIUM_TESTNET.id) ?: ELYSIUM_TESTNET.id
        return getNetworkById(selectedId) ?: ELYSIUM_TESTNET
    }

    /**
     * Persists the selected network ID separately from wallet identity.
     */
    fun setSelectedNetworkId(networkId: String): Boolean {
        val network = getNetworkById(networkId) ?: return false
        prefs?.edit()?.putString(KEY_SELECTED_NETWORK_ID, network.id)?.apply()
        return true
    }

    /**
     * Returns all registered networks (built-in and valid custom networks).
     */
    fun getAllNetworks(): List<EvmNetworkConfig> {
        val custom = getCustomNetworks()
        return BUILT_IN_NETWORKS + custom
    }

    /**
     * Looks up a registered network by its unique identifier.
     */
    fun getNetworkById(id: String): EvmNetworkConfig? {
        return getAllNetworks().find { it.id.equals(id, ignoreCase = true) }
    }

    /**
     * Looks up a registered network by its chain ID.
     */
    fun getNetworkByChainId(chainId: Long): EvmNetworkConfig? {
        return getAllNetworks().find { it.chainId == chainId }
    }

    /**
     * Validates and registers a new custom EVM network.
     * Rejects invalid URLs, negative/zero chain IDs, and duplicate IDs or chain IDs.
     */
    fun registerCustomNetwork(network: EvmNetworkConfig): Result<EvmNetworkConfig> {
        if (network.id.isBlank()) {
            return Result.failure(IllegalArgumentException("Network identifier cannot be blank"))
        }
        if (!network.rpcUrl.startsWith("https://") && !network.rpcUrl.startsWith("http://")) {
            return Result.failure(IllegalArgumentException("RPC URL must start with http:// or https://"))
        }
        if (network.chainId <= 0) {
            return Result.failure(IllegalArgumentException("Chain ID must be a positive integer"))
        }
        if (getAllNetworks().any { it.id.equals(network.id, ignoreCase = true) }) {
            return Result.failure(IllegalArgumentException("Network with ID '${network.id}' already exists"))
        }
        if (getAllNetworks().any { it.chainId == network.chainId }) {
            return Result.failure(IllegalArgumentException("Network with Chain ID ${network.chainId} already exists"))
        }

        saveCustomNetwork(network)
        return Result.success(network)
    }

    private fun getCustomNetworks(): List<EvmNetworkConfig> {
        val jsonStr = prefs?.getString(KEY_CUSTOM_NETWORKS, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<EvmNetworkConfig>()
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                list.add(
                    EvmNetworkConfig(
                        id = item.getString("id"),
                        name = item.getString("name"),
                        chainId = item.getLong("chainId"),
                        chainIdHex = item.optString("chainIdHex", "0x" + item.getLong("chainId").toString(16)),
                        rpcUrl = item.getString("rpcUrl"),
                        nativeCurrencyName = item.getString("nativeCurrencyName"),
                        nativeCurrencySymbol = item.getString("nativeCurrencySymbol"),
                        nativeCurrencyDecimals = item.optInt("nativeCurrencyDecimals", 18),
                        explorerUrl = item.optString("explorerUrl", ""),
                        isTestnet = item.optBoolean("isTestnet", true),
                        isEnabled = item.optBoolean("isEnabled", true),
                        iconType = ChainIconType.HYPERLIQUID
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveCustomNetwork(network: EvmNetworkConfig) {
        val currentList = getCustomNetworks().toMutableList()
        currentList.add(network)
        val jsonArray = JSONArray()
        for (item in currentList) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("name", item.name)
                put("chainId", item.chainId)
                put("chainIdHex", item.chainIdHex)
                put("rpcUrl", item.rpcUrl)
                put("nativeCurrencyName", item.nativeCurrencyName)
                put("nativeCurrencySymbol", item.nativeCurrencySymbol)
                put("nativeCurrencyDecimals", item.nativeCurrencyDecimals)
                put("explorerUrl", item.explorerUrl)
                put("isTestnet", item.isTestnet)
                put("isEnabled", item.isEnabled)
            }
            jsonArray.put(obj)
        }
        prefs?.edit()?.putString(KEY_CUSTOM_NETWORKS, jsonArray.toString())?.apply()
    }
}
