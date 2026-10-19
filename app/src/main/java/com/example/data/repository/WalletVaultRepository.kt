package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.core.crypto.CryptoManager
import com.example.core.security.AndroidKeystoreVault
import com.example.core.security.EncryptedRecord
import com.example.core.security.KeyVault
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets

data class StoredWalletHeader(
    val id: String,
    val name: String,
    val address: String,
    val shortAddress: String,
    val createdAt: Long,
    val isBackupVerified: Boolean
)

data class DecryptedSecret(
    val mnemonic: String,
    val privateKeyHex: String,
    val derivationPath: String
)

class WalletVaultRepository(
    private val context: Context,
    private val keyVault: KeyVault = AndroidKeystoreVault(context)
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("elysium_wallet_vault", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_WALLETS_METADATA = "wallets_metadata"
        private const val KEY_ACTIVE_WALLET_ID = "active_wallet_id"
        private const val PREFIX_VAULT_RECORD = "vault_record_"
    }

    /**
     * Creates a new wallet from 128-bit cryptographically secure entropy.
     * Returns the generated 12-word recovery phrase (only once for backup flow)
     * and the saved wallet header.
     */
    fun createNewWallet(walletName: String = "Wallet 1"): Pair<StoredWalletHeader, String> {
        val mnemonic = CryptoManager.generate12WordMnemonic()
        val account = CryptoManager.deriveEvmAccount(mnemonic)
        val walletId = "wallet_" + System.currentTimeMillis()

        saveWalletToVault(
            walletId = walletId,
            walletName = walletName,
            address = account.address,
            mnemonic = mnemonic,
            privateKeyHex = account.privateKeyHex,
            derivationPath = account.derivationPath,
            isBackupVerified = false
        )

        val header = StoredWalletHeader(
            id = walletId,
            name = walletName,
            address = account.address,
            shortAddress = formatShortAddress(account.address),
            createdAt = System.currentTimeMillis(),
            isBackupVerified = false
        )

        return Pair(header, mnemonic)
    }

    /**
     * Restores an existing wallet from a verified BIP-39 mnemonic.
     */
    fun restoreWallet(walletName: String, mnemonic: String): Result<StoredWalletHeader> {
        val cleanedMnemonic = CryptoManager.cleanMnemonic(mnemonic)
        if (!CryptoManager.validateMnemonic(cleanedMnemonic)) {
            return Result.failure(IllegalArgumentException("Invalid recovery phrase. Please check the word spelling, order, and count."))
        }

        val account = CryptoManager.deriveEvmAccount(cleanedMnemonic)
        val walletId = "wallet_" + System.currentTimeMillis()

        saveWalletToVault(
            walletId = walletId,
            walletName = walletName,
            address = account.address,
            mnemonic = cleanedMnemonic,
            privateKeyHex = account.privateKeyHex,
            derivationPath = account.derivationPath,
            isBackupVerified = true // Restored from seed phrase, so user already has it
        )

        val header = StoredWalletHeader(
            id = walletId,
            name = walletName,
            address = account.address,
            shortAddress = formatShortAddress(account.address),
            createdAt = System.currentTimeMillis(),
            isBackupVerified = true
        )

        return Result.success(header)
    }

    /**
     * Encrypts and persists wallet secret material into the Keystore-protected vault.
     */
    private fun saveWalletToVault(
        walletId: String,
        walletName: String,
        address: String,
        mnemonic: String,
        privateKeyHex: String,
        derivationPath: String,
        isBackupVerified: Boolean
    ) {
        val secretJson = JSONObject().apply {
            put("mnemonic", mnemonic)
            put("privateKeyHex", privateKeyHex)
            put("derivationPath", derivationPath)
        }

        val encryptedRecord = keyVault.encrypt(secretJson.toString().toByteArray(StandardCharsets.UTF_8))

        // Store encrypted payload
        val vaultPayload = JSONObject().apply {
            put("iv", encryptedRecord.ivBase64)
            put("ciphertext", encryptedRecord.ciphertextBase64)
        }
        prefs.edit().putString(PREFIX_VAULT_RECORD + walletId, vaultPayload.toString()).apply()

        // Update wallet headers list
        val headers = getWalletHeaders().toMutableList()
        val newHeader = StoredWalletHeader(
            id = walletId,
            name = walletName,
            address = address,
            shortAddress = formatShortAddress(address),
            createdAt = System.currentTimeMillis(),
            isBackupVerified = isBackupVerified
        )
        headers.add(newHeader)
        saveWalletHeaders(headers)
        setActiveWalletId(walletId)
    }

    /**
     * Decrypts wallet secret material using the Android Keystore master key.
     * Throws SecurityException if decryption or authentication fails (tampering/corruption).
     */
    fun decryptSecretPayload(walletId: String): Result<DecryptedSecret> {
        val recordJsonStr = prefs.getString(PREFIX_VAULT_RECORD + walletId, null)
            ?: return Result.failure(IllegalStateException("No encrypted record found for wallet $walletId"))

        return try {
            val recordJson = JSONObject(recordJsonStr)
            val iv = recordJson.getString("iv")
            val ciphertext = recordJson.getString("ciphertext")

            val decryptedBytes = keyVault.decrypt(EncryptedRecord(ivBase64 = iv, ciphertextBase64 = ciphertext))
            val secretJson = JSONObject(String(decryptedBytes, StandardCharsets.UTF_8))

            Result.success(
                DecryptedSecret(
                    mnemonic = secretJson.getString("mnemonic"),
                    privateKeyHex = secretJson.getString("privateKeyHex"),
                    derivationPath = secretJson.getString("derivationPath")
                )
            )
        } catch (e: Exception) {
            Result.failure(SecurityException("Vault decryption failed: authenticated data may be corrupted or key mismatch.", e))
        }
    }

    fun getWalletHeaders(): List<StoredWalletHeader> {
        val jsonStr = prefs.getString(KEY_WALLETS_METADATA, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<StoredWalletHeader>()
            for (i in 0 until jsonArray.length()) {
                val item = jsonArray.getJSONObject(i)
                list.add(
                    StoredWalletHeader(
                        id = item.getString("id"),
                        name = item.getString("name"),
                        address = item.getString("address"),
                        shortAddress = item.getString("shortAddress"),
                        createdAt = item.getLong("createdAt"),
                        isBackupVerified = item.optBoolean("isBackupVerified", false)
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveWalletHeaders(headers: List<StoredWalletHeader>) {
        val jsonArray = JSONArray()
        for (h in headers) {
            val obj = JSONObject().apply {
                put("id", h.id)
                put("name", h.name)
                put("address", h.address)
                put("shortAddress", h.shortAddress)
                put("createdAt", h.createdAt)
                put("isBackupVerified", h.isBackupVerified)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_WALLETS_METADATA, jsonArray.toString()).apply()
    }

    fun markBackupVerified(walletId: String) {
        val updated = getWalletHeaders().map {
            if (it.id == walletId) it.copy(isBackupVerified = true) else it
        }
        saveWalletHeaders(updated)
    }

    fun getActiveWalletId(): String? {
        return prefs.getString(KEY_ACTIVE_WALLET_ID, null)
    }

    fun setActiveWalletId(id: String) {
        prefs.edit().putString(KEY_ACTIVE_WALLET_ID, id).apply()
    }

    private fun formatShortAddress(address: String): String {
        return if (address.length >= 10) {
            "${address.take(6)}...${address.takeLast(4)}"
        } else {
            address
        }
    }
}
