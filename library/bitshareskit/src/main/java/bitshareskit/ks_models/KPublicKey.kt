package bitshareskit.ks_models

import bitcoinkit.ECKey
import bitcoinkit.Utils
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

interface Key {
    val wif: String
    val address: String
    val prefix: String
    val type: KeyType
}

@Serializable(with = KPublicKeySerializer::class)
data class KPublicKey(
    override val address: String = "BTS111111111111111111111111111111111111111111114T1Anm",
    override val prefix: String = ChainConfig.CHAIN_SYMBOL_MAIN_NET,
    override val type: KeyType = KeyType.UNDEFINED
) : Key, GrapheneComponent {

    companion object {

        private const val PUBLIC_KEY_ADDRESS_LENGTH = 50
        val EMPTY = KPublicKey()

        fun fromAddress(address: String, type: KeyType): KPublicKey {
            return if (address.length <= PUBLIC_KEY_ADDRESS_LENGTH) {
                throw IllegalArgumentException("Invalid address length!")
            } else {
                KPublicKey(address, address.substring(0, address.length - PUBLIC_KEY_ADDRESS_LENGTH), type)
            }
        }

        fun fromECKey(ecKey: ECKey, prefix: String, type: KeyType): KPublicKey{
            return KPublicKey(prefix + (ecKey.pubKey + ecKey.pubKeyChecksum).encodeBase58(), prefix, type)
        }

        fun fromKeyBytes(bytes: ByteArray, prefix: String, type: KeyType): KPublicKey {
            return if (bytes.size != 33) {
                throw IllegalArgumentException("Invalid bytes size!")
            } else {
                KPublicKey(prefix + (bytes + Utils.ripemd160(bytes).copyOfRange(0, 4)).encodeBase58(), prefix, type)
            }
        }

        fun random(prefix: String): KPublicKey = KPrivateKey.random(prefix).publicKey


    }

    init {
        if (address.length <= PUBLIC_KEY_ADDRESS_LENGTH) throw IllegalArgumentException("Address $address is invalid!")
        if (!address.startsWith(prefix)) throw IllegalArgumentException("Invalid prefix!")
    }

    private val keyBytes: ByteArray = address.substring(address.length - PUBLIC_KEY_ADDRESS_LENGTH, address.length).decodeBase58OrElse { ByteArray(37) }
    private val publicBytes = keyBytes.copyOfRange(0, keyBytes.size - 4)
    private val checksumBytes = keyBytes.copyOfRange(keyBytes.size - 4, keyBytes.size)
    private val ecKey: ECKey? = runCatching { ECKey.fromPublicOnly(publicBytes) }.getOrNull()

    override val wif: String
        get() = throw IllegalArgumentException("Public key has no wif!")

    val isValid: Boolean = runCatching {
        for (i in ecKey!!.pubKeyChecksum.indices) {
            if (checksumBytes[i] != ecKey.pubKeyChecksum[i]) {
                throw IllegalArgumentException("Invalid public key checksum!")
            }
        }
    }.isSuccess

//    fun toByteArray(): ByteArray {
//        return if (ecKey != null) ecKey.pubKeyPoint.getEncoded(true) else throw IllegalArgumentException("Invalid Public Key")
//    }

}


class KPublicKeySerializer : KSerializer<KPublicKey> {
    companion object {
        fun String.toPublicKey(): KPublicKey {
            return KPublicKey.fromAddress(this, KeyType.UNDEFINED)
        }
    }
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PublicKey", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): KPublicKey = decoder.decodeString().toPublicKey()
    override fun serialize(encoder: Encoder, value: KPublicKey) = encoder.encodeString(value.toString())
}
