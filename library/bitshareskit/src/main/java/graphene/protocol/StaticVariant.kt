package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonArray

@Serializable(with = StaticVariantSerializer::class)
open class StaticVariant<T> : Comparable<StaticVariant<T>> {
    open val tagType: Int64 get() = TODO()
    open val storage: T get() = TODO()

    // TODO: 2022/4/1 move to types
    override fun compareTo(other: StaticVariant<T>): Int {
        return tagType.compareTo(other.tagType)
    }
}

@Serializable(with = TypedFeeParameterSerializer::class)
class TypedFeeParameter(
    override val tagType: Int64,
    override val storage: FeeParameter
) : StaticVariant<FeeParameter>()

@Serializable()
sealed class FeeParameter

@Serializable
object EmptyFeeParameter : FeeParameter()


abstract class StaticVariantSerializer1<T> : KSerializer<StaticVariant<T>> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StaticVariant", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): StaticVariant<T> {
        decoder as JsonDecoder
        decoder.decodeJsonElement().jsonArray.let {
            val tag = decoder.json.decodeFromJsonElement<Int64>(it[0])
            val serializer = selectSerializer(tag)
            return deserialize(tag, decoder.json.decodeFromJsonElement(serializer, it[1]))
        }
    }
    override fun serialize(encoder: Encoder, value: StaticVariant<T>) {
        TODO("Not yet implemented")
    }
    open fun deserialize(tagType: Int64, storage: T): StaticVariant<T> {
        return object : StaticVariant<T>() {
            override val tagType: Int64 = tagType
            override val storage: T = storage
        }
    }
    abstract fun selectSerializer(tag: Int64): KSerializer<out T>
}

class TypedFeeParameterSerializer() : StaticVariantSerializer1<FeeParameter>() {

    override fun deserialize(tagType: Int64, storage: FeeParameter): StaticVariant<FeeParameter> {
        return TypedFeeParameter(tagType, storage)
    }

    override fun selectSerializer(tag: Int64): KSerializer<out FeeParameter> {
        return when (tag) {
            0L -> EmptyFeeParameter.serializer()
            else -> EmptyFeeParameter.serializer()
        }
    }
}