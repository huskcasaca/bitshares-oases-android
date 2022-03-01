package bitshareskit.ks_models

import bitcoinkit.ECKey
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = KPrivateKeySerializer::class)
data class KPrivateKey(
    override val wif: String = "BTS111111111111111111111111111111111111111111114T1Anm",
    override val prefix: String = ChainConfig.CHAIN_SYMBOL_MAIN_NET,
    override val type: KeyType = KeyType.UNDEFINED,
) : Key, GrapheneComponent {

    private val keyBytes: ByteArray = wif.decodeBase58OrElse { ByteArray(37) }
    private val privateBytes = keyBytes.copyOfRange(1, keyBytes.size - 4) // initBytes
    private val checksumBytes = keyBytes.copyOfRange(keyBytes.size - 4, keyBytes.size)
    val ecKey: ECKey? = runCatching { ECKey.fromPrivate(privateBytes) }.getOrNull()

    val publicKey = if (ecKey != null) KPublicKey.fromECKey(ECKey.fromPublicOnly(ecKey.pubKey), prefix, type) else KPublicKey(prefix = prefix, type = type)
    override val address: String = publicKey.address

    val isValid: Boolean = keyBytes.copyOfRange(0, keyBytes.size - 4).sha256().sha256().copyOfRange(0, 4).contentEquals(checksumBytes)

    companion object {

        val EMPTY = KPrivateKey()

        fun fromECKey(ecKey: ECKey, prefix: String, type: KeyType): KPrivateKey {
            val bytes = byteArrayOf(0x80.toByte()) + ecKey.privKeyBytes!!
            val address = (bytes + bytes.sha256().sha256().copyOfRange(0, 4)).encodeBase58()
            return KPrivateKey(address, prefix, KeyType.UNDEFINED)
        }

        fun fromSeed(string: String, prefix: String): KPrivateKey {
            return fromECKey(ECKey.fromPrivate(string.sha256()), prefix, KeyType.SEED)
        }

        fun fromWif(wif: String, prefix: String): KPrivateKey {
            return KPrivateKey(wif, prefix, KeyType.WIF)
        }

        fun fromMnemonic(words: List<String>, sequence: Int = 0, prefix: String): KPrivateKey {
            return if (words.size in 10..16) {
                val seed = (words + sequence).joinToString(" ")
                fromECKey(ECKey.fromPrivate(seed.sha512().sha256()), prefix, KeyType.MNEMONIC)
            } else {
                throw IllegalArgumentException("Invalid dictionary size!")
//                KPrivateKey(prefix = prefix, type = KeyType.MNEMONIC)
            }
        }

        fun random(prefix: String): KPrivateKey = fromSeed(nextSecureRandomBytes(64).toHexString(), prefix)

        fun randomPassword(prefix: String): String = "P${random(prefix).address}"

    }

//    fun toByteArray(): ByteArray = privateBytes.orEmpty()

}


class KPrivateKeySerializer : KSerializer<KPrivateKey> {
    private fun String.toPrivateKey(): KPrivateKey {
        return KPrivateKey.fromWif(this, ChainConfig.CHAIN_SYMBOL_MAIN_NET)
    }
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("PublicKey", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): KPrivateKey = decoder.decodeString().toPrivateKey()
    override fun serialize(encoder: Encoder, value: KPrivateKey) = encoder.encodeString(value.toString())
}
