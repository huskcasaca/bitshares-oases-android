package graphene.chain

import graphene.protocol.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class K102_AccountObject(
    @SerialName(KEY_ID)
    override val id: K102_AccountIdType = emptyIdType(),
    @SerialName(KEY_MEMBERSHIP_EXPIRATION_DATE)
    override val membershipExpirationDate: ChainTimePoint = ChainTimePoint.STANDARD_EXPIRATION_DATE_TIME,

    @SerialName(KEY_REGISTRAR)
    override val registrar: K102_AccountType = emptyIdType(),
    @SerialName(KEY_REFERRER)
    override val referrer: K102_AccountType = emptyIdType(),
    @SerialName(KEY_LIFETIME_REFERRER)
    override val lifetimeReferrer: K102_AccountType = emptyIdType(),

    @SerialName(KEY_NETWORK_FEE_PERCENTAGE)
    override val networkFeePercentage: UInt16 = 0U,
    @SerialName(KEY_LIFETIME_REFERRER_FEE_PERCENTAGE)
    override val lifetimeReferrerFeePercentage: UInt16 = 0U,
    @SerialName(KEY_REFERRER_REWARD_PERCENTAGE)
    override val referrerRewardsFeePercentage: UInt16 = 0U,

    @SerialName(KEY_NAME)
    override val name: String = emptyString(),

    @SerialName(KEY_OWNER)
    override val owner: Authority = emptyComponent(),
    @SerialName(KEY_ACTIVE)
    override val active: Authority = emptyComponent(),
    @SerialName(KEY_OPTIONS)
    override val options: AccountOptions = emptyComponent(),

    @SerialName(KEY_NUM_COMMITTEE_VOTED)
    override val numCommitteeVoted: UInt16 = 0U,
    @SerialName(KEY_STATISTICS)
    override val statistics: K206_AccountStatisticsType = emptyIdType(),

    @SerialName(KEY_WHITELISTING_ACCOUNTS)
    override val whiteListingAccounts: TypeSet<K102_AccountType> = sortedSetOf(),
    @SerialName(KEY_BLACKLISTING_ACCOUNTS)
    override val blackListingAccounts: TypeSet<K102_AccountType> = sortedSetOf(),
    @SerialName(KEY_WHITELISTED_ACCOUNTS)
    override val whiteListedAccounts: Set<K102_AccountType> = emptySet(),
    @SerialName(KEY_BLACKLISTED_ACCOUNTS)
    override val blackListedAccounts: Set<K102_AccountType> = emptySet(),

    @SerialName(KEY_OWNER_SPECIAL_AUTHORITY)
    override val ownerSpecialAuthority: SpecialAuthority = emptyComponent(),
    @SerialName(KEY_ACTIVE_SPECIAL_AUTHORITY)
    override val activeSpecialAuthority: SpecialAuthority = emptyComponent(),
    @SerialName(KEY_TOP_N_CONTROL_FLAGS)
    override val topNControlFlags: UInt8 = 0U,

) : K000_AbstractObject(), K102_AccountType {

    companion object {
        private const val KEY_MEMBERSHIP_EXPIRATION_DATE = "membership_expiration_date"
        private const val KEY_REGISTRAR = "registrar"
        private const val KEY_REFERRER = "referrer"
        private const val KEY_LIFETIME_REFERRER = "lifetime_referrer"
        private const val KEY_NETWORK_FEE_PERCENTAGE = "network_fee_percentage"
        private const val KEY_LIFETIME_REFERRER_FEE_PERCENTAGE = "lifetime_referrer_fee_percentage"
        private const val KEY_REFERRER_REWARD_PERCENTAGE = "referrer_rewards_percentage"
        private const val KEY_NAME = "name"
        private const val KEY_OWNER = "owner"
        private const val KEY_ACTIVE = "active"
        private const val KEY_OPTIONS = "options"

        private const val KEY_NUM_COMMITTEE_VOTED = "num_committee_voted"

        private const val KEY_STATISTICS = "statistics"
        private const val KEY_WHITELISTING_ACCOUNTS = "whitelisting_accounts"
        private const val KEY_BLACKLISTING_ACCOUNTS = "blacklisting_accounts"
        private const val KEY_WHITELISTED_ACCOUNTS = "whitelisted_accounts"
        private const val KEY_BLACKLISTED_ACCOUNTS = "blacklisted_accounts"

        private const val KEY_OWNER_SPECIAL_AUTHORITY = "owner_special_authority"
        private const val KEY_ACTIVE_SPECIAL_AUTHORITY = "active_special_authority"
        private const val KEY_TOP_N_CONTROL_FLAGS = "top_n_control_flags"

    }

}