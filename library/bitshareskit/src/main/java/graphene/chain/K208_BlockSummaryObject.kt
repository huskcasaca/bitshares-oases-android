package graphene.chain

import graphene.protocol.BlockIdType
import graphene.protocol.K208_BlockSummaryIdType
import graphene.protocol.K208_BlockSummaryType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K208_BlockSummaryObject(
    @SerialName("id")
    override val id: K208_BlockSummaryIdType,
    @SerialName("block_id")
    val block_id: BlockIdType,
) : AbstractObject(), K208_BlockSummaryType {
//    block_id_type      block_id;
}
