package graphene.chain

import graphene.protocol.ChainIdType
import graphene.protocol.ChainPropertyId
import graphene.protocol.ChainPropertyIdType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K211_ChainPropertyObject(
    @SerialName("id")
    override val id: ChainPropertyId,
    @SerialName("chain_id")
    val chainId: ChainIdType,
    @SerialName("immutable_parameters")
    val immutableParameters: ImmutableChainParameters,
) : AbstractObject(), ChainPropertyIdType {

//    immutable_chain_parameters immutable_parameters;
}