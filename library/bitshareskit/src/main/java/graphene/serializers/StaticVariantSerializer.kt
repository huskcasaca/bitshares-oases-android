package graphene.serializers

import graphene.protocol.*
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlin.reflect.KClass


// new
abstract class StaticVarSerializer<T: Any>(
    val typelist: List<KClass<out T>>,
    val fallback: Map<KClass<out T>, KSerializer<out T>> = emptyMap(),
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StaticVar", PrimitiveKind.STRING)
) : KSerializer<T> {

    @OptIn(InternalSerializationApi::class)
    fun getSerializer(tag: Int64): KSerializer<out T> {
        val clazz = typelist[tag.toInt32()]
        return clazz.serializerOrNull() ?: fallback[clazz]!!
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
