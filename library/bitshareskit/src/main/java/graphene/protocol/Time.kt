package graphene.protocol

import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = KGrapheneDateTimeSerializer::class)
data class ChainTimePoint(
    val time: Instant
) : GrapheneComponent {
    companion object {
        val DEFAULT_EXPIRATION_TIME: ChainTimePoint = "1970-01-01T00:00:00Z".toGrapheneDateTime()
    }
}

class KGrapheneDateTimeSerializer : KSerializer<ChainTimePoint> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GrapheneDateTime", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): ChainTimePoint = decoder.decodeString().toGrapheneDateTime()
    override fun serialize(encoder: Encoder, value: ChainTimePoint) = encoder.encodeString(value.toString())
}

fun String.toGrapheneDateTime(): ChainTimePoint {
    return runCatching { ChainTimePoint("${this}Z".toInstant()) }.getOrElse{ ChainTimePoint.DEFAULT_EXPIRATION_TIME }
}