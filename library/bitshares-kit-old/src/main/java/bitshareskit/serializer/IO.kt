package bitshareskit.serializer

import bitshareskit.objects.ByteSerializable
import bitshareskit.objects.GrapheneSerializable
import bitshareskit.objects.GrapheneSortSerializable
import graphene.extension.toHexByteArray
import kotlinx.io.core.*
import java.io.IOException
import java.util.*
import kotlin.experimental.and

class GrapheneString(val string: String, val writeLength: Boolean = false)

fun Output.writeGrapheneBoolean(v: Boolean) = writeByte(if (v) 1 else 0)
fun Output.writeGrapheneByte(v: Number) = writeByte(v.toByte())
fun Output.writeGrapheneShort(v: Number) = writeShortLittleEndian(v.toShort())
fun Output.writeGrapheneInt(v: Number) = writeIntLittleEndian(v.toInt())
fun Output.writeGrapheneLong(v: Number) = writeLongLittleEndian(v.toLong())

fun Output.writeGrapheneUByte(v: UByte) = writeByte(v.toByte())
fun Output.writeGrapheneUShort(v: UShort) = writeShortLittleEndian(v.toShort())
fun Output.writeGrapheneUInt(v: UInt) = writeIntLittleEndian(v.toInt())
fun Output.writeGrapheneULong(v: ULong) = writeLongLittleEndian(v.toLong())

fun Output.writeGrapheneFloat(v: Number) = writeFloatLittleEndian(v.toFloat())
fun Output.writeGrapheneDouble(v: Number) = writeDoubleLittleEndian(v.toDouble())

fun Output.writeGrapheneString(s: GrapheneString){
    if (s.writeLength) writeVarInt(s.string.length)
    writeFully(s.string.toHexByteArray())
}

fun Output.writeGrapheneString(s: String, writeLength: Boolean = false){
    if (writeLength) writeVarInt(s.length)
    writeFully(s.toHexByteArray())
}

fun Output.writeGrapheneTime(time: Date) = writeGrapheneUInt(time.toInstant().epochSecond.toUInt())

fun Output.writeSerializable(s: ByteSerializable) = writeFully(s.toByteArray())

fun Output.writeGrapheneMap(map: Map<out Any, Any>) {
    writeVarInt(map.size)
    map.toSortedMap(grapheneGlobalComparator).forEach { (key, value) ->
        writeGrapheneTypes(key)
        writeGrapheneTypes(value)
    }
}

fun Output.writeGrapheneSet(set: Set<Any>) {
    writeVarInt(set.size)
    set.toSortedSet(grapheneGlobalComparator).forEach {
        writeGrapheneTypes(it)
    }
}

fun Output.writeGrapheneIndexedSet(set: Set<Any>) {
    writeVarInt(set.size)
    set.toSortedSet(grapheneGlobalComparator).forEachIndexed { index, value ->
        writeVarInt(index)
        writeGrapheneTypes(value)
    }
}

fun Output.writeGrapheneIndexedArray(collection: Collection<GrapheneSortSerializable>) {
    writeVarInt(collection.size)
    collection.forEach {
        writeVarInt(it.ordinal)
        writeSerializable(it)
    }
}

@Suppress("UNCHECKED_CAST")
fun Output.writeGrapheneTypes(item: Any) {
    when (item) {
        is GrapheneSerializable -> writeSerializable(item)
        is Boolean              -> writeGrapheneBoolean(item)
        is UByte                -> writeGrapheneUByte(item)
        is UShort               -> writeGrapheneUShort(item)
        is UInt                 -> writeGrapheneUInt(item)
        is ULong                -> writeGrapheneULong(item)
        is Float                -> writeGrapheneFloat(item)
        is Double               -> writeGrapheneDouble(item)
        is String               -> writeGrapheneString(item)
        is Date                 -> writeGrapheneTime(item)
        is Map<*, *>            -> writeGrapheneMap(item as Map<out Any, Any>)
        is Set<*>               -> writeGrapheneSet(item as Set<Any>)
        else                    -> throw IllegalArgumentException("Unsupported graphene type: ${item::class}")
    }
}

//fun Output.writeVarInt(value: UInt) {
//    var current = value
//    while (true) {
//        val masked = current.and(0x7FU).toInt()
//        current = current.shr(7)
//        if (current == 0U) {
//            writeByte(masked.toByte())
//            return
//        } else {
//            writeByte(masked.or(0x80).toByte())
//        }
//    }
//}

//fun Output.writeVarInt(value: UInt) {
//    var curr = value
//    while (curr shr 7 != 0U) {
//        writeByte((curr and 0x7FU or 0x80U).toByte())
//        curr = curr shr 7
//    }
//    writeByte(curr.and(0x7FU).toByte())
//}
//fun Output.writeVarInt(value: UInt) = writeVarInt(value.toInt())
fun Output.writeVarInt(value: Int) {
    var curr = value
    while (curr and -0x80 != 0) {
        writeByte((curr and 0x7F or 0x80).toByte())
        curr = curr ushr 7
    }
    writeByte((curr and 0x7F).toByte())
}

//fun Output.writeVarIntUnsigned(v: Int) = writeVarInt(v shl 1 xor (v shr 31))

fun Output.writeVarLong(value: Long) {
    var curr = value
    while (curr and -0x80L != 0L) {
        writeByte((curr and 0x7F or 0x80).toByte())
        curr = curr ushr 7
    }
    writeByte((curr and 0x7F).toByte())
}

//fun Output.writeVarLongUnsigned(v: Long) = writeVarLong(v shl 1 xor (v shr 63))




/**
 * @throws IllegalArgumentException     if variable-length value does not terminate
 *                                      after 5 bytes have been read
 * @throws IOException                  if [Input] throws [IOException]
 */
@Throws(IllegalArgumentException::class, IOException::class)
fun Input.readVarLongUnsigned(): Long {
    var value = 0L
    var i = 0
    var b: Long
    while (readByte().also { b = it.toLong() } and 0x80L.toByte() != 0.toByte()) {
        value = value or (b and 0x7F shl i)
        i += 7
        require(i <= 63) { "Variable too long" }
    }
    return value or (b shl i)
}

@Throws(IllegalArgumentException::class, IOException::class)
fun Input.readVarLongSigned(): Long {
    val raw = readVarLongUnsigned()
    // This undoes the trick in writeSignedVarLong()
    val temp = raw shl 63 shr 63 xor raw shr 1
    // This extra step lets us deal with the largest signed values by treating
    // negative results from read unsigned methods as like unsigned values
    // Must re-flip the top bit if the original read value had it set.
    return temp xor (raw and (1L shl 63))
}

/**
 * @throws IllegalArgumentException     if variable-length value does not terminate
 *                                      after 5 bytes have been read
 * @throws IOException                  if [Input] throws [IOException]
 */
@Throws(IllegalArgumentException::class, IOException::class)
fun Input.readVarIntUnsigned(): Int {
    var value = 0
    var i = 0
    var b: Int
    while (readByte().also { b = it.toInt() } and 0x80.toByte() != 0.toByte()) {
        value = value or (b and 0x7F shl i)
        i += 7
        require(i <= 35) { "Variable too long" }
    }
    return value or (b shl i)
}

@Throws(IllegalArgumentException::class, IOException::class)
fun Input.readVarIntSigned(): Int {
    val raw = readVarIntUnsigned()
    val temp = raw shl 31 shr 31 xor raw shr 1
    return temp xor (raw and (1 shl 31))
}


//import java.io.*
//
///**
// * A little endian output stream writes primitive Java numbers
// * and characters to an output stream in a little endian format.
// * The standard java.io.DataOutputStream class which this class
// * imitates uses big-endian integers.
// * Modified 8/11/2016 by Steven Phillips to advertise that it implements DataOutput.
// *
// * @author  Elliotte Rusty Harold
// * @version 1.0.1, 19 May 1999
// * @see java.io.DataOutputStream
// */
//
//
///**
// * Creates a new little endian output stream and chains it to the
// * output stream specified by the out argument.
// *
// * @param   out   the underlying output stream.
// * @see java.io.FilterOutputStream.out
// */
//class GrapheneOutputStream(out: OutputStream) : FilterOutputStream(out), DataOutput {
//
//    /**
//     * The number of bytes written so far to the little endian output stream.
//     */
//    private var written = 0
//
//    /**
//     * Writes the specified byte value to the underlying output stream.
//     *
//     * @param      b   the `byte` value to be written.
//     * @exception IOException  if the underlying stream throws an IOException.
//     */
//    @Synchronized
//    @Throws(IOException::class)
//    override fun write(b: Int) {
//        out.write(b)
//        written++
//    }
//
//    /**
//     * Writes `length` bytes from the specified byte array
//     * starting at `offset` to the underlying output stream.
//     *
//     * @param      data     the data.
//     * @param      offset   the start offset in the data.
//     * @param      length   the number of bytes to write.
//     * @exception IOException  if the underlying stream throws an IOException.
//     */
//    @Synchronized
//    @Throws(IOException::class)
//    override fun write(data: ByteArray, offset: Int, length: Int) {
//        out.write(data, offset, length)
//        written += length
//    }
//
//    /**
//     * Writes a `boolean` to the underlying output stream as
//     * a single byte. If the argument is true, the byte value 1 is written.
//     * If the argument is false, the byte value `0` in written.
//     *
//     * @param      b   the `boolean` value to be written.
//     * @exception IOException  if the underlying stream throws an IOException.
//     */
//    @Throws(IOException::class)
//    override fun writeBoolean(b: Boolean) {
//        if (b) write(1) else write(0)
//    }
//
//    /**
//     * Writes out a `byte` to the underlying output stream
//     *
//     * @param      b   the `byte` value to be written.
//     * @exception IOException  if the underlying stream throws an IOException.
//     */
//    @Throws(IOException::class)
//    override fun writeByte(b: Int) {
//        out.write(b)
//        written++
//    }
//
//    /**
//     * Writes a two byte `short` to the underlying output stream in
//     * little endian order, low byte first.
//     *
//     * @param      s   the `short` to be written.
//     * @exception IOException  if the underlying stream throws an IOException.
//     */
//    @Throws(IOException::class)
//    override fun writeShort(s: Int) {
//        out.write(s and 0xFF)
//        out.write(s ushr 8 and 0xFF)
//        written += 2
//    }
//
//    /**
//     * Writes a two byte `char` to the underlying output stream
//     * in little endian order, low byte first.
//     *
//     * @param      c   the `char` value to be written.
//     * @exception IOException  if the underlying stream throws an IOException.
//     */
//    @Throws(IOException::class)
//    override fun writeChar(c: Int) {
//        out.write(c and 0xFF)
//        out.write(c ushr 8 and 0xFF)
//        written += 2
//    }
//
//    /**
//     * Writes a four-byte `int` to the underlying output stream
//     * in little endian order, low byte first, high byte last
//     *
//     * @param      i   the `int` to be written.
//     * @exception IOException  if the underlying stream throws an IOException.
//     */
//    @Throws(IOException::class)
//    override fun writeInt(i: Int) {
//        out.write(i and 0xFF)
//        out.write(i ushr 8 and 0xFF)
//        out.write(i ushr 16 and 0xFF)
//        out.write(i ushr 24 and 0xFF)
//        written += 4
//    }
//
//    /**
//     * Writes an eight-byte `long` to the underlying output stream
//     * in little endian order, low byte first, high byte last
//     *
//     * @param      l   the `long` to be written.
//     * @exception IOException  if the underlying stream throws an IOException.
//     */
//    @Throws(IOException::class)
//    override fun writeLong(l: Long) {
//        out.write(l.toInt() and 0xFF)
//        out.write((l ushr 8).toInt() and 0xFF)
//        out.write((l ushr 16).toInt() and 0xFF)
//        out.write((l ushr 24).toInt() and 0xFF)
//        out.write((l ushr 32).toInt() and 0xFF)
//        out.write((l ushr 40).toInt() and 0xFF)
//        out.write((l ushr 48).toInt() and 0xFF)
//        out.write((l ushr 56).toInt() and 0xFF)
//        written += 8
//    }
//
//    /**
//     * Writes a 4 byte Java float to the underlying output stream in
//     * little endian order.
//     *
//     * @param      f   the `float` value to be written.
//     * @exception IOException  if an I/O error occurs.
//     */
//    @Throws(IOException::class)
//    override fun writeFloat(f: Float) {
//        writeInt(java.lang.Float.floatToIntBits(f))
//    }
//
//    /**
//     * Writes an 8 byte Java double to the underlying output stream in
//     * little endian order.
//     *
//     * @param      d   the `double` value to be written.
//     * @exception IOException  if an I/O error occurs.
//     */
//    @Throws(IOException::class)
//    override fun writeDouble(d: Double) {
//        writeLong(java.lang.Double.doubleToLongBits(d))
//    }
//
//    /**
//     * Writes a string to the underlying output stream as a sequence of
//     * bytes. Each character is written to the data output stream as
//     * if by the `writeByte()` method.
//     *
//     * @param      s   the `String` value to be written.
//     * @exception IOException  if the underlying stream throws an IOException.
//     */
//    @Throws(IOException::class)
//    override fun writeBytes(s: String) {
//        val length = s.length
//        for (i in 0 until length) {
//            out.write(s[i].toInt())
//        }
//        written += length
//    }
//
//    /**
//     * Writes a string to the underlying output stream as a sequence of
//     * characters. Each character is written to the data output stream as
//     * if by the `writeChar` method.
//     *
//     * @param      s   a `String` value to be written.
//     * @exception IOException  if the underlying stream throws an IOException.
//     */
//    @Throws(IOException::class)
//    override fun writeChars(s: String) {
//        val length = s.length
//        for (i in 0 until length) {
//            val c = s[i].toInt()
//            out.write(c and 0xFF)
//            out.write(c ushr 8 and 0xFF)
//        }
//        written += length * 2
//    }
//
//    /**
//     * Writes a string of no more than 65,535 characters
//     * to the underlying output stream using UTF-8
//     * encoding. This method first writes a two byte short
//     * in **big** endian order as required by the
//     * UTF-8 specification. This gives the number of bytes in the
//     * UTF-8 encoded version of the string, not the number of characters
//     * in the string. Next each character of the string is written
//     * using the UTF-8 encoding for the character.
//     *
//     * @param      s   the string to be written.
//     * @exception UTFDataFormatException if the string is longer than
//     * 65,535 characters.
//     * @exception IOException  if the underlying stream throws an IOException.
//     */
//    @Throws(IOException::class)
//    override fun writeUTF(s: String) {
//        val numchars = s.length
//        var numbytes = 0
//        for (i in 0 until numchars) {
//            val c = s[i].toInt()
//            if (c in 0x0001..0x007F) numbytes++ else if (c > 0x07FF) numbytes += 3 else numbytes += 2
//        }
//        if (numbytes > 65535) throw UTFDataFormatException()
//        out.write(numbytes ushr 8 and 0xFF)
//        out.write(numbytes and 0xFF)
//        for (i in 0 until numchars) {
//            val c = s[i].toInt()
//            when {
//                c in 0x0001..0x007F -> {
//                    out.write(c)
//                }
//                c > 0x07FF -> {
//                    out.write(0xE0 or (c shr 12 and 0x0F))
//                    out.write(0x80 or (c shr 6 and 0x3F))
//                    out.write(0x80 or (c and 0x3F))
//                    written += 2
//                }
//                else -> {
//                    out.write(0xC0 or (c shr 6 and 0x1F))
//                    out.write(0x80 or (c and 0x3F))
//                    written += 1
//                }
//            }
//        }
//        written += numchars + 2
//    }
//
//    fun writeUnsignedVarLong(long: Long) {
//        var value = long
//        while (value and -0x80L != 0L) {
//            writeByte((value and 0x7F or 0x80).toInt())
//            value = value ushr 7
//        }
//        writeByte((value and 0x7F).toInt())
//    }
//
//
//    /**
//     * Returns the number of bytes written to this little endian output stream.
//     * (This class is not thread-safe with respect to this method. It is
//     * possible that this number is temporarily less than the actual
//     * number of bytes written.)
//     * @return  the value of the `written` field.
//     */
//    fun size() = written
//}




//
///**
// *
// * Encodes signed and unsigned values using a common variable-length
// * scheme, found for example in
// * [
// * Google's Protocol Buffers](http://code.google.com/apis/protocolbuffers/docs/encoding.html). It uses fewer bytes to encode smaller values,
// * but will use slightly more bytes to encode large values.
// *
// *
// *
// * Signed values are further encoded using so-called zig-zag encoding
// * in order to make them "compatible" with variable-length encoding.
// */
//object VarInt {
//    /**
//     * Encodes a value using the variable-length encoding from
//     * [
// * Google Protocol Buffers](http://code.google.com/apis/protocolbuffers/docs/encoding.html). It uses zig-zag encoding to efficiently
//     * encode signed values. If values are known to be nonnegative,
//     * [.writeUnsignedVarLong] should be used.
//     *
//     * @param value value to encode
//     * @param out   to write bytes to
//     * @throws IOException if [DataOutput] throws [IOException]
//     */
//    @Throws(IOException::class)
//    fun writeSignedVarLong(value: Long, out: DataOutput) {
//        // Great trick from http://code.google.com/apis/protocolbuffers/docs/encoding.html#types
//        writeUnsignedVarLong(value shl 1 xor (value shr 63), out)
//    }
//
//    /**
//     * Encodes a value using the variable-length encoding from
//     * [
// * Google Protocol Buffers](http://code.google.com/apis/protocolbuffers/docs/encoding.html). Zig-zag is not used, so input must not be negative.
//     * If values can be negative, use [.writeSignedVarLong]
//     * instead. This method treats negative input as like a large unsigned value.
//     *
//     * @param value value to encode
//     * @param out   to write bytes to
//     * @throws IOException if [DataOutput] throws [IOException]
//     */
//    @Throws(IOException::class)
//    fun writeUnsignedVarLong(value: Long, out: DataOutput) {
//        var value = value
//        while (value and -0x80L != 0L) {
//            out.writeByte(value.toInt() and 0x7F or 0x80)
//            value = value ushr 7
//        }
//        out.writeByte(value.toInt() and 0x7F)
//    }
//
//
//    /**
//     * @see .writeSignedVarLong
//     */
//    @Throws(IOException::class)
//    fun writeSignedVarInt(value: Int, out: DataOutput) {
//        // Great trick from http://code.google.com/apis/protocolbuffers/docs/encoding.html#types
//        writeUnsignedVarInt(value shl 1 xor (value shr 31), out)
//    }
//
//    /**
//     * @see .writeUnsignedVarLong
//     */
//    @Throws(IOException::class)
//    fun writeUnsignedVarInt(value: Int, out: DataOutput) {
//        var value = value
//        while ((value and -0x80).toLong() != 0L) {
//            out.writeByte(value and 0x7F or 0x80)
//            value = value ushr 7
//        }
//        out.writeByte(value and 0x7F)
//    }
//
//    fun writeSignedVarInt(value: Int): ByteArray {
//        // Great trick from http://code.google.com/apis/protocolbuffers/docs/encoding.html#types
//        return writeUnsignedVarInt(value shl 1 xor (value shr 31))
//    }
//
//    /**
//     * @see .writeUnsignedVarLong
//     */
//    fun writeUnsignedVarInt(value: Int): ByteArray {
//        var value = value
//        val byteArrayList = ByteArray(10)
//        var i = 0
//        while ((value and -0x80).toLong() != 0L) {
//            byteArrayList[i++] = (value and 0x7F or 0x80).toByte()
//            value = value ushr 7
//        }
//        byteArrayList[i] = (value and 0x7F).toByte()
//        val out = ByteArray(i + 1)
//        while (i >= 0) {
//            out[i] = byteArrayList[i]
//            i--
//        }
//        return out
//    }
//
//    /**
//     * @param in to read bytes from
//     * @return decode value
//     * @throws IOException              if [DataInput] throws [IOException]
//     * @throws IllegalArgumentException if variable-length value does not terminate
//     * after 9 bytes have been read
//     * @see .writeSignedVarLong
//     */
//    @Throws(IOException::class)
//    fun readSignedVarLong(`in`: DataInput): Long {
//        val raw = readUnsignedVarLong(`in`)
//        // This undoes the trick in writeSignedVarLong()
//        val temp = raw shl 63 shr 63 xor raw shr 1
//        // This extra step lets us deal with the largest signed values by treating
//        // negative results from read unsigned methods as like unsigned values
//        // Must re-flip the top bit if the original read value had it set.
//        return temp xor (raw and (1L shl 63))
//    }
//
//    /**
//     * @param in to read bytes from
//     * @return decode value
//     * @throws IOException              if [DataInput] throws [IOException]
//     * @throws IllegalArgumentException if variable-length value does not terminate
//     * after 9 bytes have been read
//     * @see .writeUnsignedVarLong
//     */
//    @Throws(IOException::class)
//    fun readUnsignedVarLong(`in`: DataInput): Long {
//        var value = 0L
//        var i = 0
//        var b: Long
//        while (`in`.readByte().also { b = it.toLong() } and 0x80L.toByte() != 0.toByte()) {
//            value = value or (b and 0x7F shl i)
//            i += 7
//            require(i <= 63) { "Variable length quantity is too long" }
//        }
//        return value or (b shl i)
//    }
//
//    /**
//     * @throws IllegalArgumentException if variable-length value does not terminate
//     * after 5 bytes have been read
//     * @throws IOException              if [DataInput] throws [IOException]
//     * @see .readSignedVarLong
//     */
//    @Throws(IOException::class)
//    fun readSignedVarInt(`in`: DataInput): Int {
//        val raw = readUnsignedVarInt(`in`)
//        // This undoes the trick in writeSignedVarInt()
//        val temp = raw shl 31 shr 31 xor raw shr 1
//        // This extra step lets us deal with the largest signed values by treating
//        // negative results from read unsigned methods as like unsigned values.
//        // Must re-flip the top bit if the original read value had it set.
//        return temp xor (raw and (1 shl 31))
//    }
//
//    /**
//     * @throws IllegalArgumentException if variable-length value does not terminate
//     * after 5 bytes have been read
//     * @throws IOException              if [DataInput] throws [IOException]
//     * @see .readUnsignedVarLong
//     */
//    @Throws(IOException::class)
//    fun readUnsignedVarInt(`in`: DataInput): Int {
//        var value = 0
//        var i = 0
//        var b: Int
//        while (`in`.readByte().also { b = it.toInt() } and 0x80.toByte() != 0.toByte()) {
//            value = value or (b and 0x7F shl i)
//            i += 7
//            require(i <= 35) { "Variable length quantity is too long" }
//        }
//        return value or (b shl i)
//    }
//
//    fun readSignedVarInt(bytes: ByteArray): Int {
//        val raw = readUnsignedVarInt(bytes)
//        // This undoes the trick in writeSignedVarInt()
//        val temp = raw shl 31 shr 31 xor raw shr 1
//        // This extra step lets us deal with the largest signed values by treating
//        // negative results from read unsigned methods as like unsigned values.
//        // Must re-flip the top bit if the original read value had it set.
//        return temp xor (raw and (1 shl 31))
//    }
//
//    fun readUnsignedVarInt(bytes: ByteArray): Int {
//        var value = 0
//        var i = 0
//        var rb = Byte.MIN_VALUE
//        for (b in bytes) {
//            rb = b
//            if (b and 0x80.toByte() == 0.toByte()) {
//                break
//            }
//            (b.toInt() and 0x7f shl i)
//            value = value or ((b and 0x7f).toInt() shl i)
//            i += 7
//            require(i <= 35) { "Variable length quantity is too long" }
//        }
//        return value or (rb.toInt() shl i)
//    }
//}