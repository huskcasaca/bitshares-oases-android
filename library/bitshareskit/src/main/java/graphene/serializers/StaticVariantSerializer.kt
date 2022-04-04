package graphene.serializers

import graphene.protocol.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import kotlin.reflect.KClass


open class StaticVariantSerializer<T>(private val elementSerializer: KSerializer<T>) : KSerializer<StaticVariant<T>> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StaticVariant", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): StaticVariant<T> {
        decoder as JsonDecoder
        decoder.decodeJsonElement().jsonArray.let {
            val tag = decoder.json.decodeFromJsonElement<Int64>(it[0])
            val serializer = selectSerializer(tag) ?: elementSerializer
            return deserialize(tag, decoder.json.decodeFromJsonElement(serializer, it[1]))
        }
    }
    override fun serialize(encoder: Encoder, value: StaticVariant<T>) {
        TODO("Not yet implemented")
    }
    open fun deserialize(tagType: Int64, storage: T): StaticVariant<T> {
        return StaticVariant(tagType, storage)
    }
    open fun selectSerializer(tag: Int64): KSerializer<out T>? {
        return null
    }
}

object TypedFeeParameterSerializer : StaticVariantSerializer<FeeParameter>(FeeParameter.serializer()) {

    override fun selectSerializer(tag: Int64): KSerializer<out FeeParameter> {
        return when (tag) {
            0L -> EmptyFeeParameter.serializer()
            else -> EmptyFeeParameter.serializer()
        }
    }
}





abstract class StaticVarSerializer<T: Any>(
    val typelist: Array<KClass<out T>>,
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StaticVar", PrimitiveKind.STRING)
) : KSerializer<T> {
    @OptIn(InternalSerializationApi::class)
    open fun getSerializer(tag: Int64): KSerializer<out T> {
        return typelist[tag.toInt32()].serializer()
    }
    override fun deserialize(decoder: Decoder): T {
        decoder as JsonDecoder
        decoder.decodeJsonElement().jsonArray.let {
            val tag = decoder.json.decodeFromJsonElement<Int64>(it[0])
            return decoder.json.decodeFromJsonElement(getSerializer(tag), it[1])
        }
    }
    override fun serialize(encoder: Encoder, value: T) {
        encoder as JsonEncoder
        val tag = typelist.indexOf(value::class).toInt64()
        encoder.encodeJsonElement(
            buildJsonArray {
                add(tag)
                add(encoder.json.encodeToJsonElement(getSerializer(tag) as KSerializer<T>, value))
            }
        )
    }
}

object SpecialAuthoritySerializer : StaticVarSerializer<SpecialAuthority>(
    arrayOf(
        NoSpecialAuthority::class,
        TopHoldersSpecialAuthority::class
    )
)

object FeeParameterSerializer : StaticVarSerializer<FeeParameter>(
    arrayOf(
        EmptyFeeParameter::class
    )
)
