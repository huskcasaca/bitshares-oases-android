package graphene.chain

import graphene.protocol.BlockIdType
import graphene.protocol.BlockSummaryId
import graphene.protocol.BlockSummaryIdType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K208_BlockSummaryObject(
    @SerialName("id")
    override val id: BlockSummaryId,
    @SerialName("block_id")
    val blockId: BlockIdType,
) : AbstractObject(), BlockSummaryIdType {
//    block_id_type      block_id;
}
