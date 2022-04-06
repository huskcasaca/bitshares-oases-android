package com.bitshares.oases.security

import android.content.Context
import android.os.Build
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.database.LocalDatabase
import com.bitshares.oases.globalPreferenceManager
import kotlinx.coroutines.launch
import modulon.extensions.livedata.NonNullMutableLiveData
import modulon.union.UnionContext
import java.util.*
import graphene.extension.*


class WalletManager(
    override val context: Context
): UnionContext {

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

    var provider = SecurityProvider(
        globalPreferenceManager.SECURITY_UUID.value,
        globalPreferenceManager.SECURITY_WALLET_ENC.value,
        globalPreferenceManager.SECURITY_BIO_ENC.value,
    )

    private fun resetProvider() {
        provider = SecurityProvider(
            globalPreferenceManager.SECURITY_UUID.value,
            globalPreferenceManager.SECURITY_WALLET_ENC.value,
            globalPreferenceManager.SECURITY_BIO_ENC.value,
        )
    }

    fun enableFingerprint() {
        val sec = secret ?: return
        val secResult = provider.bioEncOrNull(sec) ?: return
        globalPreferenceManager.SECURITY_BIO_ENC.value = secResult
        globalPreferenceManager.USE_BIO.value = true
        resetProvider()
    }

    fun disableFingerprint() {
        globalPreferenceManager.SECURITY_BIO_ENC.reset()
        globalPreferenceManager.USE_BIO.reset()
        provider.initBioProvider()
        resetProvider()
    }

    fun unlockFingerprint(): Boolean {
        secret = provider.bioDecOrNull()
        isUnlocked.value = secret != null
        return secret != null
    }

    var bioAuth: Boolean
        get() = globalPreferenceManager.USE_BIO.value
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
        globalPreferenceManager.apply {
            USE_PASSWORD.reset()
            USE_BIO.reset()
            SECURITY_WALLET_ENC.reset()
            SECURITY_BIO_ENC.reset()
            SECURITY_UUID.value = uuid
//            N_KEY_CURRENT_ACCOUNT_ID.reset()
        }
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

        globalPreferenceManager.SECURITY_WALLET_ENC.value = result + key.sha256().copyOfRange(0, 16)
        resetProvider()
        globalPreferenceManager.USE_PASSWORD.value = new != DEFAULT_PASSWORD
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
            cipher.iv
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