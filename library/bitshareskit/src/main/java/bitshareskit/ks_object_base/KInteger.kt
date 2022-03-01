package bitshareskit.ks_object_base


typealias UInt8  = UByte
typealias UInt16 = UShort
typealias UInt32 = UInt
typealias UInt64 = ULong

typealias Int8  = Byte
typealias Int16 = Short
typealias Int32 = Int
typealias Int64 = Long


fun String.toUInt8OrNull(radix: Int = 10): UInt8? = toUByteOrNull(radix)
fun String.toUInt16OrNull(radix: Int = 10): UInt16? = toUShortOrNull(radix)
fun String.toUInt32OrNull(radix: Int = 10): UInt32? = toUIntOrNull(radix)
fun String.toUInt64OrNull(radix: Int = 10): UInt64? = toULongOrNull(radix)

fun String.toUInt8(radix: Int = 10): UInt8 = toUByte(radix)
fun String.toUInt16(radix: Int = 10): UInt16 = toUShort(radix)
fun String.toUInt32(radix: Int = 10): UInt32 = toUInt(radix)
fun String.toUInt64(radix: Int = 10): UInt64 = toULong(radix)