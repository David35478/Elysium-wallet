package com.example.core.crypto

import org.web3j.crypto.Bip32ECKeyPair
import org.web3j.crypto.Credentials
import org.web3j.crypto.Keys
import org.web3j.crypto.MnemonicUtils
import java.security.SecureRandom

/**
 * Standard EVM Derivation Path: m/44'/60'/0'/0/0
 * Purpose: 44' (BIP-44)
 * Coin Type: 60' (Ethereum / EVM)
 * Account: 0' (Default account)
 * Change: 0 (External / receiving address)
 * Address Index: 0 (First address)
 */
object CryptoManager {

    val EVM_DERIVATION_PATH = intArrayOf(
        44 or Bip32ECKeyPair.HARDENED_BIT,
        60 or Bip32ECKeyPair.HARDENED_BIT,
        0 or Bip32ECKeyPair.HARDENED_BIT,
        0,
        0
    )

    private val secureRandom = SecureRandom()

    /**
     * Generates 16 bytes (128 bits) of cryptographically secure random entropy
     * and produces a standard 12-word BIP-39 mnemonic phrase.
     */
    fun generate12WordMnemonic(): String {
        val entropy = ByteArray(16)
        secureRandom.nextBytes(entropy)
        return MnemonicUtils.generateMnemonic(entropy)
    }

    /**
     * Validates whether a mnemonic phrase consists of valid BIP-39 words
     * and has a valid BIP-39 checksum.
     */
    fun validateMnemonic(mnemonic: String): Boolean {
        val trimmed = cleanMnemonic(mnemonic)
        val words = trimmed.split(" ").filter { it.isNotBlank() }
        if (words.size != 12 && words.size != 24) {
            return false
        }
        return try {
            MnemonicUtils.validateMnemonic(trimmed)
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Derives an EVM account (address, private key, public key) from a BIP-39 mnemonic
     * using BIP-44 derivation path m/44'/60'/0'/0/0.
     */
    fun deriveEvmAccount(mnemonic: String, passphrase: String = ""): EvmAccountResult {
        val cleanedMnemonic = cleanMnemonic(mnemonic)
        val seed = MnemonicUtils.generateSeed(cleanedMnemonic, passphrase)
        val masterKeyPair = Bip32ECKeyPair.generateKeyPair(seed)
        val childKeyPair = Bip32ECKeyPair.deriveKeyPair(masterKeyPair, EVM_DERIVATION_PATH)
        val credentials = Credentials.create(childKeyPair)
        val checksumAddress = Keys.toChecksumAddress(credentials.address)

        return EvmAccountResult(
            address = checksumAddress,
            privateKeyHex = childKeyPair.privateKey.toString(16).padStart(64, '0'),
            publicKeyHex = childKeyPair.publicKey.toString(16),
            derivationPath = "m/44'/60'/0'/0/0"
        )
    }

    /**
     * Normalizes mnemonic words (trims whitespace, converts to lowercase, single space separated).
     */
    fun cleanMnemonic(mnemonic: String): String {
        return mnemonic.trim()
            .lowercase()
            .split("\\s+".toRegex())
            .filter { it.isNotEmpty() }
            .joinToString(" ")
    }
}

data class EvmAccountResult(
    val address: String,
    val privateKeyHex: String,
    val publicKeyHex: String,
    val derivationPath: String
)
