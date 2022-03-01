package com.bitshares.android.security

import android.os.Build
import bitshareskit.extensions.aesDecrypt
import bitshareskit.extensions.aesEncrypt
import bitshareskit.extensions.nextSecureRandomBytes
import bitshareskit.extensions.sha256
import com.bitshares.android.chain.blockchainDatabaseScope
import com.bitshares.android.database.LocalDatabase
import com.bitshares.android.preference.old.Settings
import kotlinx.coroutines.launch
import modulon.extensions.livedata.NonNullMutableLiveData
import java.util.*

// TODO: 21/1/2022 singleton
object SecurityService {

    object Provider {

        var uuid: UUID
            get() = Settings.KEY_UUID.value
            set(value) {
                Settings.KEY_UUID.value = value
                initWalletProvider(value)
                initBioProvider(value)
            }

        fun initBioProvider(uuid: UUID = Provider.uuid) {
            val bio = UUID(uuid.mostSignificantBits, uuid.leastSignificantBits + 1).toString()
            SecureKeyProvider.removeKeyFromKeyStore(bio)
            bioSecureProvider = SecureKeyProvider(bio, false)
        }

        fun initWalletProvider(uuid: UUID = Provider.uuid) {
            val wallet = UUID(uuid.mostSignificantBits, uuid.leastSignificantBits - 1).toString()
            SecureKeyProvider.removeKeyFromKeyStore(wallet)
            walletSecureProvider = SecureKeyProvider(wallet, false)
        }

        private var walletSecure
            get() = Settings.KEY_WALLET_SECURE.value.takeIf { it.size == 96 } ?: Settings.KEY_WALLET_SECURE.defaultValue
            set(value) {
                Settings.KEY_WALLET_SECURE.value = value.copyInto(ByteArray(96), 0)
            }

        val secureBytes: ByteArray
            get() = walletSecure.copyOfRange(0, 80)

        val secureHash: ByteArray
            get() = walletSecure.copyOfRange(80, 96)

        private var fingerprintSecure
            get() = Settings.KEY_FINGERPRINT_SECURE.value.takeIf { it.size == 96 } ?: Settings.KEY_FINGERPRINT_SECURE.defaultValue
            set(value) {
                Settings.KEY_FINGERPRINT_SECURE.value = value.copyInto(ByteArray(96), 0)
            }

        val fingerprintSecureBytes: ByteArray
            get() = fingerprintSecure.copyOfRange(0, 80)

        val fingerprintNonceBytes: ByteArray
            get() = fingerprintSecure.copyOfRange(80, 96)

        var bioSecureProvider = SecureKeyProvider(UUID(uuid.mostSignificantBits, uuid.leastSignificantBits + 1).toString(), false)
            private set

        var walletSecureProvider = SecureKeyProvider(UUID(uuid.mostSignificantBits, uuid.leastSignificantBits - 1).toString(), false)
            private set

        fun setWalletSecure(secure: ByteArray, secureHash: ByteArray) {
            walletSecure = ByteArray(96).apply {
                secure.copyInto(this, 0)
                secureHash.copyInto(this, 80)
            }
        }
    }

    fun enableFingerprint() {
        val secretKey = secret
        if (secretKey != null) {
            val cipher = runCatching { Provider.bioSecureProvider.encCipher }.getOrNull()
            if (cipher != null) {
                // 80 + 16 = 96
                Settings.KEY_FINGERPRINT_SECURE.value = cipher.doFinal(secretKey) + cipher.iv
                Settings.KEY_USE_FINGERPRINT.value = true
            }
        }
    }

    fun disableFingerprint() {
        Settings.KEY_FINGERPRINT_SECURE.reset()
        Settings.KEY_USE_FINGERPRINT.reset()
        Provider.initBioProvider()
    }

    fun unlockFingerprint(): Boolean {
        val cipher = Provider.bioSecureProvider.decCipher
        secret = runCatching { cipher.doFinal(Provider.fingerprintSecureBytes) }.getOrNull()
        isUnlocked.value = secret != null
        return isUnlocked.value
    }

    private const val DEFAULT_PASSWORD = ""

    val isCorrupted: Boolean
        get() = Provider.uuid != uuid

    val isUnlocked = NonNullMutableLiveData(false)
    val isLocked get() = !isUnlocked.value

    private var secret: ByteArray? = null

    private val uuid: UUID
        get() = calculateUuid()

    fun initialize() {
        Settings.apply {
            KEY_USE_PASSWORD.reset()
            KEY_USE_FINGERPRINT.reset()
            KEY_WALLET_SECURE.reset()
            KEY_FINGERPRINT_SECURE.reset()
            KEY_CURRENT_ACCOUNT_ID.reset()
        }
        Provider.uuid = uuid
        secret = nextSecureRandomBytes()
        isUnlocked.value = true
        removePassword()
        lock()
    }

    fun lock() {
        secret = null
        isUnlocked.value = false
    }

    fun unlock(code: String = DEFAULT_PASSWORD): Boolean {
        if (isUnlocked.value) return true
        val seed = generateSeed(code)
        val decryptedKey = runCatching { aesDecrypt(seed, Provider.secureBytes) }.getOrNull()
        if (decryptedKey != null && decryptedKey.sha256().copyOfRange(0, 16).contentEquals(Provider.secureHash)) secret = decryptedKey
        isUnlocked.value = secret != null
        return isUnlocked.value
    }

    fun encrypt(message: ByteArray?): ByteArray? {
        val seed = secret
        if (seed == null || message == null) return null
        return try {
            val cipher = Provider.walletSecureProvider.initEncryptionCipher()
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
            val cipher = Provider.walletSecureProvider.initDecryptionCipher(dec0.copyOfRange(dec0.size - 16, dec0.size))
            cipher.doFinal(dec0.copyOfRange(0, dec0.size - 16))!!
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun changePassword(newCode: String): Boolean {
        val key = secret
        if (isUnlocked.value && key != null) {
            val seed = generateSeed(newCode)
            val result = runCatching { aesEncrypt(seed, key) }.getOrNull()
            if (result != null) {
                Provider.setWalletSecure(result, key.sha256().copyOfRange(0, 16))
                Settings.KEY_USE_PASSWORD.value = newCode != DEFAULT_PASSWORD
                return true
            }
        }
        return false
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
//        lock()
        unlock()
    }

    private fun generateSeed(seed: String): ByteArray {
        return seed.sha256(16) + uuid.mostSignificantBits.toString().sha256(16)
    }

    private fun calculateUuid(): UUID {
        val device = Build.BOARD + Build.BRAND + Build.DEVICE + Build.MANUFACTURER + Build.MODEL + Build.PRODUCT
//        val timestamp = File(MainApplication.requireContext().dataDir.absolutePath + File.separator + "databases").lastModified().toString()
//        return UUID.nameUUIDFromBytes(device.sha256() + timestamp.sha256())
        return UUID.nameUUIDFromBytes(device.sha256())
    }

}