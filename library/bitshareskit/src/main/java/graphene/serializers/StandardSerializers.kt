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
import kotlinx.serialization.json.JsonEncoder

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

class OptionalSerializer<T>(
    private val elementSerializer: KSerializer<T>
) : KSerializer<Optional<T>> {

    override val descriptor: SerialDescriptor = elementSerializer.descriptor
    override fun deserialize(decoder: Decoder): Optional<T> {
        return try {
            optional(elementSerializer.deserialize(decoder))
        } catch (e: Throwable) {
            e.printStackTrace()
            optional(null)
        }
    }
    override fun serialize(encoder: Encoder, value: Optional<T>) {
        if (encoder is JsonEncoder) {
            if (value.isPresent) elementSerializer.serialize(encoder, value.value)
        } else if (encoder is IOEncoder) {
            encoder.encodeBoolean(value.isPresent)
            if (value.isPresent) elementSerializer.serialize(encoder, value.value)
        } else {
            TODO()
        }
    }

}