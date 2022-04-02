package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigInteger

typealias uint8_t  = UByte
typealias uint16_t = UShort
typealias uint32_t = UInt
typealias uint64_t = ULong

typealias Int8  = Byte
typealias Int16 = Short
typealias Int32 = Int
typealias Int64 = Long

fun String.toUInt8OrNull(radix: Int = 10):  uint8_t?  = toUByteOrNull(radix)
fun String.toUInt16OrNull(radix: Int = 10): uint16_t? = toUShortOrNull(radix)
fun String.toUInt32OrNull(radix: Int = 10): uint32_t? = toUIntOrNull(radix)
fun String.toUInt64OrNull(radix: Int = 10): uint64_t? = toULongOrNull(radix)

fun String.toUInt8(radix: Int = 10):  uint8_t  = toUByte(radix)
fun String.toUInt16(radix: Int = 10): uint16_t = toUShort(radix)
fun String.toUInt32(radix: Int = 10): uint32_t = toUInt(radix)
fun String.toUInt64(radix: Int = 10): uint64_t = toULong(radix)


fun uint8_t.toUInt8():  uint8_t  = toUByte()
fun uint8_t.toUInt16(): uint16_t = toUShort()
fun uint8_t.toUInt32(): uint32_t = toUInt()
fun uint8_t.toUInt64(): uint64_t = toULong()

fun uint16_t.toUInt8():  uint8_t  = toUByte()
fun uint16_t.toUInt16(): uint16_t = toUShort()
fun uint16_t.toUInt32(): uint32_t = toUInt()
fun uint16_t.toUInt64(): uint64_t = toULong()

fun uint32_t.toUInt8():  uint8_t  = toUByte()
fun uint32_t.toUInt16(): uint16_t = toUShort()
fun uint32_t.toUInt32(): uint32_t = toUInt()
fun uint32_t.toUInt64(): uint64_t = toULong()

fun uint64_t.toUInt8():  uint8_t  = toUByte()
fun uint64_t.toUInt16(): uint16_t = toUShort()
fun uint64_t.toUInt32(): uint32_t = toUInt()
fun uint64_t.toUInt64(): uint64_t = toULong()


fun uint8_t.toInt8():  Int8  = toByte()
fun uint8_t.toInt32(): Int32 = toInt()
fun uint8_t.toInt64(): Int64 = toLong()

fun uint16_t.toInt8():  Int8  = toByte()
fun uint16_t.toInt32(): Int32 = toInt()
fun uint16_t.toInt64(): Int64 = toLong()

fun uint32_t.toInt8():  Int8  = toByte()
fun uint32_t.toInt16(): Int16 = toShort()
fun uint32_t.toInt64(): Int64 = toLong()

fun uint64_t.toInt8():  Int8  = toByte()
fun uint64_t.toInt16(): Int16 = toShort()
fun uint64_t.toInt32(): Int32 = toInt()


fun Int8.toUInt8():  uint8_t  = toUByte()
fun Int8.toUInt16(): uint16_t = toUShort()
fun Int8.toUInt32(): uint32_t = toUInt()
fun Int8.toUInt64(): uint64_t = toULong()

fun Int16.toUInt8():  uint8_t  = toUByte()
fun Int16.toUInt16(): uint16_t = toUShort()
fun Int16.toUInt32(): uint32_t = toUInt()
fun Int16.toUInt64(): uint64_t = toULong()

fun Int32.toUInt8():  uint8_t  = toUByte()
fun Int32.toUInt16(): uint16_t = toUShort()
fun Int32.toUInt32(): uint32_t = toUInt()
fun Int32.toUInt64(): uint64_t = toULong()

fun Int64.toUInt8():  uint8_t  = toUByte()
fun Int64.toUInt16(): uint16_t = toUShort()
fun Int64.toUInt32(): uint32_t = toUInt()
fun Int64.toUInt64(): uint64_t = toULong()


fun Int8.toInt8():  Int8  = toByte()
fun Int8.toInt32(): Int32 = toInt()
fun Int8.toInt64(): Int64 = toLong()

fun Int16.toInt8():  Int8  = toByte()
fun Int16.toInt32(): Int32 = toInt()
fun Int16.toInt64(): Int64 = toLong()

fun Int32.toInt8():  Int8  = toByte()
fun Int32.toInt16(): Int16 = toShort()
fun Int32.toInt64(): Int64 = toLong()

fun Int64.toInt8():  Int8  = toByte()
fun Int64.toInt16(): Int16 = toShort()
fun Int64.toInt32(): Int32 = toInt()

typealias share_type = uint64_t // safe<int64_t>


object UInt128Serializer : KSerializer<UInt128> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kotlin.ULongLong", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: UInt128) {
        encoder.encodeString(value.toString())
    }
    override fun deserialize(decoder: Decoder): UInt128 = UInt128(decoder.decodeString())
}

@JvmInline
@Serializable(with = UInt128Serializer::class)
value class UInt128 constructor(@PublishedApi internal val data: BigInteger) : Comparable<UInt128> {

    companion object {
        val MIN_VALUE: UInt128 = UInt128(BigInteger.valueOf(0))
        val MAX_VALUE: UInt128 = UInt128(BigInteger.valueOf(2).pow(128) - BigInteger.valueOf(1))
        const val SIZE_BYTES: Int = 16
        const val SIZE_BITS: Int = 128
    }
    constructor(value: String) : this(BigInteger(value))

    operator fun plus(other: UInt128): UInt128 = UInt128(data + other.data)
    operator fun minus(other: UInt128): UInt128 = UInt128(data - other.data)
    operator fun times(other: UInt128): UInt128 = UInt128(data * other.data)
    operator fun div(other: UInt128): UInt128 = UInt128(data / other.data)
    operator fun rem(other: UInt128): UInt128 = UInt128(data % other.data)

    fun UInt128.toByte(): Byte = data.toByte()
    fun UInt128.toChar(): Char = data.toChar()
    fun UInt128.toDouble(): Double = data.toDouble()
    fun UInt128.toFloat(): Float = data.toFloat()
    fun UInt128.toInt(): Int = data.toInt()
    fun UInt128.toLong(): Long = data.toLong()
    fun UInt128.toShort(): Short = data.toShort()

    override fun toString(): String = data.toString()
    override fun compareTo(other: UInt128): Int = data.compareTo(other.data)
}


fun uint8_t.toUInt128(): UInt128 = UInt128(toString())
fun uint16_t.toUInt128(): UInt128 = UInt128(toString())
fun uint32_t.toUInt128(): UInt128 = UInt128(toString())
fun uint64_t.toUInt128(): UInt128 = UInt128(toString())

fun Int8.toUInt128():  UInt128 = UInt128(BigInteger.valueOf(toLong()))
fun Int16.toUInt128(): UInt128 = UInt128(BigInteger.valueOf(toLong()))
fun Int32.toUInt128(): UInt128 = UInt128(BigInteger.valueOf(toLong()))
fun Int64.toUInt128(): UInt128 = UInt128(BigInteger.valueOf(toLong()))


