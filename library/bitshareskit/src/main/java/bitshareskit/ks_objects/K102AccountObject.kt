package bitshareskit.ks_objects

import bitshareskit.ks_models.KAuthority
import bitshareskit.ks_models.KGrapheneDateTime
import bitshareskit.ks_models.KOptions
import bitshareskit.ks_object_base.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class K102AccountObject(
    @SerialName(KEY_ID) override val id: K102AccountId = emptyIdType(),
    @SerialName(KEY_MEMBERSHIP_EXPIRATION_DATE) override val membershipExpirationDate: KGrapheneDateTime = KGrapheneDateTime.STANDARD_EXPIRATION_DATE_TIME,
    @SerialName(KEY_REGISTRAR) @Serializable(with = KGrapheneIdSerializer::class) @Suppress("SERIALIZER_TYPE_INCOMPATIBLE") override val registrar: K102AccountType = emptyIdType(),
    @SerialName(KEY_REFERRER) @Serializable(with = KGrapheneIdSerializer::class) @Suppress("SERIALIZER_TYPE_INCOMPATIBLE") override val referrer: K102AccountType = emptyIdType(),
    @SerialName(KEY_LIFETIME_REFERRER) @Serializable(with = KGrapheneIdSerializer::class) @Suppress("SERIALIZER_TYPE_INCOMPATIBLE") override val lifetimeReferrer: K102AccountType = emptyIdType(),
    @SerialName(KEY_NETWORK_FEE_PERCENTAGE) override val networkFeePercentage: UInt = 0U,
    @SerialName(KEY_LIFETIME_REFERRER_FEE_PERCENTAGE) override val lifetimeReferrerFeePercentage: UInt = 0U,
    @SerialName(KEY_REFERRER_REWARD_PERCENTAGE) override val referrerRewardsFeePercentage: UInt = 0U,
    @SerialName(KEY_NAME) override val name: String = emptyString(),
    @SerialName(KEY_OWNER) override val owner: KAuthority = emptyComponent(),
    @SerialName(KEY_ACTIVE) override val active: KAuthority = emptyComponent(),
    @SerialName(KEY_OPTIONS) override val options: KOptions = emptyComponent(),
    @SerialName(KEY_WHITELISTING_ACCOUNTS) override val whiteListingAccounts: Set<@Serializable(with = KGrapheneIdSerializer::class) @Suppress("SERIALIZER_TYPE_INCOMPATIBLE") K102AccountType> = emptySet(),
    @SerialName(KEY_BLACKLISTING_ACCOUNTS) override val blackListingAccounts: Set<@Serializable(with = KGrapheneIdSerializer::class) @Suppress("SERIALIZER_TYPE_INCOMPATIBLE") K102AccountType> = emptySet(),
    @SerialName(KEY_WHITELISTED_ACCOUNTS) override val whiteListedAccounts: Set<@Serializable(with = KGrapheneIdSerializer::class) @Suppress("SERIALIZER_TYPE_INCOMPATIBLE") K102AccountType> = emptySet(),
    @SerialName(KEY_BLACKLISTED_ACCOUNTS) override val blackListedAccounts: Set<@Serializable(with = KGrapheneIdSerializer::class) @Suppress("SERIALIZER_TYPE_INCOMPATIBLE") K102AccountType> = emptySet(),
) : K000AbstractObject(), K102AccountType {

    companion object {
        const val TABLE_NAME = "account_object"

        const val KEY_MEMBERSHIP_EXPIRATION_DATE = "membership_expiration_date"
        const val KEY_REGISTRAR = "registrar"
        const val KEY_REFERRER = "referrer"
        const val KEY_LIFETIME_REFERRER = "lifetime_referrer"
        const val KEY_NETWORK_FEE_PERCENTAGE = "network_fee_percentage"
        const val KEY_LIFETIME_REFERRER_FEE_PERCENTAGE = "lifetime_referrer_fee_percentage"
        const val KEY_REFERRER_REWARD_PERCENTAGE = "referrer_rewards_percentage"
        const val KEY_NAME = "name"
        const val KEY_OWNER = "owner"
        const val KEY_ACTIVE = "active"
        const val KEY_OPTIONS = "options"
        const val KEY_STATISTICS = "statistics"
        const val KEY_WHITELISTING_ACCOUNTS = "whitelisting_accounts"
        const val KEY_BLACKLISTING_ACCOUNTS = "blacklisting_accounts"
        const val KEY_WHITELISTED_ACCOUNTS = "whitelisted_accounts"
        const val KEY_BLACKLISTED_ACCOUNTS = "blacklisted_accounts"
        const val KEY_OWNER_SPECIAL_AUTHORITY = "owner_special_authority"
        const val KEY_ACTIVE_SPECIAL_AUTHORITY = "active_special_authority"
        const val KEY_N_CONTROL_FLAGS = "top_n_control_flags"
//        const val STANDARD_EXPIRATION_DATE = "1970-01-01T00:00:00"

//        const val COMMITTEE_ACCOUNT_ID = ChainConfig.Account.COMMITTEE_ACCOUNT_ID
//        const val WITNESS_ACCOUNT_ID = ChainConfig.Account.WITNESS_ACCOUNT_ID
//        const val RELAXED_COMMITTEE_ACCOUNT_ID = ChainConfig.Account.RELAXED_COMMITTEE_ACCOUNT_ID
//        const val NULL_ACCOUNT_ID = ChainConfig.Account.NULL_ACCOUNT_ID
//        const val TEMP_ACCOUNT_ID = ChainConfig.Account.TEMP_ACCOUNT_ID
//        const val PROXY_TO_SELF_ID = ChainConfig.Account.PROXY_TO_SELF_ID
//
//        const val COMMITTEE_ACCOUNT_UID = ChainConfig.Account.COMMITTEE_ACCOUNT_INSTANCE
//        const val WITNESS_ACCOUNT_UID = ChainConfig.Account.WITNESS_ACCOUNT_INSTANCE
//        const val RELAXED_COMMITTEE_ACCOUNT_UID = ChainConfig.Account.RELAXED_COMMITTEE_ACCOUNT_INSTANCE
//        const val NULL_ACCOUNT_UID = ChainConfig.Account.NULL_ACCOUNT_INSTANCE
//        const val TEMP_ACCOUNT_UID = ChainConfig.Account.TEMP_ACCOUNT_INSTANCE
//        const val PROXY_TO_SELF_UID = ChainConfig.Account.PROXY_TO_SELF_INSTANCE
    }

}

