package graphene.chain

import graphene.protocol.*
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class K115_BalanceObject(
    @SerialName("id")
    override val id: BalanceId,
    @SerialName("owner")
    val owner: AddressType,
    @SerialName("balance")
    val balance: Asset,
    @SerialName("vesting_policy")
    val vestingPolicy: Optional<LinearVestingPolicy> = optional(),
    @SerialName("last_claim_date") @Serializable(TimePointSecSerializer::class)
    val lastClaimDate: Instant,
) : AbstractObject(), BalanceIdType {
//    bool is_vesting_balance()const
//    { return vesting_policy.valid(); }
//    asset available(fc::time_point_sec now)const
//    {
//        return is_vesting_balance()? vesting_policy->get_allowed_withdraw({balance, now, {}})
//        : balance;
//    }
//
//    asset_id_type asset_type()const { return balance.asset_id; }
}
