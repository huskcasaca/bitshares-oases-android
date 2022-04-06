package graphene.protocol

import graphene.serializers.StaticVarSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder


@Serializable(with = HtlcHashSerializer::class)
sealed class HtlcHash

@Serializable(with = HtlcAlgoRipemd160Serializer::class)
data class HtlcAlgoRipemd160(
    val hash: String
): HtlcHash() {
    override fun toString(): String = hash
}

@Serializable(with = HtlcAlgoSha1Serializer::class)
data class HtlcAlgoSha1(
    val hash: String
): HtlcHash() {
    override fun toString(): String = hash
}

@Serializable(with = HtlcAlgoSha256Serializer::class)
data class HtlcAlgoSha256(
    val hash: String
): HtlcHash() {
    override fun toString(): String = hash
}

@Serializable(with = HtlcAlgoHash160Serializer::class)
data class HtlcAlgoHash160(
    val hash: String
): HtlcHash() {
    override fun toString(): String = hash
}

object HtlcHashSerializer : StaticVarSerializer<HtlcHash>(
    listOf(
        HtlcAlgoRipemd160::class,
        HtlcAlgoSha1::class,
        HtlcAlgoSha256::class,
        HtlcAlgoHash160::class,
    )
)

object HtlcAlgoRipemd160Serializer: KSerializer<HtlcAlgoRipemd160> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("HtlcAlgoRipemd160", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: HtlcAlgoRipemd160) =
        encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): HtlcAlgoRipemd160 =
        HtlcAlgoRipemd160(decoder.decodeString())
}

object HtlcAlgoSha1Serializer: KSerializer<HtlcAlgoSha1> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("HtlcAlgoSha1", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: HtlcAlgoSha1) =
        encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): HtlcAlgoSha1 =
        HtlcAlgoSha1(decoder.decodeString())
}

object HtlcAlgoSha256Serializer: KSerializer<HtlcAlgoSha256> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("HtlcAlgoSha256", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: HtlcAlgoSha256) =
        encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): HtlcAlgoSha256 =
        HtlcAlgoSha256(decoder.decodeString())
}

object HtlcAlgoHash160Serializer: KSerializer<HtlcAlgoHash160> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("HtlcAlgoHash160", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: HtlcAlgoHash160) =
        encoder.encodeString(value.toString())
    override fun deserialize(decoder: Decoder): HtlcAlgoHash160 =
        HtlcAlgoHash160(decoder.decodeString())
}