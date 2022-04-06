package bitshareskit.objects

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.*
import bitshareskit.models.AccountOptions
import bitshareskit.models.Authority
import bitshareskit.models.PublicKey
import kotlinx.serialization.Serializable
import org.java_json.JSONObject

@Entity(tableName = AccountObject.TABLE_NAME)
@Serializable(with = GrapheneJsonSerializer::class)
data class AccountObject(@ColumnInfo(name = "data") val rawJson: JSONObject) : GrapheneObject(rawJson) {

    companion object {
        @Ignore const val TABLE_NAME = "account_object"

        @Ignore const val KEY_MEMBERSHIP_EXPIRATION_DATE = "membership_expiration_date"
        @Ignore const val KEY_REGISTRAR = "registrar"
        @Ignore const val KEY_REFERRER = "referrer"
        @Ignore const val KEY_LIFETIME_REFERRER = "lifetime_referrer"
        @Ignore const val KEY_NETWORK_FEE_PERCENTAGE = "network_fee_percentage"
        @Ignore const val KEY_LIFETIME_REFERRER_FEE_PERCENTAGE = "lifetime_referrer_fee_percentage"
        @Ignore const val KEY_REFERRER_REWARD_PERCENTAGE = "referrer_rewards_percentage"
        @Ignore const val KEY_NAME = "name"
        @Ignore const val KEY_OWNER = "owner"
        @Ignore const val KEY_ACTIVE = "active"
        @Ignore const val KEY_OPTIONS = "options"
        @Ignore const val KEY_STATISTICS = "statistics"
        @Ignore const val KEY_WHITELISTING_ACCOUNTS = "whitelisting_accounts"
        @Ignore const val KEY_BLACKLISTING_ACCOUNTS = "blacklisting_accounts"
        @Ignore const val KEY_WHITELISTED_ACCOUNTS = "whitelisted_accounts"
        @Ignore const val KEY_BLACKLISTED_ACCOUNTS = "blacklisted_accounts"
        @Ignore const val KEY_OWNER_SPECIAL_AUTHORITY = "owner_special_authority"
        @Ignore const val KEY_ACTIVE_SPECIAL_AUTHORITY = "active_special_authority"
        @Ignore const val KEY_N_CONTROL_FLAGS = "top_n_control_flags"
        @Ignore const val STANDARD_EXPIRATION_DATE = "1970-01-01T00:00:00"

        @Ignore const val COMMITTEE_ACCOUNT_ID = ChainConfig.Account.COMMITTEE_ACCOUNT_ID
        @Ignore const val WITNESS_ACCOUNT_ID = ChainConfig.Account.WITNESS_ACCOUNT_ID
        @Ignore const val RELAXED_COMMITTEE_ACCOUNT_ID = ChainConfig.Account.RELAXED_COMMITTEE_ACCOUNT_ID
        @Ignore const val NULL_ACCOUNT_ID = ChainConfig.Account.NULL_ACCOUNT_ID
        @Ignore const val TEMP_ACCOUNT_ID = ChainConfig.Account.TEMP_ACCOUNT_ID
        @Ignore const val PROXY_TO_SELF_ID = ChainConfig.Account.PROXY_TO_SELF_ID

        @Ignore const val COMMITTEE_ACCOUNT_UID = ChainConfig.Account.COMMITTEE_ACCOUNT_INSTANCE
        @Ignore const val WITNESS_ACCOUNT_UID = ChainConfig.Account.WITNESS_ACCOUNT_INSTANCE
        @Ignore const val RELAXED_COMMITTEE_ACCOUNT_UID = ChainConfig.Account.RELAXED_COMMITTEE_ACCOUNT_INSTANCE
        @Ignore const val NULL_ACCOUNT_UID = ChainConfig.Account.NULL_ACCOUNT_INSTANCE
        @Ignore const val TEMP_ACCOUNT_UID = ChainConfig.Account.TEMP_ACCOUNT_INSTANCE
        @Ignore const val PROXY_TO_SELF_UID = ChainConfig.Account.PROXY_TO_SELF_INSTANCE

        @Ignore val COMMITTEE_ACCOUNT = createGraphene<AccountObject>(COMMITTEE_ACCOUNT_UID)
        @Ignore val WITNESS_ACCOUNT = createGraphene<AccountObject>(WITNESS_ACCOUNT_UID)
        @Ignore val RELAXED_COMMITTEE_ACCOUNT = createGraphene<AccountObject>(RELAXED_COMMITTEE_ACCOUNT_UID)
        @Ignore val NULL_ACCOUNT = createGraphene<AccountObject>(NULL_ACCOUNT_UID)
        @Ignore val TEMP_ACCOUNT = createGraphene<AccountObject>(TEMP_ACCOUNT_UID)
        @Ignore val PROXY_TO_SELF = createGraphene<AccountObject>(PROXY_TO_SELF_UID)

        val EMPTY: AccountObject = createGrapheneEmptyInstance()
    }

    // TODO: 2021/10/12 replace with isBasicAccount(timePointSecNow)
    @Ignore val isLifetimeMember: Boolean

//    account_object.hpp
//
//    bool is_lifetime_member()const
//    {
//        return membership_expiration_date == time_point_sec::maximum();
//    }
//    /// @return true if this is a basic account; false otherwise.
//    bool is_basic_account(time_point_sec now)const
//    {
//        return now > membership_expiration_date;
//    }
//    /// @return true if the account is an unexpired annual member; false otherwise.
//    /// @note This method will return false for lifetime members.
//    bool is_annual_member(time_point_sec now)const
//    {
//        return !is_lifetime_member() && !is_basic_account(now);
//    }
//    /// @return true if the account is an annual or lifetime member; false otherwise.
//    bool is_member(time_point_sec now)const
//    {
//        return !is_basic_account(now);
//    }

    @Ignore val membershipExpirationDate: String
    @Ignore val registrar: AccountObject?
    @Ignore val referrer: AccountObject?
    @Ignore val lifetimeReferrer: AccountObject?
    @Ignore val networkFeePercentage: Int
    @Ignore val lifetimeReferrerFeePercentage: Int
    @Ignore val referrerRewardsFeePercentage: Int
    @Ignore val name: String
    @Ignore val owner: Authority?
    @Ignore val active: Authority?
    @delegate:Ignore val options: AccountOptions by lazy { AccountOptions.fromJson(rawJson.optJSONObject(KEY_OPTIONS)) }
    @Ignore val whiteListingAccount: List<AccountObject>
    @Ignore val blackListingAccount: List<AccountObject>
    @Ignore val whiteListedAccount: List<AccountObject>
    @Ignore val blackListedAccount: List<AccountObject>

    @Ignore val ownerKeyAuths: Map<PublicKey, UShort>
    @Ignore val activeKeyAuths: Map<PublicKey, UShort>
    @delegate:Ignore val memoKeyAuths: Map<PublicKey, UShort> by lazy { mapOf(options.memoKey to 1U.toUShort()) }


    @Ignore var ownerAccountAuths: Map<AccountObject, UShort>
    @Ignore var activeAccountAuths: Map<AccountObject, UShort>

    @Ignore val ownerMinThreshold: UInt
    @Ignore val activeMinThreshold: UInt
    @Ignore val memoMinThreshold: UInt = 1U

    init {
        membershipExpirationDate = rawJson.optString(KEY_MEMBERSHIP_EXPIRATION_DATE, STANDARD_EXPIRATION_DATE)
        isLifetimeMember = membershipExpirationDate != STANDARD_EXPIRATION_DATE
        registrar = rawJson.optGrapheneInstance(KEY_REGISTRAR, null)
        referrer = rawJson.optGrapheneInstance(KEY_REFERRER, null)
        lifetimeReferrer = rawJson.optGrapheneInstance(KEY_LIFETIME_REFERRER, null)
        networkFeePercentage = rawJson.optInt(KEY_NETWORK_FEE_PERCENTAGE, 2000)
        lifetimeReferrerFeePercentage = rawJson.optInt(KEY_LIFETIME_REFERRER_FEE_PERCENTAGE, 3000)
        referrerRewardsFeePercentage = rawJson.optInt(KEY_REFERRER_REWARD_PERCENTAGE, 0)
        name = rawJson.optString(KEY_NAME)
        owner = rawJson.optItem(KEY_OWNER, null)
        active = rawJson.optItem(KEY_ACTIVE, null)

        whiteListingAccount = rawJson.optIterable<String>(KEY_WHITELISTING_ACCOUNTS).map { createGraphene(it) }
        blackListingAccount = rawJson.optIterable<String>(KEY_BLACKLISTING_ACCOUNTS).map { createGraphene(it) }
        whiteListedAccount = rawJson.optIterable<String>(KEY_WHITELISTED_ACCOUNTS).map { createGraphene(it) }
        blackListedAccount = rawJson.optIterable<String>(KEY_BLACKLISTED_ACCOUNTS).map { createGraphene(it) }

        ownerKeyAuths = owner?.keyAuths.orEmpty()
        activeKeyAuths = active?.keyAuths.orEmpty()

        ownerAccountAuths = owner?.accountAuths.orEmpty()
        activeAccountAuths = active?.accountAuths.orEmpty()

        ownerMinThreshold = owner?.weightThreshold ?: 0U
        activeMinThreshold = active?.weightThreshold ?: 0U

    }

    override fun equals(other: Any?): Boolean = super.equals(other)
    override fun hashCode(): Int = super.hashCode()

}

