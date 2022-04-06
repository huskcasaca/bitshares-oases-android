package bitshareskit.models

import bitshareskit.objects.GrapheneSerializable
import bitshareskit.serializer.writeGrapheneUInt
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes

data class Vote(
    val group: UInt,
    val instance: UInt
) : GrapheneSerializable {

    constructor(id: String) : this(
        id.split(":").first().toUInt().coerceIn(GROUP_RANGE),
        id.split(":").last().toUInt().coerceIn(INSTANCE_RANGE),
    )

    companion object {

        const val COMMITTEE_GROUP = 0U
        const val WITNESS_GROUP = 1U
        const val WORKER_GROUP = 2U

        val GROUP_RANGE = 0U..0xFFU
        val INSTANCE_RANGE = 0U..0xFFFFFFU

        val EMPTY = Vote(0U, 0U)

        fun fromStringId(id: String): Vote {
            return kotlin.runCatching {
                val group = id.split(":").first().toUInt()
                val uid = id.split(":").last().toUInt()
                require(group in GROUP_RANGE)
                require(uid in INSTANCE_RANGE)
                Vote(group, uid)
            }.getOrElse { EMPTY }
        }

    }

    override fun toByteArray(): ByteArray = buildPacket {
        writeGrapheneUInt(instance shl 8 or group)
    }.readBytes()

    override fun toJsonElement(): String = "$group:$instance"

    override fun toString(): String = "$group:$instance"

}