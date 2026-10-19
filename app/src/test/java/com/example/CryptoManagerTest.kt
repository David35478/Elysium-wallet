package com.example

import com.example.core.crypto.CryptoManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CryptoManagerTest {

    // Standard BIP-39 / BIP-44 Ethereum test vector
    // Mnemonic: "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
    // Expected EVM address at m/44'/60'/0'/0/0: "0x9858EfFD232B4033E47d90003D41EC34EcaEda94"
    private val standardTestMnemonic = "abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"
    private val expectedStandardAddress = "0x9858EfFD232B4033E47d90003D41EC34EcaEda94"

    @Test
    fun testStandardEvmAddressDerivationMatchesBip44Vector() {
        val account = CryptoManager.deriveEvmAccount(standardTestMnemonic)
        assertEquals("Derivation path must be m/44'/60'/0'/0/0", "m/44'/60'/0'/0/0", account.derivationPath)
        assertEquals("Derived address must match expected standard checksummed EVM address", expectedStandardAddress, account.address)
        assertNotNull("Private key must be generated", account.privateKeyHex)
        assertEquals("Private key must be 64 hex characters (32 bytes)", 64, account.privateKeyHex.length)
    }

    @Test
    fun testGenerate12WordMnemonicProducesValidBip39Phrase() {
        val mnemonic = CryptoManager.generate12WordMnemonic()
        val words = mnemonic.split(" ")
        assertEquals("Must generate exactly 12 words", 12, words.size)
        assertTrue("Generated mnemonic must pass BIP-39 validation", CryptoManager.validateMnemonic(mnemonic))
    }

    @Test
    fun testRecoveryProducesDeterministicEvmAddress() {
        val mnemonic = CryptoManager.generate12WordMnemonic()

        // Derive address twice independently from the same mnemonic
        val account1 = CryptoManager.deriveEvmAccount(mnemonic)
        val account2 = CryptoManager.deriveEvmAccount(mnemonic)

        assertEquals("Same mnemonic must deterministically produce identical EVM address", account1.address, account2.address)
        assertEquals("Same mnemonic must produce identical private key", account1.privateKeyHex, account2.privateKeyHex)
    }

    @Test
    fun testDifferentMnemonicsProduceDifferentAddresses() {
        val mnemonic1 = CryptoManager.generate12WordMnemonic()
        val mnemonic2 = CryptoManager.generate12WordMnemonic()

        val account1 = CryptoManager.deriveEvmAccount(mnemonic1)
        val account2 = CryptoManager.deriveEvmAccount(mnemonic2)

        assertNotEquals("Different mnemonics must produce different addresses", account1.address, account2.address)
    }

    @Test
    fun testInvalidMnemonicDetection() {
        // Less than 12 words
        assertFalse(CryptoManager.validateMnemonic("abandon abandon abandon"))

        // Invalid word not in BIP-39 dictionary
        assertFalse(CryptoManager.validateMnemonic("notabipword abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon about"))

        // Corrupted checksum (replaced 'about' with 'abandon')
        assertFalse(CryptoManager.validateMnemonic("abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon abandon"))
    }

    @Test
    fun testMnemonicWhitespaceAndCaseNormalization() {
        val messyMnemonic = "  ABANDON   abandon   Abandon abandon abandon abandon abandon abandon abandon abandon abandon about  "
        assertTrue("Messy whitespace and mixed case should be accepted after cleaning", CryptoManager.validateMnemonic(messyMnemonic))
        val account = CryptoManager.deriveEvmAccount(messyMnemonic)
        assertEquals(expectedStandardAddress, account.address)
    }
}
