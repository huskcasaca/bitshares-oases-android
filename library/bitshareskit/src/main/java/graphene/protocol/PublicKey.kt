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
    override val address: String = "BTS1111111111111111111111111111111114T1Anm",
    override val prefix: String = GRAPHENE_ADDRESS_PREFIX,
    @Deprecated("removed")
    override val type: KeyType = KeyType.UNDEFINED
) : Key, GrapheneComponent, Comparable<PublicKeyType> {

    companion object {

        private const val PUBLIC_KEY_ADDRESS_LENGTH = 50
        val EMPTY = PublicKeyType()

        fun fromAddress(address: String, prefix: String): PublicKeyType {
            return PublicKeyType(address, prefix)
        }

        fun fromECKey(ecKey: ECKey, prefix: String): PublicKeyType {
            return PublicKeyType(prefix + (ecKey.pubKey + ecKey.pubKeyChecksum).encodeBase58(), prefix)
        }

        fun fromKeyBytes(bytes: ByteArray, prefix: String): PublicKeyType {
            return if (bytes.size != 33) {
                throw IllegalArgumentException("Invalid bytes size ${bytes.size}")
            } else {
                PublicKeyType(prefix + (bytes + Utils.ripemd160(bytes).copyOfRange(0, 4)).encodeBase58(), prefix)
            }
        }

        fun random(prefix: String): PublicKeyType = PrivateKeyType.random(prefix).publicKey


    }

    init {
        if (!address.startsWith(prefix)) throw IllegalArgumentException("Invalid prefix!")
    }

    val keyBytes: ByteArray = address.substring(prefix.length, address.length).decodeBase58()
    val publicBytes = keyBytes.copyOfRange(0, keyBytes.size - 4)
    val checksumBytes = keyBytes.copyOfRange(keyBytes.size - 4, keyBytes.size)
    val ecKey: ECKey? = runCatching { ECKey.fromPublicOnly(publicBytes) }.getOrNull()

    override val wif: String
        get() = throw IllegalArgumentException("Public key!")

    val isValid: Boolean = ecKey.let { it != null && it.pubKeyChecksum.contentEquals(checksumBytes) }

    override fun compareTo(other: PublicKeyType): Int {
        return bytesComparator.compare(publicBytes, other.publicBytes)
    }

}


class KPublicKeySerializer : KSerializer<PublicKeyType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PublicKeyType", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): PublicKeyType = decoder.decodeString().toPublicKey()
    override fun serialize(encoder: Encoder, value: PublicKeyType) = encoder.encodeString(value.toString())
}

fun String.toPublicKey(): PublicKeyType {
    return PublicKeyType.fromAddress(this, GRAPHENE_ADDRESS_PREFIX)
}