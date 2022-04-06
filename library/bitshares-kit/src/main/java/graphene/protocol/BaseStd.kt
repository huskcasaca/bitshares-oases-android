package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigInteger

typealias UInt8  = UByte
typealias UInt16 = UShort
typealias UInt32 = UInt
typealias UInt64 = ULong
typealias UnsignedInt = UInt64 // TODO: 2022/4/3

typealias Int8  = Byte
typealias Int16 = Short
typealias Int32 = Int
typealias Int64 = Long

fun String.toUInt8OrNull(radix: Int = 10):  UInt8?  = toUByteOrNull(radix)
fun String.toUInt16OrNull(radix: Int = 10): UInt16? = toUShortOrNull(radix)
fun String.toUInt32OrNull(radix: Int = 10): UInt32? = toUIntOrNull(radix)
fun String.toUInt64OrNull(radix: Int = 10): UInt64? = toULongOrNull(radix)

fun String.toUInt8(radix: Int = 10):  UInt8  = toUByte(radix)
fun String.toUInt16(radix: Int = 10): UInt16 = toUShort(radix)
fun String.toUInt32(radix: Int = 10): UInt32 = toUInt(radix)
fun String.toUInt64(radix: Int = 10): UInt64 = toULong(radix)


fun UInt8.toUInt8():  UInt8  = toUByte()
fun UInt8.toUInt16(): UInt16 = toUShort()
fun UInt8.toUInt32(): UInt32 = toUInt()
fun UInt8.toUInt64(): UInt64 = toULong()

fun UInt16.toUInt8():  UInt8  = toUByte()
fun UInt16.toUInt16(): UInt16 = toUShort()
fun UInt16.toUInt32(): UInt32 = toUInt()
fun UInt16.toUInt64(): UInt64 = toULong()

fun UInt32.toUInt8():  UInt8  = toUByte()
fun UInt32.toUInt16(): UInt16 = toUShort()
fun UInt32.toUInt32(): UInt32 = toUInt()
fun UInt32.toUInt64(): UInt64 = toULong()

fun UInt64.toUInt8():  UInt8  = toUByte()
fun UInt64.toUInt16(): UInt16 = toUShort()
fun UInt64.toUInt32(): UInt32 = toUInt()
fun UInt64.toUInt64(): UInt64 = toULong()


fun UInt8.toInt8():  Int8  = toByte()
fun UInt8.toInt16(): Int16 = toShort()
fun UInt8.toInt32(): Int32 = toInt()
fun UInt8.toInt64(): Int64 = toLong()

fun UInt16.toInt8():  Int8  = toByte()
fun UInt16.toInt16(): Int16 = toShort()
fun UInt16.toInt32(): Int32 = toInt()
fun UInt16.toInt64(): Int64 = toLong()

fun UInt32.toInt8():  Int8  = toByte()
fun UInt32.toInt16(): Int16 = toShort()
fun UInt32.toInt32(): Int32 = toInt()
fun UInt32.toInt64(): Int64 = toLong()

fun UInt64.toInt8():  Int8  = toByte()
fun UInt64.toInt16(): Int16 = toShort()
fun UInt64.toInt32(): Int32 = toInt()
fun UInt64.toInt64(): Int64 = toLong()


fun Int8.toUInt8():  UInt8  = toUByte()
fun Int8.toUInt16(): UInt16 = toUShort()
fun Int8.toUInt32(): UInt32 = toUInt()
fun Int8.toUInt64(): UInt64 = toULong()

fun Int16.toUInt8():  UInt8  = toUByte()
fun Int16.toUInt16(): UInt16 = toUShort()
fun Int16.toUInt32(): UInt32 = toUInt()
fun Int16.toUInt64(): UInt64 = toULong()

fun Int32.toUInt8():  UInt8  = toUByte()
fun Int32.toUInt16(): UInt16 = toUShort()
fun Int32.toUInt32(): UInt32 = toUInt()
fun Int32.toUInt64(): UInt64 = toULong()

fun Int64.toUInt8():  UInt8  = toUByte()
fun Int64.toUInt16(): UInt16 = toUShort()
fun Int64.toUInt32(): UInt32 = toUInt()
fun Int64.toUInt64(): UInt64 = toULong()


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


object UInt128Serializer : KSerializer<UInt128> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("kotlin.ULongLong", PrimitiveKind.STRING)
    override fun serialize(encoder: Encoder, value: UInt128) {
        encoder.encodeString(value.toString())
    }
    override fun deserialize(decoder: Decoder): UInt128 = UInt128(decoder.decodeString())
}

fun UInt8.toUInt128(): UInt128 = UInt128(toString())
fun UInt16.toUInt128(): UInt128 = UInt128(toString())
fun UInt32.toUInt128(): UInt128 = UInt128(toString())
fun UInt64.toUInt128(): UInt128 = UInt128(toString())

fun Int8.toUInt128():  UInt128 = UInt128(BigInteger.valueOf(toLong()))
fun Int16.toUInt128(): UInt128 = UInt128(BigInteger.valueOf(toLong()))
fun Int32.toUInt128(): UInt128 = UInt128(BigInteger.valueOf(toLong()))
fun Int64.toUInt128(): UInt128 = UInt128(BigInteger.valueOf(toLong()))

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


