package graphene.chain

import graphene.protocol.*
import graphene.serializers.StaticVarSerializer
import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
data class K113_VestingBalanceObject(
    @SerialName("id")
    override val id: VestingBalanceId,
    // Account which owns and may withdraw from this vesting balance
    @SerialName("owner")
    val owner: AccountIdType,
    // Total amount remaining in this vesting balance
    // Includes the unvested funds, and the vested funds which have not yet been withdrawn
    @SerialName("balance")
    val balance: Asset,
    // The vesting policy stores details on when funds vest, and controls when they may be withdrawn
    @SerialName("policy")
    val policy: VestingPolicy,
    // type of the vesting balance
    @SerialName("balance_type")
    val balanceType: VestingBalanceType = VestingBalanceType.UNSPECIFIED,
) : AbstractObject(), VestingBalanceIdType {


//
//    vesting_balance_object() {}
//
//    ///@brief Deposit amount into vesting balance, requiring it to vest before withdrawal
//    void deposit(const fc::time_point_sec& now, const asset& amount);
//    bool is_deposit_allowed(const fc::time_point_sec& now, const asset& amount)const;
//
//    /// @brief Deposit amount into vesting balance, making the new funds vest immediately
//    void deposit_vested(const fc::time_point_sec& now, const asset& amount);
//    bool is_deposit_vested_allowed(const fc::time_point_sec& now, const asset& amount)const;
//
//    /**
//     * Used to remove a vesting balance from the VBO. As well as the
//     * balance field, coin_seconds_earned and
//     * coin_seconds_earned_last_update fields are updated.
//     *
//     * The money doesn't "go" anywhere; the caller is responsible for
//     * crediting it to the proper account.
//     */
//    void withdraw(const fc::time_point_sec& now, const asset& amount);
//    bool is_withdraw_allowed(const fc::time_point_sec& now, const asset& amount)const;
//
//    /**
//     * Get amount of allowed withdrawal.
//     */
//    asset get_allowed_withdraw(const time_point_sec& now)const;
}

@Serializable(with = VestingPolicySerializer::class)
sealed class VestingPolicy

@Serializable
data class LinearVestingPolicy(
    // This is the time at which funds begin vesting.
    @SerialName("begin_timestamp") @Serializable(TimePointSecSerializer::class)
    val beginTimestamp: Instant,
    // No amount may be withdrawn before this many seconds of the vesting period have elapsed.
    @SerialName("vesting_cliff_seconds")
    val vestingCliffSeconds: UInt32 = 0U,
    // Duration of the vesting period, in seconds. Must be greater than 0 and greater than vesting_cliff_seconds.
    @SerialName("vesting_duration_seconds")
    val vestingDurationSeconds: UInt32 = 0U,
    // The total amount of asset to vest.
    @SerialName("begin_balance")
    val beginBalance: ShareType,
) : VestingPolicy() {
//    /// This is the time at which funds begin vesting.
//    fc::time_point_sec begin_timestamp;
//    /// No amount may be withdrawn before this many seconds of the vesting period have elapsed.
//    uint32_t vesting_cliff_seconds = 0;
//    /// Duration of the vesting period, in seconds. Must be greater than 0 and greater than vesting_cliff_seconds.
//    uint32_t vesting_duration_seconds = 0;
//    /// The total amount of asset to vest.
//    share_type begin_balance;
//
//    asset get_allowed_withdraw(const vesting_policy_context& ctx)const;
//    bool is_deposit_allowed(const vesting_policy_context& ctx)const;
//    bool is_deposit_vested_allowed(const vesting_policy_context&)const { return false; }
//    bool is_withdraw_allowed(const vesting_policy_context& ctx)const;
//    void on_deposit(const vesting_policy_context& ctx);
//    void on_deposit_vested(const vesting_policy_context&)
//    { FC_THROW( "May not deposit vested into a linear vesting balance." ); }
//    void on_withdraw(const vesting_policy_context& ctx);
}

@Serializable
data class CddVestingPolicy(
    @SerialName("vesting_seconds")
    val vesting_seconds: UInt32? = 0U,
    @SerialName("coin_seconds_earned")
    val coin_seconds_earned: UInt128,
    /** while coindays may accrue over time, none may be claimed before first_claim date  */
    @SerialName("start_claim") @Serializable(TimePointSecSerializer::class)
    val start_claim: Instant,
    @SerialName("coin_seconds_earned_last_update") @Serializable(TimePointSecSerializer::class)
    val coin_seconds_earned_last_update: Instant,
) : VestingPolicy()
//    /**
//     * Compute coin_seconds_earned.  Used to
//     * non-destructively figure out how many coin seconds
//     * are available.
//     */
//    fc::uint128_t compute_coin_seconds_earned(const vesting_policy_context& ctx)const;
//
//    /**
//     * Update coin_seconds_earned and
//     * coin_seconds_earned_last_update fields; called by both
//     * on_deposit() and on_withdraw().
//     */
//    void update_coin_seconds_earned(const vesting_policy_context& ctx);
//
//    asset get_allowed_withdraw(const vesting_policy_context& ctx)const;
//    bool is_deposit_allowed(const vesting_policy_context& ctx)const;
//    bool is_deposit_vested_allowed(const vesting_policy_context& ctx)const;
//    bool is_withdraw_allowed(const vesting_policy_context& ctx)const;
//    void on_deposit(const vesting_policy_context& ctx);
//    void on_deposit_vested(const vesting_policy_context& ctx);
//    void on_withdraw(const vesting_policy_context& ctx);


@Serializable
class InstantVestingPolicy(
    @Transient
    val reserved: Unit = Unit
) : VestingPolicy() {
//    asset get_allowed_withdraw(const vesting_policy_context& ctx)const;
//    bool is_deposit_allowed(const vesting_policy_context& ctx)const;
//    bool is_deposit_vested_allowed(const vesting_policy_context&)const { return false; }
//    bool is_withdraw_allowed(const vesting_policy_context& ctx)const;
//    void on_deposit(const vesting_policy_context& ctx);
//    void on_deposit_vested(const vesting_policy_context&);
//    void on_withdraw(const vesting_policy_context& ctx);
}

@Serializable
enum class VestingBalanceType {
    @SerialName("unspecified") UNSPECIFIED,
    @SerialName("cashback") CASHBACK,
    @SerialName("worker") WORKER,
    @SerialName("witness") WITNESS,
    @SerialName("market_fee_sharing") MARKET_FEE_SHARING,
}

object VestingPolicySerializer : StaticVarSerializer<VestingPolicy>(
    listOf(
        LinearVestingPolicy::class,
        CddVestingPolicy::class,
        InstantVestingPolicy::class,
    )
)
