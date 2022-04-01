package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

//data class RIPEMD160(
//    val value: String
//)

typealias BlockIdType = String
typealias ChecksumType = String
//typealias block_id_type = fc::ripemd160; TODO
//typealias checksum_type = fc::ripemd160; TODO
//typealias transaction_id_type = fc::ripemd160; TODO
//typealias digest_type = fc::sha256; TODO
//typealias signature_type = fc::ecc::compact_signature; TODO
//typealias share_type = safe<int64_t>; TODO
//typealias weight_type = uint16_t; TODO


@Serializable(with = OptionalSerializer::class)
class Optional<T>(
    val valueSafe: T? = null
) {
    val value get() = valueSafe!!
    val isPresent get() = valueSafe != null
    val isNull get() = valueSafe != null
}

fun <T> optional(value: T? = null) = Optional(value)

fun <T> Optional<T>.getOrNull() = valueSafe
fun <T> Optional<T>.getOrThrow() = value
fun <T> Optional<T>.getOrElse(fallback: () -> T) = valueSafe ?: fallback()


class OptionalSerializer<T>(private val elementSerializer: KSerializer<T>) : KSerializer<Optional<T>> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("optional", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): Optional<T> {
        return try {
            optional(elementSerializer.deserialize(decoder))
        } catch (e: Throwable) {
            e.printStackTrace()
            optional(null)
        }
    }
    override fun serialize(encoder: Encoder, value: Optional<T>) {
        if (value.isPresent) {
            elementSerializer.serialize(encoder, value.value)
        } else {

        }
    }

}