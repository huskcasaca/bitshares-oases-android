package graphene.chain

import graphene.protocol.ChainIdType
import graphene.protocol.K211_ChainPropertyIdType
import graphene.protocol.K211_ChainPropertyType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K211_ChainPropertyObject(
    @SerialName("id")
    override val id: K211_ChainPropertyIdType,
    @SerialName("chain_id")
    val chainId: ChainIdType,
    @SerialName("immutable_parameters")
    val immutableParameters: ImmutableChainParameters,
) : AbstractObject(), K211_ChainPropertyType {

//    immutable_chain_parameters immutable_parameters;
}