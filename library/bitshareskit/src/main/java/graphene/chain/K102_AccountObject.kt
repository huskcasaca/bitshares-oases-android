package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K102_AccountObject(
    @SerialName("id")
    override val id: AccountIdType,
    @SerialName("membership_expiration_date")
    override val membershipExpirationDate: ChainTimePoint,

    @SerialName("registrar")
    override val registrar: AccountType,
    @SerialName("referrer")
    override val referrer: AccountType,
    @SerialName("lifetime_referrer")
    override val lifetimeReferrer: AccountType,

    @SerialName("network_fee_percentage")
    override val networkFeePercentage: uint16_t,
    @SerialName("lifetime_referrer_fee_percentage")
    override val lifetimeReferrerFeePercentage: uint16_t,
    @SerialName("referrer_rewards_percentage")
    override val referrerRewardsFeePercentage: uint16_t,

    @SerialName("name")
    override val name: String,

    @SerialName("owner")
    override val owner: Authority,
    @SerialName("active")
    override val active: Authority,
    @SerialName("options")
    override val options: AccountOptions,

    @SerialName("num_committee_voted")
    override val numCommitteeVoted: uint16_t,
    @SerialName("statistics")
    override val statistics: AccountStatisticsType,

    @SerialName("whitelisting_accounts")
    override val whiteListingAccounts: TypeSet<AccountType>,
    @SerialName("blacklisting_accounts")
    override val blackListingAccounts: TypeSet<AccountType>,
    @SerialName("whitelisted_accounts")
    override val whiteListedAccounts: Set<AccountType>,
    @SerialName("blacklisted_accounts")
    override val blackListedAccounts: Set<AccountType>,

    @SerialName("cashback_vb")
    override val cashbackVestingBalance: Optional<VestingBalanceIdType> = optional(),

    @SerialName("owner_special_authority")
    override val ownerSpecialAuthority: TypedSpecialAuthority,
    @SerialName("active_special_authority")
    override val activeSpecialAuthority: TypedSpecialAuthority,
    @SerialName("top_n_control_flags")
    override val topNControlFlags: uint8_t,

    @SerialName("allowed_assets")
    override val allowedAssets: Optional<FlatSet<AccountType>> = optional(),
) : AbstractObject(), AccountType {

    companion object {
        const val TOP_N_CONTROL_OWNER  : uint8_t = 0x01U
        const val TOP_N_CONTROL_ACTIVE : uint8_t = 0x02U
    }

}