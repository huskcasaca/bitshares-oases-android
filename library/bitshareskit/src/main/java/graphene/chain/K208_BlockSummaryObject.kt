package graphene.chain

import graphene.protocol.BlockIdType
import graphene.protocol.BlockSummaryIdType
import graphene.protocol.BlockSummaryType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K208_BlockSummaryObject(
    @SerialName("id")
    override val id: BlockSummaryIdType,
    @SerialName("block_id")
    val block_id: BlockIdType,
) : AbstractObject(), BlockSummaryType {
//    block_id_type      block_id;
}
