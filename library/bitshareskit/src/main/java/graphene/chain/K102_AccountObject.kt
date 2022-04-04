package graphene.chain

import graphene.protocol.*
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K102_AccountObject(
    @SerialName("id")
    override val id: AccountId,
    @SerialName("membership_expiration_date") @Serializable(with = TimePointSecSerializer::class)
    override val membershipExpirationDate: Instant,

    @SerialName("registrar")
    override val registrar: AccountIdType,
    @SerialName("referrer")
    override val referrer: AccountIdType,
    @SerialName("lifetime_referrer")
    override val lifetimeReferrer: AccountIdType,

    @SerialName("network_fee_percentage")
    override val networkFeePercentage: UInt16,
    @SerialName("lifetime_referrer_fee_percentage")
    override val lifetimeReferrerFeePercentage: UInt16,
    @SerialName("referrer_rewards_percentage")
    override val referrerRewardsFeePercentage: UInt16,

    @SerialName("name")
    override val name: String,

    @SerialName("owner")
    override val owner: Authority,
    @SerialName("active")
    override val active: Authority,
    @SerialName("options")
    override val options: AccountOptions,

    @SerialName("num_committee_voted")
    override val numCommitteeVoted: UInt16,
    @SerialName("statistics")
    override val statistics: AccountStatisticsIdType,

    @SerialName("whitelisting_accounts")
    override val whiteListingAccounts: TypeSet<AccountIdType>,
    @SerialName("blacklisting_accounts")
    override val blackListingAccounts: TypeSet<AccountIdType>,
    @SerialName("whitelisted_accounts")
    override val whiteListedAccounts: Set<AccountIdType>,
    @SerialName("blacklisted_accounts")
    override val blackListedAccounts: Set<AccountIdType>,

    @SerialName("cashback_vb")
    override val cashbackVestingBalance: Optional<VestingBalanceId> = optional(),

    @SerialName("owner_special_authority")
    override val ownerSpecialAuthority: SpecialAuthority,
    @SerialName("active_special_authority")
    override val activeSpecialAuthority: SpecialAuthority,
    @SerialName("top_n_control_flags")
    override val topNControlFlags: UInt8,

    @SerialName("allowed_assets")
    override val allowedAssets: Optional<FlatSet<AccountIdType>> = optional(),
) : AbstractObject(), AccountIdType {

    companion object {
        const val TOP_N_CONTROL_OWNER  : UInt8 = 0x01U
        const val TOP_N_CONTROL_ACTIVE : UInt8 = 0x02U
    }

}