package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.model.DappBookmark
import com.example.model.DappHistoryItem
import com.example.model.FeaturedDapp
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * Local repository for DApp Browser bookmarks, recently visited history, and curated discovery items.
 * Strictly persists web navigation metadata only — never private keys or recovery phrases.
 */
class BrowserStorageRepository(context: Context) {

    companion object {
        private const val PREFS_NAME = "koro_browser_storage"
        private const val KEY_BOOKMARKS = "dapp_bookmarks_json"
        private const val KEY_HISTORY = "dapp_history_json"

        val DEFAULT_FEATURED_DAPPS = listOf(
            FeaturedDapp(
                id = "kinetiq_explorer",
                name = "Kinetiq Explorer",
                category = "Explore",
                url = "https://elysium.kinetiq.xyz/testnet-explorer",
                description = "Official Elysium Testnet Block Explorer"
            ),
            FeaturedDapp(
                id = "hyperliquid_dex",
                name = "Hyperliquid L1",
                category = "DEX",
                url = "https://app.hyperliquid.xyz",
                description = "High-performance decentralized perpetual exchange"
            ),
            FeaturedDapp(
                id = "uniswap_interface",
                name = "Uniswap Interface",
                category = "DeFi",
                url = "https://app.uniswap.org",
                description = "Decentralized automated liquidity protocol (Sample)"
            ),
            FeaturedDapp(
                id = "aave_protocol",
                name = "Aave V3",
                category = "DeFi",
                url = "https://app.aave.com",
                description = "Non-custodial liquidity market protocol (Sample)"
            ),
            FeaturedDapp(
                id = "opensea_nft",
                name = "OpenSea",
                category = "NFT",
                url = "https://opensea.io",
                description = "Web3 marketplace for NFTs and crypto collectibles (Sample)"
            ),
            FeaturedDapp(
                id = "stargate_bridge",
                name = "Stargate Bridge",
                category = "Bridge",
                url = "https://stargate.finance",
                description = "Fully composable native asset bridge (Sample)"
            ),
            FeaturedDapp(
                id = "sandbox_game",
                name = "The Sandbox",
                category = "Gaming",
                url = "https://www.sandbox.game",
                description = "Decentralized gaming metaverse (Sample)"
            )
        )
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getBookmarks(): List<DappBookmark> {
        val raw = prefs.getString(KEY_BOOKMARKS, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<DappBookmark>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    DappBookmark(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        url = obj.getString("url"),
                        iconUrl = obj.optString("iconUrl", null),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            list.sortedByDescending { it.timestamp }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addBookmark(title: String, url: String): DappBookmark {
        val cleanUrl = url.trim()
        val current = getBookmarks().filterNot { it.url.equals(cleanUrl, ignoreCase = true) }.toMutableList()
        val newBookmark = DappBookmark(
            id = UUID.randomUUID().toString(),
            title = if (title.isBlank()) cleanUrl else title.trim(),
            url = cleanUrl,
            timestamp = System.currentTimeMillis()
        )
        current.add(0, newBookmark)
        saveBookmarks(current)
        return newBookmark
    }

    fun removeBookmark(url: String) {
        val cleanUrl = url.trim()
        val filtered = getBookmarks().filterNot { it.url.equals(cleanUrl, ignoreCase = true) }
        saveBookmarks(filtered)
    }

    fun isBookmarked(url: String): Boolean {
        if (url.isBlank()) return false
        val cleanUrl = url.trim()
        return getBookmarks().any { it.url.equals(cleanUrl, ignoreCase = true) }
    }

    private fun saveBookmarks(bookmarks: List<DappBookmark>) {
        val array = JSONArray()
        for (item in bookmarks) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("url", item.url)
                if (item.iconUrl != null) put("iconUrl", item.iconUrl)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_BOOKMARKS, array.toString()).apply()
    }

    fun getHistory(): List<DappHistoryItem> {
        val raw = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<DappHistoryItem>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    DappHistoryItem(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        url = obj.getString("url"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            list.sortedByDescending { it.timestamp }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun addHistoryItem(title: String, url: String) {
        if (url.isBlank() || url.startsWith("about:", ignoreCase = true)) return
        val cleanUrl = url.trim()
        val current = getHistory().filterNot { it.url.equals(cleanUrl, ignoreCase = true) }.toMutableList()
        val newItem = DappHistoryItem(
            id = UUID.randomUUID().toString(),
            title = if (title.isBlank()) cleanUrl else title.trim(),
            url = cleanUrl,
            timestamp = System.currentTimeMillis()
        )
        current.add(0, newItem)
        // Keep at most 50 recent history items
        val trimmed = if (current.size > 50) current.take(50) else current
        val array = JSONArray()
        for (item in trimmed) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("url", item.url)
                put("timestamp", item.timestamp)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY, array.toString()).apply()
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }
}
