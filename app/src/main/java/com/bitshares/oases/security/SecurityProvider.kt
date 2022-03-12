package com.bitshares.oases.security

import java.util.*


class SecurityProvider(
    val uuid: UUID,
    ws: ByteArray,
    fs: ByteArray
) {

    companion object {
        val UUID.leastIncreased get() = UUID(mostSignificantBits, leastSignificantBits + 1)
        val UUID.leastDecreased get() = UUID(mostSignificantBits, leastSignificantBits - 1)
        val UUID.bioUUID get() = UUID(mostSignificantBits, leastSignificantBits + 1)
        val UUID.walUUID get() = UUID(mostSignificantBits, leastSignificantBits - 1)
    }

    init {
        require(ws.size == 96)
        require(fs.size == 96)
    }

    fun initBioProvider() {
        SecureKeyProvider.removeKeyFromKeyStore(uuid.bioUUID.toString())
    }
    fun initWalletProvider() {
        SecureKeyProvider.removeKeyFromKeyStore(uuid.walUUID.toString())
    }

    var bioSecureProvider = SecureKeyProvider(uuid.bioUUID.toString(), false)
    var walletSecureProvider = SecureKeyProvider(uuid.walUUID.toString(), false)

    val walletSecure = ws
    val secureBytes: ByteArray = walletSecure.copyOfRange(0, 80)
    val secureHash: ByteArray = walletSecure.copyOfRange(80, 96)

    val fingerprintSecure = fs
    val fingerprintSecureBytes: ByteArray = fingerprintSecure.copyOfRange(0, 80)
    val fingerprintNonceBytes: ByteArray = fingerprintSecure.copyOfRange(80, 96)

    fun bioDecOrNull(): ByteArray? {
        val cipher = bioSecureProvider.decCipher
        return runCatching { cipher.doFinal(fingerprintSecureBytes) }.getOrNull()
    }

    fun bioEncOrNull(sec: ByteArray): ByteArray? {
        return runCatching {
            val cipher = bioSecureProvider.encCipher
            cipher.doFinal(sec) + cipher.iv
        }.getOrNull()
    }

}