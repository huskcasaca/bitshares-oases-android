package bitshareskit.models

import bitshareskit.extensions.createGraphene
import bitshareskit.extensions.symbolOrId
import bitshareskit.objects.AssetObject
import kotlinx.serialization.Serializable

//object MarketSerializer : KSerializer<Market> {
//
//    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Market", PrimitiveKind.STRING)
//
//    override fun deserialize(decoder: Decoder): Market {
//        return Market(
//            decoder.decodeSerializableValue(GrapheneJsonSerializer()),
//            decoder.decodeSerializableValue(GrapheneJsonSerializer()),
//        )
//    }
//    override fun serialize(encoder: Encoder, value: Market) {
//        encoder.encodeSerializableValue(GrapheneJsonSerializer(), value.base)
//        encoder.encodeSerializableValue(GrapheneJsonSerializer(), value.quote)
//    }
//}
//
//@Serializable(with = MarketSerializer::class)
@Serializable
data class Market(
    val base: AssetObject,
    val quote: AssetObject
) {

    companion object {
        val EMPTY get() = Market(AssetObject.EMPTY, AssetObject.EMPTY)
    }

    constructor(base: Long, quote: Long) : this(createGraphene(base), createGraphene(quote))

    val inverted: Market get() = Market(quote, base)

    override fun equals(other: Any?): Boolean {
        return other is Market && base.uid == other.base.uid && quote.uid == other.quote.uid
    }

    override fun hashCode(): Int {
        return base.uid.hashCode().shl(16) + quote.uid.hashCode()
    }

    override fun toString(): String {
        return "${quote.symbolOrId}/${base.symbolOrId}"
    }
}

fun encodeMarketInstance(base: Long, quote: Long): Long {
    return base.shl(32) + quote
}

fun decodeMarketInstance(encoded: Long): Pair<Long, Long> {
    val base = encoded.and(0xFFFF_FFFF_0000_0000U.toLong()).shr(32)
    val quote = encoded.and(0xFFFF_FFFFU.toLong())
    return Pair(base, quote)
}

