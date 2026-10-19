package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.example.model.DappPermission
import org.json.JSONArray
import org.json.JSONObject

/**
 * Local repository for DApp connection permissions scoped to (origin, accountAddress).
 * Manages approved origins and provides revocation controls.
 */
class DappPermissionRepository(context: Context) {

    companion object {
        private const val PREFS_NAME = "koro_dapp_permissions"
        private const val KEY_PERMISSIONS = "approved_permissions_json"

        /**
         * Normalizes and validates a web URL into its security origin (scheme://host:port).
         * Restricts to valid HTTPS schemes or local development hosts (localhost / 127.0.0.1).
         * Rejects unsafe schemes: javascript:, file:, data:, content:, about:, etc.
         */
        fun extractOrigin(url: String): String? {
            val trimmed = url.trim()
            if (trimmed.isBlank()) return null
            return try {
                val uri = Uri.parse(trimmed)
                val scheme = uri.scheme?.lowercase() ?: return null
                val host = uri.host?.lowercase() ?: return null

                // Validate safe schemes
                if (scheme == "https") {
                    val portPart = if (uri.port != -1 && uri.port != 443) ":${uri.port}" else ""
                    "https://$host$portPart"
                } else if (scheme == "http" && (host == "localhost" || host == "127.0.0.1")) {
                    val portPart = if (uri.port != -1 && uri.port != 80) ":${uri.port}" else ""
                    "http://$host$portPart"
                } else {
                    null // Ineligible or unsafe scheme
                }
            } catch (_: Exception) {
                null
            }
        }

        fun extractHostname(url: String): String {
            return try {
                Uri.parse(url).host ?: url
            } catch (_: Exception) {
                url
            }
        }
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getAllPermissions(): List<DappPermission> {
        val raw = prefs.getString(KEY_PERMISSIONS, null) ?: return emptyList()
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<DappPermission>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val permissionsArray = obj.optJSONArray("permissions")
                val perms = mutableListOf<String>()
                if (permissionsArray != null) {
                    for (j in 0 until permissionsArray.length()) {
                        perms.add(permissionsArray.getString(j))
                    }
                } else {
                    perms.add("eth_accounts")
                }
                list.add(
                    DappPermission(
                        origin = obj.getString("origin"),
                        accountAddress = obj.getString("accountAddress"),
                        grantedTimestamp = obj.optLong("grantedTimestamp", System.currentTimeMillis()),
                        permissions = perms
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun isOriginApproved(origin: String, accountAddress: String): Boolean {
        val cleanOrigin = extractOrigin(origin) ?: return false
        val cleanAddress = accountAddress.trim().lowercase()
        return getAllPermissions().any {
            it.origin.equals(cleanOrigin, ignoreCase = true) &&
                it.accountAddress.trim().lowercase() == cleanAddress
        }
    }

    fun grantPermission(origin: String, accountAddress: String, permissions: List<String> = listOf("eth_accounts")) {
        val cleanOrigin = extractOrigin(origin) ?: return
        val cleanAddress = accountAddress.trim().lowercase()
        val current = getAllPermissions().filterNot {
            it.origin.equals(cleanOrigin, ignoreCase = true) &&
                it.accountAddress.trim().lowercase() == cleanAddress
        }.toMutableList()

        current.add(
            DappPermission(
                origin = cleanOrigin,
                accountAddress = accountAddress.trim(),
                grantedTimestamp = System.currentTimeMillis(),
                permissions = permissions
            )
        )
        savePermissions(current)
    }

    fun revokePermission(origin: String, accountAddress: String) {
        val cleanOrigin = extractOrigin(origin) ?: origin.trim()
        val cleanAddress = accountAddress.trim().lowercase()
        val filtered = getAllPermissions().filterNot {
            it.origin.equals(cleanOrigin, ignoreCase = true) &&
                it.accountAddress.trim().lowercase() == cleanAddress
        }
        savePermissions(filtered)
    }

    fun revokeOrigin(origin: String) {
        val cleanOrigin = extractOrigin(origin) ?: origin.trim()
        val filtered = getAllPermissions().filterNot {
            it.origin.equals(cleanOrigin, ignoreCase = true)
        }
        savePermissions(filtered)
    }

    fun clearAllPermissions() {
        prefs.edit().remove(KEY_PERMISSIONS).apply()
    }

    private fun savePermissions(list: List<DappPermission>) {
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("origin", item.origin)
                put("accountAddress", item.accountAddress)
                put("grantedTimestamp", item.grantedTimestamp)
                val permsArray = JSONArray()
                item.permissions.forEach { permsArray.put(it) }
                put("permissions", permsArray)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_PERMISSIONS, array.toString()).apply()
    }
}
