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

fun Output.writeVarInt(value: Int) {
    var curr = value
    while (curr and -0x80 != 0) {
        writeByte((curr and 0x7F or 0x80).toByte())
        curr = curr ushr 7
    }
    writeByte((curr and 0x7F).toByte())
}

fun Output.writeVarLong(value: Long) {
    var curr = value
    while (curr and -0x80L != 0L) {
        writeByte((curr and 0x7F or 0x80).toByte())
        curr = curr ushr 7
    }
    writeByte((curr and 0x7F).toByte())
}
