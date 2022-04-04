package graphene.chain

import graphene.protocol.*
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class K121_CreditOfferObject(
    @SerialName("id")
    override val id: CreditOfferId,
    @SerialName("owner_account")
    val owner: AccountIdType, //< Owner of the fund
    @SerialName("asset_type")
    val asset: AssetIdType, //< Asset type in the fund
    @SerialName("total_balance")
    val totalBalance: ShareType, //< Total size of the fund
    @SerialName("current_balance")
    val currentBalance: ShareType, //< Usable amount in the fund
    @SerialName("fee_rate")
    val feeRate: UInt32, // = 0 //< Fee rate, the demominator is GRAPHENE_FEE_RATE_DENOM
    @SerialName("max_duration_seconds")
    val maxDurationSeconds: UInt32, // = 0 //< The time limit that borrowed funds should be repaid
    @SerialName("min_deal_amount")
    val minDealAmount: ShareType, //< Minimum amount to borrow for each new deal
    @SerialName("enabled")
    val enabled: Boolean,// = false //< Whether this offer is available
    @SerialName("auto_disable_time") @Serializable(TimePointSecSerializer::class)
    val autoDisableTime: Instant, //< The time when this offer will be disabled automatically
    @SerialName("acceptable_collateral")
    val acceptableCollateral: FlatMap<AssetIdType, PriceType>, // Types and rates of acceptable collateral
    @SerialName("acceptable_borrowers")
    val acceptableBorrowers: FlatMap<AccountIdType, ShareType>, // Allowed borrowers and their maximum amounts to borrow. No limitation if empty.
) : AbstractObject(), CreditOfferIdType {

}

