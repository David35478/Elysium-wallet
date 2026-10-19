package com.example

import android.util.Base64
import com.example.core.security.EncryptedRecord
import com.example.core.security.VaultCipher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.nio.charset.StandardCharsets
import javax.crypto.AEADBadTagException
import javax.crypto.KeyGenerator

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class VaultCipherTest {

    private fun generateTestKey() = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()

    @Test
    fun testEncryptionAndDecryptionRoundTrip() {
        val key = generateTestKey()
        val originalSecret = "velvet matrix quantum falcon zenith aurora crystal beacon".toByteArray(StandardCharsets.UTF_8)

        val record = VaultCipher.encrypt(originalSecret, key)

        assertTrue("IV must not be empty", record.ivBase64.isNotEmpty())
        assertTrue("Ciphertext must not be empty", record.ciphertextBase64.isNotEmpty())
        assertNotEquals("Ciphertext must not equal plaintext", String(originalSecret), record.ciphertextBase64)

        val decrypted = VaultCipher.decrypt(record, key)
        assertEquals("Decrypted bytes must match original secret", String(originalSecret, StandardCharsets.UTF_8), String(decrypted, StandardCharsets.UTF_8))
    }

    @Test
    fun testDecryptionFailsWithWrongKey() {
        val correctKey = generateTestKey()
        val wrongKey = generateTestKey()
        val originalSecret = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about".toByteArray(StandardCharsets.UTF_8)

        val record = VaultCipher.encrypt(originalSecret, correctKey)

        assertThrows("Decryption with wrong key must fail due to AEAD tag verification", AEADBadTagException::class.java) {
            VaultCipher.decrypt(record, wrongKey)
        }
    }

    @Test
    fun testCorruptedCiphertextFailsDecryption() {
        val key = generateTestKey()
        val originalSecret = "my super secret mnemonic phrase".toByteArray(StandardCharsets.UTF_8)

        val record = VaultCipher.encrypt(originalSecret, key)

        // Corrupt a byte in the ciphertext
        val rawCiphertext = Base64.decode(record.ciphertextBase64, Base64.NO_WRAP)
        rawCiphertext[0] = (rawCiphertext[0].toInt() xor 0xFF).toByte()
        val corruptedCiphertextBase64 = Base64.encodeToString(rawCiphertext, Base64.NO_WRAP)

        val tamperedRecord = EncryptedRecord(
            ivBase64 = record.ivBase64,
            ciphertextBase64 = corruptedCiphertextBase64
        )

        assertThrows("Tampered ciphertext must fail authentication and throw AEADBadTagException", AEADBadTagException::class.java) {
            VaultCipher.decrypt(tamperedRecord, key)
        }
    }

    @Test
    fun testCorruptedIvFailsDecryption() {
        val key = generateTestKey()
        val originalSecret = "my super secret mnemonic phrase".toByteArray(StandardCharsets.UTF_8)

        val record = VaultCipher.encrypt(originalSecret, key)

        // Corrupt a byte in the IV
        val rawIv = Base64.decode(record.ivBase64, Base64.NO_WRAP)
        rawIv[0] = (rawIv[0].toInt() xor 0x01).toByte()
        val corruptedIvBase64 = Base64.encodeToString(rawIv, Base64.NO_WRAP)

        val tamperedRecord = EncryptedRecord(
            ivBase64 = corruptedIvBase64,
            ciphertextBase64 = record.ciphertextBase64
        )

        assertThrows("Tampered IV must fail authentication and throw AEADBadTagException", AEADBadTagException::class.java) {
            VaultCipher.decrypt(tamperedRecord, key)
        }
    }
}
