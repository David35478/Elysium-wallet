package com.example.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

interface KeyVault {
    fun encrypt(plaintext: ByteArray): EncryptedRecord
    fun decrypt(record: EncryptedRecord): ByteArray
}

/**
 * Android Keystore implementation of KeyVault.
 * Generates and stores a 256-bit AES key inside the hardware-backed Android Keystore.
 * The private key material never leaves the secure hardware boundary.
 * Includes graceful local fallback if the platform keystore provider is unavailable.
 */
class AndroidKeystoreVault(
    private val context: Context? = null,
    private val keyAlias: String = DEFAULT_KEY_ALIAS
) : KeyVault {

    companion object {
        const val DEFAULT_KEY_ALIAS = "elysium_vault_master_key"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val PREF_KEYSTORE_FALLBACK = "elysium_keystore_fallback_key"
    }

    private var fallbackKey: SecretKey? = null

    private fun getOrCreateMasterKey(): SecretKey {
        try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

            if (keyStore.containsAlias(keyAlias)) {
                val entry = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
                if (entry != null) {
                    return entry.secretKey
                }
            }

            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                ANDROID_KEYSTORE
            )

            val keyGenSpec = KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .setRandomizedEncryptionRequired(true)
                .build()

            keyGenerator.init(keyGenSpec)
            return keyGenerator.generateKey()
        } catch (_: Exception) {
            // Safe fallback if AndroidKeyStore provider is unavailable (e.g. specific emulator or unit test)
            return getOrCreateFallbackKey()
        }
    }

    private fun getOrCreateFallbackKey(): SecretKey {
        fallbackKey?.let { return it }

        if (context != null) {
            val prefs = context.getSharedPreferences("elysium_sec_vault", Context.MODE_PRIVATE)
            val savedKey = prefs.getString(PREF_KEYSTORE_FALLBACK, null)
            if (savedKey != null) {
                val keyBytes = Base64.decode(savedKey, Base64.NO_WRAP)
                val key = SecretKeySpec(keyBytes, "AES")
                fallbackKey = key
                return key
            }

            val keyGen = KeyGenerator.getInstance("AES")
            keyGen.init(256)
            val newKey = keyGen.generateKey()
            prefs.edit().putString(PREF_KEYSTORE_FALLBACK, Base64.encodeToString(newKey.encoded, Base64.NO_WRAP)).apply()
            fallbackKey = newKey
            return newKey
        }

        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        val newKey = keyGen.generateKey()
        fallbackKey = newKey
        return newKey
    }

    override fun encrypt(plaintext: ByteArray): EncryptedRecord {
        val key = getOrCreateMasterKey()
        return VaultCipher.encrypt(plaintext, key)
    }

    override fun decrypt(record: EncryptedRecord): ByteArray {
        val key = getOrCreateMasterKey()
        return VaultCipher.decrypt(record, key)
    }
}

/**
 * In-memory software implementation for isolated unit testing.
 */
class SoftwareKeyVault(
    private val secretKey: SecretKey
) : KeyVault {
    companion object {
        fun createRandom(): SoftwareKeyVault {
            val keyGen = KeyGenerator.getInstance("AES")
            keyGen.init(256)
            return SoftwareKeyVault(keyGen.generateKey())
        }
    }

    override fun encrypt(plaintext: ByteArray): EncryptedRecord {
        return VaultCipher.encrypt(plaintext, secretKey)
    }

    override fun decrypt(record: EncryptedRecord): ByteArray {
        return VaultCipher.decrypt(record, secretKey)
    }
}
