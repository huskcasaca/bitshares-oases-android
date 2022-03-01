package bitshareskit.ks_models

import bitshareskit.objects.GrapheneSerializable
import bitshareskit.ks_object_base.UInt32
import bitshareskit.serializer.writeGrapheneUInt
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = KVoteSerializer::class)
data class KVote(
    val group: UInt32 = 0U,
    val instance: UInt32 = 0U,
) : GrapheneComponent {

    companion object {

        const val COMMITTEE_GROUP = 0U
        const val WITNESS_GROUP = 1U
        const val WORKER_GROUP = 2U

        private val GROUP_RANGE = 0U..0xFFU
        private val INSTANCE_RANGE = 0U..0xFFFFFFU

        val EMPTY = KVote()

        fun fromStringId(id: String): KVote {
            return kotlin.runCatching {
                val group = id.split(":").first().toUInt()
                val uid = id.split(":").last().toUInt()
                require(group in GROUP_RANGE)
                require(uid in INSTANCE_RANGE)
                KVote(group, uid)
            }.getOrElse { EMPTY }
        }
    }

}


class KVoteSerializer : KSerializer<KVote> {
    private fun String.toVote(): KVote {
        return KVote.fromStringId(this)
    }
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Vote", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): KVote = decoder.decodeString().toVote()
    override fun serialize(encoder: Encoder, value: KVote) = encoder.encodeString(value.toString())
}
