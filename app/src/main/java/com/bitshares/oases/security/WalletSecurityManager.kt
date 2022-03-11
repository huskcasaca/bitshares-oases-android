package com.bitshares.oases.security

import android.os.Build
import bitshareskit.extensions.aesDecrypt
import bitshareskit.extensions.aesEncrypt
import bitshareskit.extensions.nextSecureRandomBytes
import bitshareskit.extensions.sha256
import com.bitshares.oases.MainApplication
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.database.LocalDatabase
import kotlinx.coroutines.launch
import modulon.extensions.livedata.NonNullMutableLiveData
import java.util.*


class WalletSecurityManager(
    private val application: MainApplication
) {

    companion object {
        fun calculateUuid(): UUID {
            val device = Build.BOARD + Build.BRAND + Build.DEVICE + Build.MANUFACTURER + Build.MODEL + Build.PRODUCT
            return UUID.nameUUIDFromBytes(device.sha256())
        }
        private fun generateSeed(uuid: UUID, seed: String): ByteArray {
            return seed.sha256(16) + uuid.mostSignificantBits.toString().sha256(16)
        }
        private val DEFAULT_PASSWORD = ""

    }

    private var secret: ByteArray? = null

    private val uuid: UUID = calculateUuid()

    var provider = WalletSecurityProvider(
        application.settingsManager.N_KEY_UUID.value,
        application.settingsManager.N_KEY_WALLET_SECURE.value,
        application.settingsManager.N_KEY_FINGERPRINT_SECURE.value,
    )

    private fun resetProvider() {
        provider = WalletSecurityProvider(
            application.settingsManager.N_KEY_UUID.value,
            application.settingsManager.N_KEY_WALLET_SECURE.value,
            application.settingsManager.N_KEY_FINGERPRINT_SECURE.value,
        )
    }

    fun enableFingerprint() {
        val sec = secret ?: return
        val secResult = provider.bioEncOrNull(sec) ?: return
        application.settingsManager.N_KEY_FINGERPRINT_SECURE.value = secResult
        application.settingsManager.N_KEY_USE_FINGERPRINT.value = true
        resetProvider()
    }

    fun disableFingerprint() {
        application.settingsManager.N_KEY_FINGERPRINT_SECURE.reset()
        application.settingsManager.N_KEY_USE_FINGERPRINT.reset()
        provider.initBioProvider()
        resetProvider()
    }

    fun unlockFingerprint(): Boolean {
        secret = provider.bioDecOrNull()
        isUnlocked.value = secret != null
        return secret != null
    }

    var bioAuth: Boolean
        get() = application.settingsManager.N_KEY_USE_FINGERPRINT.value
        set(value) {
            if (value) enableFingerprint() else disableFingerprint()
        }


    val isCorrupted: Boolean
        get() = provider.uuid != uuid

    // TODO: 2022/3/10 add for this
    val isUnlockedState = false
    val isUnlocked = NonNullMutableLiveData(isUnlockedState)

    fun initialize() {
        isUnlocked.value = false
        secret = null
        application.settingsManager.apply {
            N_KEY_USE_PASSWORD.reset()
            N_KEY_USE_FINGERPRINT.reset()
            N_KEY_WALLET_SECURE.reset()
            N_KEY_FINGERPRINT_SECURE.reset()
//            N_KEY_CURRENT_ACCOUNT_ID.reset()
        }
        application.settingsManager.N_KEY_UUID.value = uuid
        resetProvider()
        secret = nextSecureRandomBytes(64)

        isUnlocked.value = true
        removePassword()
        lock()
    }

    fun changePassword(new: String): Boolean {
        if (!isUnlocked.value) return false
        val key = secret ?: return false
        val seed = generateSeed(provider.uuid, new)
        val result = runCatching { aesEncrypt(seed, key) }.getOrNull() ?: return false

        application.settingsManager.N_KEY_WALLET_SECURE.value = result + key.sha256().copyOfRange(0, 16)
        resetProvider()
        application.settingsManager.N_KEY_USE_PASSWORD.value = new != DEFAULT_PASSWORD
        return true
    }

    fun removePassword() {
        changePassword(DEFAULT_PASSWORD)
        disableFingerprint()
    }

    fun reset() {
        lock()
        blockchainDatabaseScope.launch {
            LocalDatabase.INSTANCE.userDao().clear()
        }
        initialize()
        unlock()
    }




    fun lock() {
        secret = null
        isUnlocked.value = false
    }
    fun unlock(code: String = DEFAULT_PASSWORD): Boolean {
        if (isUnlocked.value && secret != null) return true
        val seed = generateSeed(provider.uuid, code)
        val decryptedKey = runCatching { aesDecrypt(seed, provider.secureBytes) }.getOrNull() ?: return false
        if (decryptedKey.sha256().copyOfRange(0, 16).contentEquals(provider.secureHash)) {
            secret = decryptedKey
        }
        isUnlocked.value = secret != null
        return secret != null
    }
    fun encrypt(message: ByteArray?): ByteArray? {
        val seed = secret
        if (seed == null || message == null) return null
        return try {
            val cipher = provider.walletSecureProvider.initEncryptionCipher()
            val enc0 = cipher.doFinal(message)!! + cipher.iv
            aesEncrypt(seed, enc0)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    fun decrypt(message: ByteArray?): ByteArray? {
        val seed = secret
        if (seed == null || message == null) return null
        return try {
            val dec0 = aesDecrypt(seed, message)
            val cipher = provider.walletSecureProvider.initDecryptionCipher(dec0.copyOfRange(dec0.size - 16, dec0.size))
            cipher.doFinal(dec0.copyOfRange(0, dec0.size - 16))!!
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}