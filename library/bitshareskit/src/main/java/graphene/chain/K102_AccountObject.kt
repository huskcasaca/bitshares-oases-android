package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K102_AccountObject(
    @SerialName("id")
    override val id: K102_AccountIdType,
    @SerialName("membership_expiration_date")
    override val membershipExpirationDate: ChainTimePoint,

    @SerialName("registrar")
    override val registrar: K102_AccountType,
    @SerialName("referrer")
    override val referrer: K102_AccountType,
    @SerialName("lifetime_referrer")
    override val lifetimeReferrer: K102_AccountType,

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
    override val statistics: K206_AccountStatisticsType,

    @SerialName("whitelisting_accounts")
    override val whiteListingAccounts: TypeSet<K102_AccountType>,
    @SerialName("blacklisting_accounts")
    override val blackListingAccounts: TypeSet<K102_AccountType>,
    @SerialName("whitelisted_accounts")
    override val whiteListedAccounts: Set<K102_AccountType>,
    @SerialName("blacklisted_accounts")
    override val blackListedAccounts: Set<K102_AccountType>,

    @SerialName("cashback_vb") @Optional
    val cashback_vb: K113_VestingBalanceIdType = emptyIdType(),

    @SerialName("owner_special_authority")
    override val ownerSpecialAuthority: SpecialAuthority,
    @SerialName("active_special_authority")
    override val activeSpecialAuthority: SpecialAuthority,
    @SerialName("top_n_control_flags")
    override val topNControlFlags: UInt8,

) : AbstractObject(), K102_AccountType