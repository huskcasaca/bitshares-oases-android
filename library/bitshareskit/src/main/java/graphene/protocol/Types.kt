package graphene.protocol

import graphene.serializers.OptionalSerializer
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

typealias BlockIdType = String  //typealias block_id_type = fc::ripemd160; TODO
typealias ChecksumType = String //typealias checksum_type = fc::ripemd160; TODO
//typealias transaction_id_type = fc::ripemd160; TODO
//typealias digest_type = fc::sha256; TODO
//typealias signature_type = fc::ecc::compact_signature; TODO
//typealias share_type = safe<int64_t>; TODO
//typealias weight_type = uint16_t; TODO


//using private_key_type = fc::ecc::private_key;
typealias ChainIdType = String  //using chain_id_type = fc::sha256;
//using ratio_type = boost::rational<int32_t>;

@Serializable(with = OptionalSerializer::class)
data class Optional<T>(
    val valueSafe: T? = null
) {
    val value get() = valueSafe!!
    val isPresent get() = valueSafe != null

    override fun toString(): String {
        return valueSafe.toString()
    }
}

fun <T> optional(value: T? = null) = Optional(value)

fun <T> Optional<T>.getOrNull() = valueSafe
fun <T> Optional<T>.getOrThrow() = value
fun <T> Optional<T>.getOrElse(fallback: () -> T) = valueSafe ?: fallback()

