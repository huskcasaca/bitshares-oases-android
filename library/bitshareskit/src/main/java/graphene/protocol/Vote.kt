package graphene.protocol

import kotlinx.serialization.Serializable

@Serializable(with = VoteTypeSerializer::class)
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class VoteIdType(
    val type: VoteType = VoteType.COMMITTEE,
    val instance: uint32_t = 0U,
) : GrapheneComponent, Comparable<VoteIdType> {

    companion object {

        fun fromStringId(id: String): VoteIdType {
            return VoteIdType(
                id.substringBefore(':').toInt().let { VoteType.values()[it] },
                id.substringAfter(':').toUInt()
            )
        }
    }

    enum class VoteType {
        COMMITTEE,
        WITNESS,
        WORKER,
    }

    private val content: uint32_t = instance shl 8 or type.ordinal.toUInt()

    override fun toString(): String {
        return "${type.ordinal}:${instance}"
    }

    override fun compareTo(other: VoteIdType): Int {
        return (content - other.content).toInt()
    }

}