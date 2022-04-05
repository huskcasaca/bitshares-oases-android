package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K210_BlindedBalanceObject(
    @SerialName("id")
    override val id: BlindedBalanceId,
    @SerialName("commitment")
    val commitment: CommitmentType, // fc::ecc::commitment_type
    @SerialName("asset_id")
    val asset_id: AssetIdType,
    @SerialName("owner")
    val owner: Authority,
) : AbstractObject(), BlindedBalanceIdType {
}