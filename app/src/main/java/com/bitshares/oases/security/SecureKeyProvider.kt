package com.bitshares.oases.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class SecureKeyProvider(name: String, auth: Boolean) {

    companion object {

        private const val ANDROID_KEY_STORE = "AndroidKeyStore"

        fun getKeyFromKeyStore(name: String, auth: Boolean): SecretKey {
            val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
            return if (!checkKeyExists(name)) generateAesSecKey(name, auth) else keyStore.getKey(name, null) as SecretKey
        }

        fun removeKeyFromKeyStore(name: String) {
            val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
                load(null)
                deleteEntry(name)
            }
        }

        private fun checkKeyExists(name: String): Boolean {
            val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
            val aliases = keyStore.aliases()
            while (aliases.hasMoreElements()) {
                if (name == aliases.nextElement()) return true
            }
            return false
        }

        private fun generateAesSecKey(name: String, auth: Boolean): SecretKey {
            return try {
                KeyGenParameterSpec.Builder(name, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT).run {
                    setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    setKeySize(256)
                    setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    setUserAuthenticationRequired(auth)
                    // keyGenerator
                    KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE).run {
                        init(build())
                        generateKey()
                    }
                }
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException("Failed to create a symmetric key", e)
            } catch (e: NoSuchProviderException) {
                throw RuntimeException("Failed to create a symmetric key", e)
            } catch (e: InvalidAlgorithmParameterException) {
                // FIXME: 16/11/2021      Caused by: java.lang.RuntimeException: Failed to create a symmetric key
                //        at com.bitshares.android.security.SecureKeyProvider$Companion.generateAesSecKey(SecureKeyProvider.kt:60)
                //        at com.bitshares.android.security.SecureKeyProvider$Companion.getKeyFromKeyStore(SecureKeyProvider.kt:23)
                //        at com.bitshares.android.security.SecureKeyProvider.<init>(SecureKeyProvider.kt:70)
                //        at com.bitshares.android.user_interface.wallet.WalletService$Provider.<clinit>(WalletService.kt:65)
                //     Caused by: java.security.InvalidAlgorithmParameterException: java.lang.IllegalStateException: At least one biometric must be enrolled to create keys requiring user authentication for every use
                //        at android.security.keystore.AndroidKeyStoreKeyGeneratorSpi.engineInit(AndroidKeyStoreKeyGeneratorSpi.java:252)
                //        at android.security.keystore.AndroidKeyStoreKeyGeneratorSpi$AES.engineInit(AndroidKeyStoreKeyGeneratorSpi.java:53)
                //        at javax.crypto.KeyGenerator.init(KeyGenerator.java:519)
                //        at javax.crypto.KeyGenerator.init(KeyGenerator.java:502)
                //     Caused by: java.lang.IllegalStateException: At least one biometric must be enrolled to create keys requiring user authentication for every use
                //        at android.security.keystore.KeymasterUtils.addSids(KeymasterUtils.java:110)
                //        at android.security.keystore.KeymasterUtils.addUserAuthArgs(KeymasterUtils.java:174)
                //        at android.security.keystore.AndroidKeyStoreKeyGeneratorSpi.engineInit(AndroidKeyStoreKeyGeneratorSpi.java:250)
                //        at android.security.keystore.AndroidKeyStoreKeyGeneratorSpi$AES.engineInit(AndroidKeyStoreKeyGeneratorSpi.java:53) 
                throw RuntimeException("Failed to create a symmetric key", e)
            }
        }

    }

    private fun createCipher(): Cipher = Cipher.getInstance(
        "${KeyProperties.KEY_ALGORITHM_AES}/${KeyProperties.BLOCK_MODE_CBC}/${KeyProperties.ENCRYPTION_PADDING_PKCS7}"
    )

    private val key: SecretKey = getKeyFromKeyStore(name, auth)

    val encCipher: Cipher = createCipher()
    val decCipher: Cipher = createCipher()

    fun encrypt(cipher: Cipher, message: ByteArray): ByteArray = cipher.doFinal(message)
    fun decrypt(cipher: Cipher, encrypted: ByteArray): ByteArray = cipher.doFinal(encrypted)

    fun initEncryptionCipher(): Cipher {
        try {
            encCipher.init(Cipher.ENCRYPT_MODE, key)
            return encCipher
        } catch (e: KeyPermanentlyInvalidatedException) {
            throw RuntimeException("Key Permanently Invalidated", e)
        } catch (e: Exception) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }

    fun initDecryptionCipher(iv: ByteArray): Cipher {
        try {
            decCipher.init(Cipher.DECRYPT_MODE, key, IvParameterSpec(iv))
            return decCipher
        } catch (e: KeyPermanentlyInvalidatedException) {
            throw RuntimeException("Key Permanently Invalidated", e)
        } catch (e: Exception) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }
}