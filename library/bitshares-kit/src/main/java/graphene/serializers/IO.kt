package graphene.serializers

import graphene.extension.info
import kotlinx.io.core.*
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

class IO : BinaryFormat {
    override val serializersModule: SerializersModule = EmptySerializersModule
    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray {
        val encoder = LittleEndianEncoder(serializersModule)
        serializer.serialize(encoder, value)
        return encoder.build()
    }
    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T = TODO()
}

abstract class IOEncoder : AbstractEncoder() {
    private val headerSizeHint = 0
    protected val builder = BytePacketBuilder(headerSizeHint)
    private fun Output.writeVarLong(value: Long) {
        var curr = value
        while (curr and -0x80L != 0L) {
            writeByte((curr and 0x7F or 0x80).toByte())
            curr = curr ushr 7
        }
        writeByte((curr and 0x7F).toByte())
    }
    private fun Output.writeVarInt(value: Int) {
        var curr = value
        while (curr and -0x80 != 0) {
            writeByte((curr and 0x7F or 0x80).toByte())
            curr = curr ushr 7
        }
        writeByte((curr and 0x7F).toByte())
    }
    fun encodeByteArray(value: ByteArray): Unit = builder.writeFully(value)
    fun encodeVarInt(value: Int): Unit = builder.writeVarInt(value)
    fun encodeVarLong(value: Long): Unit = builder.writeVarLong(value)
    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        encodeVarInt(collectionSize)
        return super.beginCollection(descriptor, collectionSize)
    }
    fun build() = builder.build().readBytes()
    override fun encodeValue(value: Any) {
        "IOEncoder encodeValue: $value".info()
    }
}

@OptIn(ExperimentalSerializationApi::class)
class LittleEndianEncoder(
    override val serializersModule: SerializersModule,
) : IOEncoder() {
    override fun encodeBoolean(value: Boolean) = builder.writeByte(if (value) 0x01 else 0x00)
    override fun encodeByte(value: Byte): Unit = builder.writeByte(value)
    override fun encodeShort(value: Short): Unit = builder.writeShortLittleEndian(value)
    override fun encodeInt(value: Int): Unit = builder.writeIntLittleEndian(value)
    override fun encodeLong(value: Long): Unit = builder.writeLongLittleEndian(value)
    override fun encodeFloat(value: Float): Unit = builder.writeFloatLittleEndian(value)
    override fun encodeDouble(value: Double): Unit = builder.writeDoubleLittleEndian(value)
    override fun encodeChar(value: Char): Unit = builder.writeShortLittleEndian(value.toShort())
    override fun encodeString(value: String): Unit {
        encodeVarInt(value.length)
        TODO()
    }
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int): Unit = TODO()
}




class IOBuilder {
    fun build() = IO()
}

fun IO(action: IOBuilder.() -> Unit): IO = IOBuilder().apply(action).build()
