package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.core.crypto.CryptoManager
import com.example.data.repository.WalletVaultRepository
import com.example.model.Screen
import com.example.viewmodel.WalletViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Elysium", appName)
    }

    @Test
    fun `launch MainActivity without crash`() {
        val controller = org.robolectric.Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        assertNotNull(activity)
    }

    @Test
    fun `CryptoManager generates valid 12-word mnemonic and derives EVM address`() {
        val mnemonic = CryptoManager.generate12WordMnemonic()
        val words = mnemonic.split(" ").filter { it.isNotBlank() }
        assertEquals(12, words.size)
        assertTrue(CryptoManager.validateMnemonic(mnemonic))

        val account = CryptoManager.deriveEvmAccount(mnemonic)
        assertNotNull(account.address)
        assertTrue(account.address.startsWith("0x"))
        assertEquals(42, account.address.length)
        assertEquals("m/44'/60'/0'/0/0", account.derivationPath)
    }

    @Test
    fun `CryptoManager rejects invalid mnemonic`() {
        assertFalse(CryptoManager.validateMnemonic("not a valid mnemonic phrase"))
        assertFalse(CryptoManager.validateMnemonic("apple banana cherry dog elephant fox grape horse iguana jaguar kiwi"))
    }

    @Test
    fun `WalletVaultRepository creates and encrypts wallet into keystore`() {
        val context = ApplicationProvider.getApplicationContext<Context>() as android.app.Application
        val repo = WalletVaultRepository(context)

        val (header, mnemonic) = repo.createNewWallet("Test Wallet")
        assertNotNull(header.id)
        assertEquals("Test Wallet", header.name)
        assertTrue(header.address.startsWith("0x"))

        val stored = repo.getWalletHeaders()
        assertTrue(stored.any { it.id == header.id })

        val decrypted = repo.decryptSecretPayload(header.id).getOrThrow()
        assertEquals(mnemonic, decrypted.mnemonic)
    }
}
