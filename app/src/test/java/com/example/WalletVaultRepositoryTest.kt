package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.security.SoftwareKeyVault
import com.example.data.repository.WalletVaultRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class WalletVaultRepositoryTest {

    private lateinit var context: Context
    private lateinit var keyVault: SoftwareKeyVault
    private lateinit var repository: WalletVaultRepository

    private val testMnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
    private val expectedAddress = "0x9858EfFD232B4033E47d90003D41EC34EcaEda94"

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Clear shared preferences
        context.getSharedPreferences("elysium_wallet_vault", Context.MODE_PRIVATE).edit().clear().commit()
        keyVault = SoftwareKeyVault.createRandom()
        repository = WalletVaultRepository(context, keyVault)
    }

    @Test
    fun testCreateNewWalletStoresEncryptedPayloadAndHeader() {
        val (header, mnemonic) = repository.createNewWallet("Primary Account")

        assertNotNull(header.id)
        assertEquals("Primary Account", header.name)
        assertTrue("Address must start with 0x", header.address.startsWith("0x"))
        assertFalse("Newly created wallet requires backup verification", header.isBackupVerified)

        val storedList = repository.getWalletHeaders()
        assertEquals(1, storedList.size)
        assertEquals(header.id, storedList.first().id)

        // Decrypt from vault
        val decryptResult = repository.decryptSecretPayload(header.id)
        assertTrue("Decryption must succeed", decryptResult.isSuccess)
        val secret = decryptResult.getOrThrow()
        assertEquals(mnemonic, secret.mnemonic)
        assertEquals("m/44'/60'/0'/0/0", secret.derivationPath)
    }

    @Test
    fun testRestoreWalletStoresCorrectAddressAndMnemonic() {
        val result = repository.restoreWallet("Restored Vault", testMnemonic)
        assertTrue("Restoration must succeed", result.isSuccess)
        val header = result.getOrThrow()

        assertEquals("Restored Vault", header.name)
        assertEquals("Restored address must match standard vector", expectedAddress, header.address)
        assertTrue("Restored wallet is marked as backup-verified", header.isBackupVerified)

        // Verify stored payload in vault
        val decryptResult = repository.decryptSecretPayload(header.id)
        assertTrue("Vault decryption must succeed", decryptResult.isSuccess)
        val secret = decryptResult.getOrThrow()
        assertEquals(testMnemonic, secret.mnemonic)
    }

    @Test
    fun testRestoreWithInvalidMnemonicFails() {
        val result = repository.restoreWallet("Bad Wallet", "abandon abandon badword")
        assertFalse("Restoration with invalid mnemonic must fail", result.isSuccess)
    }

    @Test
    fun testCorruptedStorageThrowsSecurityException() {
        val (header, _) = repository.createNewWallet("Tamper Test")

        // Intentionally tamper with the SharedPreferences encrypted record
        val prefs = context.getSharedPreferences("elysium_wallet_vault", Context.MODE_PRIVATE)
        val tamperedJson = """{"iv":"AAAA","ciphertext":"corrupted_data"}"""
        prefs.edit().putString("vault_record_${header.id}", tamperedJson).commit()

        val decryptResult = repository.decryptSecretPayload(header.id)
        assertFalse("Decryption of corrupted vault record must fail", decryptResult.isSuccess)
        assertTrue("Exception must be a SecurityException", decryptResult.exceptionOrNull() is SecurityException)
    }

    @Test
    fun testMarkBackupVerifiedUpdatesStatus() {
        val (header, _) = repository.createNewWallet("Backup Test")
        assertFalse(repository.getWalletHeaders().first().isBackupVerified)

        repository.markBackupVerified(header.id)
        assertTrue(repository.getWalletHeaders().first().isBackupVerified)
    }
}
