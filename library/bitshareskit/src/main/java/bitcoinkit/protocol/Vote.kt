package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = KVoteSerializer::class)
data class VoteType(
    val group: UInt32 = 0U,
    val instance: UInt32 = 0U,
) : GrapheneComponent {

    companion object {

        const val COMMITTEE_GROUP = 0U
        const val WITNESS_GROUP = 1U
        const val WORKER_GROUP = 2U

        private val GROUP_RANGE = 0U..0xFFU
        private val INSTANCE_RANGE = 0U..0xFFFFFFU

        val EMPTY = VoteType()

        fun fromStringId(id: String): VoteType {
            return kotlin.runCatching {
                val group = id.split(":").first().toUInt()
                val uid = id.split(":").last().toUInt()
                require(group in GROUP_RANGE)
                require(uid in INSTANCE_RANGE)
                VoteType(group, uid)
            }.getOrElse { EMPTY }
        }
    }

}


class KVoteSerializer : KSerializer<VoteType> {
    private fun String.toVote(): VoteType {
        return VoteType.fromStringId(this)
    }
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Vote", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): VoteType = decoder.decodeString().toVote()
    override fun serialize(encoder: Encoder, value: VoteType) = encoder.encodeString(value.toString())
}
