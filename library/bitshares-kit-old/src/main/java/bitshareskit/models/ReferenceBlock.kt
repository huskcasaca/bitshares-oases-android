package bitshareskit.models

import bitshareskit.objects.ByteSerializable
import bitshareskit.serializer.writeGrapheneTime
import bitshareskit.serializer.writeGrapheneUInt
import bitshareskit.serializer.writeGrapheneUShort
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import java.util.*

data class ReferenceBlock(
    val refBlockNum: Int,
    val refBlockPrefix: Long,
    var expiration: Date
): ByteSerializable {

    constructor(
        headBlockNum: Long,
        headBlockId: String,
        expiration: Date
    ) : this(
        generateRefBlockNum(headBlockNum),
        generateRefBlockPrefix(headBlockId),
        expiration
    )

    companion object {
        private fun generateRefBlockNum(headBlockNum: Long): Int{
            return headBlockNum.toInt() and 0xFFFF
        }

        private fun generateRefBlockPrefix(headBlockId: String): Long{
            val hash = headBlockId.substring(8, 16)
            var prefix = ""
            for (x in 0 until 8 step 2) prefix += hash.substring(6 - x, 8 - x)
            return prefix.toLong(16)
        }
    }

    override fun toByteArray(): ByteArray {
        return buildPacket {
            writeGrapheneUShort(refBlockNum.toUShort())
            writeGrapheneUInt(refBlockPrefix.toUInt())
            writeGrapheneTime(expiration)
        }.readBytes()
    }

}