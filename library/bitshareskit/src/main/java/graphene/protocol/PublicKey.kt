package graphene.protocol

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
data class PublicKeyType(
    override val address: String = "BTS111111111111111111111111111111111111111111114T1Anm",
    override val prefix: String = ChainConfig.CHAIN_SYMBOL_MAIN_NET,
    override val type: KeyType = KeyType.UNDEFINED
) : Key, GrapheneComponent, Comparable<PublicKeyType> {

    companion object {

        private const val PUBLIC_KEY_ADDRESS_LENGTH = 50
        val EMPTY = PublicKeyType()

        fun fromAddress(address: String, type: KeyType): PublicKeyType {
            return if (address.length <= PUBLIC_KEY_ADDRESS_LENGTH) {
                throw IllegalArgumentException("Invalid address length!")
            } else {
                PublicKeyType(address, address.substring(0, address.length - PUBLIC_KEY_ADDRESS_LENGTH), type)
            }
        }

        fun fromECKey(ecKey: ECKey, prefix: String, type: KeyType): PublicKeyType {
            return PublicKeyType(prefix + (ecKey.pubKey + ecKey.pubKeyChecksum).encodeBase58(), prefix, type)
        }

        fun fromKeyBytes(bytes: ByteArray, prefix: String, type: KeyType): PublicKeyType {
            return if (bytes.size != 33) {
                throw IllegalArgumentException("Invalid bytes size!")
            } else {
                PublicKeyType(prefix + (bytes + Utils.ripemd160(bytes).copyOfRange(0, 4)).encodeBase58(), prefix, type)
            }
        }

        fun random(prefix: String): PublicKeyType = PrivateKeyType.random(prefix).publicKey


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

    override fun compareTo(other: PublicKeyType): Int {
        for (i in 0..32) {
            val a = keyBytes[i].toInt() and 0xff
            val b = other.keyBytes[i].toInt() and 0xff
            if (a != b) {
                return a - b
            }
        }
        return 0
    }

}


class KPublicKeySerializer : KSerializer<PublicKeyType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PublicKey", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): PublicKeyType = decoder.decodeString().toPublicKey()
    override fun serialize(encoder: Encoder, value: PublicKeyType) = encoder.encodeString(value.toString())
}

fun String.toPublicKey(): PublicKeyType {
    return PublicKeyType.fromAddress(this, KeyType.UNDEFINED)
}