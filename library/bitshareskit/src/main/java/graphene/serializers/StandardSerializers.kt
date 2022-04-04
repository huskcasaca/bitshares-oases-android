package graphene.serializers

import graphene.protocol.*
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object VoteIdTypeSerializer : KSerializer<VoteIdType> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("VoteIdType", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): VoteIdType =
        decoder.decodeString().run { VoteIdType.fromStringId(this) }
    override fun serialize(encoder: Encoder, value: VoteIdType) =
        encoder.encodeString(value.toString())
}

object TimePointSecSerializer: KSerializer<Instant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("Instant", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): Instant =
        decoder.decodeString().toLocalDateTime().toInstant(TimeZone.UTC)
    override fun serialize(encoder: Encoder, value: Instant) =
        encoder.encodeString(value.toLocalDateTime(TimeZone.UTC).toString())
}

