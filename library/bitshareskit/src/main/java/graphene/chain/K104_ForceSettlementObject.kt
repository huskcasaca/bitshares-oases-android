package graphene.chain

import graphene.protocol.*
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K104_ForceSettlementObject(
    @SerialName("id")
    override val id: ForceSettlementId,
    @SerialName("owner")
    val owner: AccountIdType,
    @SerialName("balance")
    val balance: Asset,
    @SerialName("settlement_date") @Serializable(TimePointSecSerializer::class)
    val settlementDate: Instant,
) : AbstractObject(), ForceSettlementIdType {

//    asset_id_type settlement_asset_id()const
//    { return balance.asset_id; }

}
