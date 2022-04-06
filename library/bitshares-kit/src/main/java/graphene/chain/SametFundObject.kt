package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class K120_SametFundObject(
    @SerialName("id")
    override val id: SametFundId,
    @SerialName("owner_account")
    val owner: AccountIdType, //< Owner of the fund
    @SerialName("asset_type")
    val asset: AssetIdType, //< Asset type in the fund
    @SerialName("balance")
    val balance: ShareType, //< Usable amount in the fund
    @SerialName("fee_rate")
    val feeRate: UInt32, // = 0 ///< Fee rate, the demominator is GRAPHENE_FEE_RATE_DENOM
    @SerialName("unpaid_amount")
    val unpaidAmount: ShareType, //< Unpaid amount
) : AbstractObject(), SametFundIdType {

}

