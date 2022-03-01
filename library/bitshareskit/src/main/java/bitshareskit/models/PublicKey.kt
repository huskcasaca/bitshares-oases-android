package bitshareskit.models

import bitcoinkit.ECKey
import bitcoinkit.Utils
import bitshareskit.extensions.*
import bitshareskit.objects.GrapheneSerializable
import bitshareskit.objects.JsonSerializable

class PublicKey(key: ByteArray?, prefix: String) : GrapheneSerializable, JsonSerializable {

    companion object {
        const val BITSHARES_MAINNET_PREFIX = "BTS"
        const val BITSHARES_TESTNET_PREFIX = "TEST"
        private const val PUBLIC_KEY_LENGTH = 50

        val EMPTY_KEY = PublicKey(null, "")

        fun fromAddress(address: String): PublicKey {
            if (address.length <= PUBLIC_KEY_LENGTH) {
                return PublicKey(byteArrayOf(), BITSHARES_MAINNET_PREFIX).apply {
                    this.address = address
                }
            }
            return PublicKey(address.substring(address.length - PUBLIC_KEY_LENGTH, address.length).decodeBase58OrNull(), address.substring(0, address.length - PUBLIC_KEY_LENGTH) ).apply {
                this.address = address
            }
        }

        fun fromECKey(publicKey: ECKey, prefix: String): PublicKey{
            return PublicKey(publicKey.pubKey + publicKey.pubKeyChecksum, prefix)
        }

        fun fromKeyBytes(bytes: ByteArray, prefix: String): PublicKey {
            return if (bytes.size == 33) PublicKey(bytes + Utils.ripemd160(bytes).copyOfRange(0, 4), prefix) else PublicKey(bytes, prefix)
        }

        fun random(prefix: String): PublicKey = PrivateKey.random(prefix).publicKey

    }

    var keyBytes: ByteArray?
        private set

    var address: String = ""
        private set

    var ecKey: ECKey? = null
        private set

    val isValid: Boolean
        get() = keyBytes != null && address.isNotBlank()

    val isInvalid
        get() = !isValid

    val addressBytes
        get() = toByteArray().sha512().ripemd160()

    init {
        try {
            require(key != null && key.size == 37)
            val public = key.copyOfRange(0, key.size - 4)
            val checksum = key.copyOfRange(key.size - 4, key.size)
            val created: ECKey = ECKey.fromPublicOnly(public)
            keyBytes = public
            for (i in created.pubKeyChecksum.indices) {
                if (checksum[i] != created.pubKeyChecksum[i]) {
                    throw IllegalArgumentException("Invalid public key checksum")
                }
            }
            ecKey = created
            if (address.isEmpty()) address = prefix + key.encodeBase58()
        } catch (e: Throwable) {
            ecKey = null
            keyBytes = null
        }
    }

    override fun toByteArray(): ByteArray {
        val ecKey = ecKey
        return if (ecKey != null) ecKey.pubKeyPoint.getEncoded(true) else byteArrayOf()
    }

    override fun toString(): String = address

    override fun toJsonElement(): String = address

    override fun equals(other: Any?): Boolean = other is PublicKey && keyBytes.contentEquals(other.keyBytes)

    override fun hashCode(): Int = ecKey.hashCode()

}