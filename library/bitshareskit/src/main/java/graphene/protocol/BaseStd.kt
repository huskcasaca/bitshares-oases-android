package graphene.protocol

typealias UInt8  = UByte
typealias UInt16 = UShort
typealias UInt32 = UInt
typealias UInt64 = ULong

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
fun UInt8.toInt32(): Int32 = toInt()
fun UInt8.toInt64(): Int64 = toLong()

fun UInt16.toInt8():  Int8  = toByte()
fun UInt16.toInt32(): Int32 = toInt()
fun UInt16.toInt64(): Int64 = toLong()

fun UInt32.toInt8():  Int8  = toByte()
fun UInt32.toInt16(): Int16 = toShort()
fun UInt32.toInt64(): Int64 = toLong()

fun UInt64.toInt8():  Int8  = toByte()
fun UInt64.toInt16(): Int16 = toShort()
fun UInt64.toInt32(): Int32 = toInt()


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


typealias ShareType = UInt64 // safe<int64_t>



