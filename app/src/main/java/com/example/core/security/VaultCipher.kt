package com.example.core.security

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

data class EncryptedRecord(
    val ivBase64: String,
    val ciphertextBase64: String
)

/**
 * Pure AES-256-GCM authenticated cipher engine.
 * Protects both confidentiality and integrity with a 128-bit authentication tag.
 * Any bit flipped in the ciphertext or IV causes an immediate decryption failure.
 */
object VaultCipher {

    private const val GCM_IV_LENGTH_BYTES = 12
    private const val GCM_TAG_LENGTH_BITS = 128
    private const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"

    private val secureRandom = SecureRandom()

    fun encrypt(plaintext: ByteArray, secretKey: SecretKey): EncryptedRecord {
        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)

        // For Android Keystore keys with randomized encryption required,
        // do not pass GCMParameterSpec in ENCRYPT_MODE; let Keystore generate IV automatically.
        val iv: ByteArray = try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey)
            cipher.iv ?: ByteArray(GCM_IV_LENGTH_BYTES).also { secureRandom.nextBytes(it) }
        } catch (_: Exception) {
            // Fallback for software keys requiring explicit parameters
            val randomIv = ByteArray(GCM_IV_LENGTH_BYTES)
            secureRandom.nextBytes(randomIv)
            val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, randomIv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)
            randomIv
        }

        val ciphertextWithTag = cipher.doFinal(plaintext)

        val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)
        val ciphertextBase64 = Base64.encodeToString(ciphertextWithTag, Base64.NO_WRAP)

        return EncryptedRecord(ivBase64 = ivBase64, ciphertextBase64 = ciphertextBase64)
    }

    fun decrypt(record: EncryptedRecord, secretKey: SecretKey): ByteArray {
        val iv = Base64.decode(record.ivBase64, Base64.NO_WRAP)
        val ciphertextWithTag = Base64.decode(record.ciphertextBase64, Base64.NO_WRAP)

        val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
        val spec = GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        return cipher.doFinal(ciphertextWithTag)
    }
}
