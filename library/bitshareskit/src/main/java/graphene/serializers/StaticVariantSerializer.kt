package graphene.serializers

import graphene.protocol.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import kotlin.reflect.KClass

fun staticVarSerialDescriptor(
    elementDescriptor: SerialDescriptor
) = StaticVarSerialDescriptor(elementDescriptor)

class StaticVarSerialDescriptor(
    val elementDescriptor: SerialDescriptor
) : SerialDescriptor {
    override val serialName: String get() = "StaticVar"
    override val kind: SerialKind get() = StructureKind.LIST
    override val elementsCount: Int = 2
    override fun getElementName(index: Int): String = index.toString()
    override fun getElementIndex(name: String): Int =
        name.toIntOrNull() ?: throw IllegalArgumentException("$name is not a valid map index")
    override fun isElementOptional(index: Int): Boolean {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return false
    }
    override fun getElementAnnotations(index: Int): List<Annotation> {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return emptyList()
    }
    override fun getElementDescriptor(index: Int): SerialDescriptor {
        require(index >= 0) { "Illegal index $index, $serialName expects only non-negative indices"}
        return when (index) {
            0 -> Int64.serializer().descriptor
            1 -> elementDescriptor
            else -> error("Unreached")
        }
    }
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StaticVarSerialDescriptor) return false
        if (serialName != other.serialName) return false
        if (elementDescriptor != other.elementDescriptor) return false
        return true
    }
    override fun hashCode(): Int {
        var result = serialName.hashCode()
        result = 31 * result + elementDescriptor.hashCode()
        return result
    }
    override fun toString(): String = "$serialName($elementDescriptor)"
}

// new
abstract class StaticVarSerializer<T: Any>(
    val typelist: List<KClass<out T>>,
    val fallback: Map<KClass<out T>, KSerializer<out T>> = emptyMap(),
) : KSerializer<T> {

    private val typelistInternal = typelist.map {
        @OptIn(InternalSerializationApi::class)
        fallback.getOrElse(it) { it.serializer() }
    }
    override val descriptor: SerialDescriptor = staticVarSerialDescriptor(typelistInternal.first().descriptor) // TODO: 2022/4/6

    private val tagSerializer = Int64.serializer()
    fun getSerializer(tag: Int64): KSerializer<T> {
        val clazz: KClass<out T> = typelist[tag.toInt32()]
        @OptIn(InternalSerializationApi::class)
        return (clazz.serializerOrNull() ?: fallback[clazz]!!) as KSerializer<T> // TODO: 2022/4/6
    }
    override fun deserialize(decoder: Decoder): T {
        return decoder.decodeStructure(descriptor) {
            val tag: Int64 = decodeSerializableElement(tagSerializer.descriptor, 0, tagSerializer)
            val valueSerializer = getSerializer(tag)
            decodeSerializableElement(valueSerializer.descriptor, 1, valueSerializer)
        }
    }
    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeStructure(descriptor) {
            val tag: Int64 = typelist.indexOf(value::class).toInt64()
            encodeSerializableElement(tagSerializer.descriptor, 0, tagSerializer, tag)
            val valueSerializer = getSerializer(tag)
            encodeSerializableElement(valueSerializer.descriptor, 1, valueSerializer, value)
        }
    }
}
