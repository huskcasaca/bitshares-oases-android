package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K210_BlindedBalanceObject(
    @SerialName("id")
    override val id: K210_BlindedBalanceIdType,
//    @SerialName("commitment")
//    val commitment: commitment_type, // fc::ecc::commitment_type
    @SerialName("asset_id")
    val asset_id: K103_AssetType,
    @SerialName("owner")
    val owner: Authority,
) : AbstractObject(), K210_BlindedBalanceType {
}