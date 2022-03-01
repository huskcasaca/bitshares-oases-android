package bitshareskit.models

import bitshareskit.objects.GrapheneSerializable
import bitshareskit.serializer.writeGrapheneBoolean
import bitshareskit.serializer.writeGrapheneTypes
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import java.util.*

class Optional<T>(
    val field: T? = null
): GrapheneSerializable {

    companion object {
        fun <T> from(value: T?): Optional<T> {
            return Optional(value)
        }

        fun <T> empty(): Optional<T> = Optional(null)
    }

    val isPresent get() = field != null

    val fieldSafe get() = field!!

    fun get(): T = field ?: throw NoSuchElementException("No value present")

    override fun toByteArray(): ByteArray = buildPacket {
        writeGrapheneBoolean(isPresent)
        if (field != null) writeGrapheneTypes(field)
    }.readBytes()

//    override fun toJsonElement(): Any = when (field) {
//        is GrapheneSerializable -> field.toJsonElement()
//        else -> field.toString()
//    }

    override fun toJsonElement(): Any? = when (field) {
        is GrapheneSerializable -> field.toJsonElement()
        else                    -> field
    }

    override fun toString(): String {
        return field.toString()
    }

}