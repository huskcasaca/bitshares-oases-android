package bitshareskit.models

import bitcoinkit.ECKey
import bitshareskit.extensions.*
import bitshareskit.models.PrivateKey.KeyType.*
import bitshareskit.objects.GrapheneSerializable

open class PrivateKey(
    private val initBytes: ByteArray?,
    val type: KeyType = UNDEFINED,
    val prefix: String = PublicKey.BITSHARES_MAINNET_PREFIX
) : GrapheneSerializable {

    enum class KeyType {
        UNDEFINED, SEED, WIF, MNEMONIC, RESTORE
    }

    companion object {

        fun fromECKey(privateKey: ECKey, prefix: String): PrivateKey {
            return PrivateKey(privateKey.privKeyBytes, UNDEFINED, prefix)
        }

        fun fromSeed(string: String, prefix: String): PrivateKey {
            return PrivateKey(string.sha256(), SEED, prefix)
        }

        fun fromWif(wif: String, prefix: String): PrivateKey {
            runCatching {
                wif.decodeBase58().let {
                    if (it.size != 37) return@let
                    val checksum = it.copyOfRange(0, it.size - 4).sha256().sha256().copyOfRange(0, 4)
                    if (it.copyOfRange(it.size - 4, it.size).contentEquals(checksum))
                        return PrivateKey(it.copyOfRange(1, it.size - 4), WIF, prefix)
                }
            }
            return PrivateKey(null, WIF, prefix)
        }

        fun fromMnemonic(words: List<String>, sequence: Int = 0, prefix: String): PrivateKey {
            if (words.size in 10..16) {
                val seed = (words + sequence).joinToString(" ")
                return PrivateKey(seed.sha512().sha256(), MNEMONIC, prefix)
            }
            return PrivateKey(null, MNEMONIC, prefix)
        }

        fun random(prefix: String): PrivateKey = fromSeed(nextSecureRandomBytes(64).toHexString(), prefix)

        fun randomPassword(prefix: String): String = "P${random(prefix).wif}"

    }

    var isEncrypted = true

    private var _publicKey: PublicKey? = null

    var keyBytes: ByteArray? = initBytes
        private set

    var address: String = EMPTY_SPACE
        private set

    var wif: String = EMPTY_SPACE
        private set

    val publicKey: PublicKey get() = _publicKey ?: PublicKey.EMPTY_KEY

    var ecKey: ECKey? = null
        private set

    val isValid: Boolean
        get() = keyBytes != null && address.isNotEmpty()


    init {
        if (initBytes != null && initBytes.size == 32) createPrivate(initBytes)
    }

//    fun sign(input: ByteArray) = ecKey?.sign(input)
//
//    fun changeTestnet(testnet: Boolean){
//        if (ecKey != null) {
//            _publicKey = PublicKey.fromECKey(ECKey.fromPublicOnly(ecKey?.pubKey), testnet)
//            address = _publicKey?.address
//        }
//    }

    fun PrivateKey.encrypt(seed: ByteArray) {
        keyBytes = keyBytes?.let { isEncrypted = true; aesEncrypt(seed, it) }
    }

    fun PrivateKey.decrypt(seed: ByteArray) {
        keyBytes = keyBytes?.let { isEncrypted = false; aesDecrypt(seed, it) }
        createPrivate(keyBytes)
    }

    private fun createPrivate(key: ByteArray?) {
        try {
            val ec = ECKey.fromPrivate(key!!)
            val array = byteArrayOf(0x80.toByte()) + ec.privKeyBytes!!
            _publicKey = PublicKey.fromECKey(ECKey.fromPublicOnly(ec.pubKey), prefix)
            address = publicKey.address
            wif = (array + array.sha256().sha256().copyOfRange(0, 4)).encodeBase58()
            ecKey = ec
        } catch (e: Throwable) {
            _publicKey = null
            address = EMPTY_SPACE
            e.printStackTrace()
        }
    }

    fun rePrefix(prefix: String) = PrivateKey(keyBytes, type, prefix)

    override fun toByteArray(): ByteArray = keyBytes.orEmpty()

    override fun toString(): String = keyBytes?.toHexString().orEmpty()

    override fun toJsonElement(): String = wif

    override fun equals(other: Any?): Boolean = other is PrivateKey && other.keyBytes.contentEquals(keyBytes)

    override fun hashCode(): Int = keyBytes?.contentHashCode() ?: 0


}