package graphene.protocol

import graphene.serializers.TimePointSecSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable


typealias account_id_type = AccountIdType
typealias uint64_t = UInt64
typealias uint32_t = UInt32
typealias uint16_t = UInt16
typealias uint8_t = UInt8
typealias share_type = ShareType
typealias time_point_sec = @Serializable(with = TimePointSecSerializer::class) Instant
typealias extensions_type = ExtensionsType
typealias asset = Asset
typealias memo_data = MemoData
typealias bool = Boolean
typealias string = String
typealias credit_deal_id_type = CreditDealIdType
typealias asset_id_type = AssetIdType
typealias flat_set<V> = FlatSet<V>
typealias flat_map<K, V> = FlatMap<K, V>
typealias void_t = Unit

@Serializable
sealed class Operation

/*  0 */ @Serializable data class TransferOperation(
    val fee: Asset,
    // Account to transfer asset from
    val from: AccountIdType,
    // Account to transfer asset to
    val to: AccountIdType,
    // The amount of asset to transfer from @ref from to @ref to
    val amount: Asset,
    // User provided data encrypted to the memo key of the "to" account
    val memo: Optional<MemoData>,
    val extensions: ExtensionsType,
) : Operation()
/*  1 */ @Serializable data class LimitOrderCreateOperation(
    val fee: Asset,
    val seller: AccountIdType,
    val amount_to_sell: Asset,
    val min_to_receive: Asset,

// The order will be removed from the books if not filled by expiration
// Upon expiration, all unsold asset will be returned to seller
    val expiration: time_point_sec, // = MAXIMUM

// If this flag is set the entire order must be filled or the operation is rejected
    val fill_or_kill: bool = false,
    val extensions: ExtensionsType,
) : Operation()
/*  2 */ @Serializable data class LimitOrderCancelOperation(
    val fee: asset,
    val order: LimitOrderIdType,
    /** must be order->seller  */
    val fee_paying_account: account_id_type,
    val extensions: extensions_type,
) : Operation()

/*  3 */ @Serializable data class CallOrderUpdateOperation(
    val fee: asset,
    val funding_account : account_id_type,// pays fee, collateral, and cover
    val delta_collateral : asset,// the amount of collateral to add to the margin position
    val delta_debt : asset,// the amount of the debt to be paid off, may be negative to issue new debt
    val extensions: options_type,
) : Operation() {
//    typealias extensions_type = extension<options_type>, // note: this will be jsonified to {...} but no longer [...]
    @Serializable
    data class options_type(
        val target_collateral_ratio: Optional<uint16_t> = optional() // maximum CR to maintain when selling collateral on margin call
    ): Extension<options_type>

}
/*  4 */ @Serializable data class FillOrderOperation(
    val order_id: ObjectIdType,
    val account_id: account_id_type,
    val pays: asset,
    val receives: asset,
    val fee // paid by receiving account
    : asset,
    val fill_price: PriceType,
    val is_maker: bool,
) : Operation()           // Virtual


/*  5 */ @Serializable data class AccountCreateOperation(
    val fee: asset,

// This account pays the fee. Must be a lifetime member.
    val registrar: account_id_type,

// This account receives a portion of the fee split between registrar and referrer. Must be a member.
    val referrer: account_id_type,

// Of the fee split between registrar and referrer, this percentage goes to the referrer. The rest goes to the
// registrar.
    val referrer_percent: uint16_t, // = 0

    val name: string,
    val owner: Authority,
    val active: Authority,

    val options: AccountOptions,
    val extensions: Ext,
) : Operation() {

    @Serializable
    data class Ext(
        val null_ext: Optional<void_t>,
        val owner_special_authority: Optional<SpecialAuthority>,
        val active_special_authority: Optional<SpecialAuthority>,
        val buyback_options: Optional<BuybackAccountOptions>,
    ): Extension<Ext>

}

/*  6 */ @Serializable data class AccountUpdateOperation(
    val fee: asset,

// The account to update
    val account: account_id_type,

// New owner authority. If set, this operation requires owner authority to execute.
    val owner: Optional<Authority>,

// New active authority. This can be updated by the current active authority.
    val active: Optional<Authority>,

// New account options
    val new_options: Optional<AccountOptions>,
    val extensions: Ext,
) : Operation() {

    @Serializable
    data class Ext(
        val null_ext: Optional<void_t>,
        val owner_special_authority: Optional<SpecialAuthority>,
        val active_special_authority: Optional<SpecialAuthority>,
    )
}

/*  7 */ @Serializable data class AccountWhitelistOperation(
// Paid by authorizing_account
    val fee: asset,

// The account which is specifying an opinion of another account
    val authorizing_account: account_id_type,

// The account being opined about
    val account_to_list: account_id_type,

// The new white and blacklist status of account_to_list, as determined by authorizing_account
// This is a bitfield using values defined in the account_listing enum
    val new_listing: uint8_t = no_listing
    val extensions: extensions_type,
) : Operation() {
    @Serializable
    enum class account_listing {
        no_listing = 0x0, // No opinion is specified about this account
        white_listed = 0x1, // This account is whitelisted, but not blacklisted
        black_listed = 0x2, // This account is blacklisted, but not whitelisted
        white_and_black_listed = white_listed | black_listed // This account is both whitelisted and blacklisted
    }
}

/*  8 */ @Serializable data class AccountUpgradeOperation(
    val fee: asset,

// The account to upgrade; must not already be a lifetime member
    val account_to_upgrade: account_id_type,

// If true, the account will be upgraded to a lifetime member; otherwise, it will add a year to the subscription
    val upgrade_to_lifetime_member: bool = false
    val extensions: extensions_type,
) : Operation()

/*  9 */ @Serializable data class AccountTransferOperation(
    val fee: asset,
    val account_id: account_id_type,
    val new_owner: account_id_type,
    val extensions: extensions_type,
) : Operation()
/* 10 */ @Serializable data class AssetCreateOperation(
    val fee: asset,
    // This account must sign and pay the fee for this operation. Later, this account may update the asset
    val issuer: account_id_type,
    // The ticker symbol of this asset
    val symbol: string,
    // Number of digits to the right of decimal point, must be less than or equal to 12
    val precision: uint8_t, // = 0
    // Options common to all assets.
    // @note common_options.core_exchange_rate technically needs to store the asset ID of this new asset. Since this
    // ID is not known at the time this operation is created, create this price as though the new asset has instance
    // ID 1, and the chain will overwrite it with the new asset's ID.
    val common_options: AssetOptions,
    // Options only available for BitAssets. MUST be non-null if and only if the asset is market-issued.
    val bitasset_opts: Optional<BitassetOptions>,
    // For BitAssets, set this to true if the asset implements a prediction market; false otherwise
    val is_prediction_market: bool, // = false,
    val extensions: extensions_type,
) : Operation()

/* 11 */ @Serializable data class AssetUpdateOperation(
    val fee: asset,
    val issuer: account_id_type,
    val asset_to_update: asset_id_type,
    // If the asset is to be given a new issuer, specify his ID here.
    val new_issuer: Optional<account_id_type>,
    val new_options: AssetOptions,
    val extensions: Ext,
) : Operation() {

    @Serializable
    data class Ext(
        // After BSIP48, the precision of an asset can be updated if no supply is available
        // @note The parties involved still need to be careful
        val new_precision: Optional<uint8_t>,
        // After BSIP48, if this option is set to true, the asset's core_exchange_rate won't be updated.
        // This is especially useful for committee-owned bitassets which can not be updated quickly.
        val skip_core_exchange_rate: Optional<bool>,
    ) : Extension<Ext>

}

/* 12 */ @Serializable data class AssetUpdateBitassetOperation(
    val fee: asset,
    val issuer: account_id_type,
    val asset_to_update: asset_id_type,
    val new_options: BitassetOptions,
    val extensions: extensions_type,
) : Operation()

/* 13 */ @Serializable data class AssetUpdateFeedProducersOperation(
    val fee: asset,
    val issuer: account_id_type,
    val asset_to_update: asset_id_type,
    val new_feed_producers: flat_set<account_id_type>,
    val extensions: extensions_type,
) : Operation()

/* 14 */ @Serializable data class AssetIssueOperation(
    val fee: asset,
    val issuer: account_id_type, // Must be asset_to_issue->asset_id->issuer
    val asset_to_issue: asset,
    val issue_to_account: account_id_type,
    /** user provided data encrypted to the memo key of the "to" account  */
    val memo: Optional<memo_data>,
    val extensions: extensions_type,
) : Operation()

/* 15 */ @Serializable data class AssetReserveOperation(
    val fee: asset,
    val payer: account_id_type,
    val amount_to_reserve: asset,
    val extensions: extensions_type,
) : Operation()

/* 16 */ @Serializable data class AssetFundFeePoolOperation(
    val fee: asset, // core asset
    val from_account: account_id_type,
    val asset_id: asset_id_type,
    val amount: share_type, // core asset
    val extensions: extensions_type,
) : Operation()

/* 17 */ @Serializable data class AssetSettleOperation(
    val fee: asset,
    // Account requesting the force settlement. This account pays the fee
    val account: account_id_type,
    // Amount of asset to force settle. This must be a market-issued asset
    val amount: asset,
    val extensions: extensions_type,
) : Operation()

/* 18 */ @Serializable data class AssetGlobalSettleOperation(
    val fee: asset,
    val issuer: account_id_type, // must equal issuer of @ref asset_to_settle
    val asset_to_settle: asset_id_type,
    val settle_price: PriceType,
    val extensions: extensions_type,
) : Operation()

/* 19 */ @Serializable data class AssetPublishFeedOperation(
    val fee: asset,// paid for by publisher
    val publisher: account_id_type,
    val asset_id: asset_id_type, // asset for which the feed is published
    val feed: PriceFeed,
    val extensions: Ext,
) : Operation() {
    @Serializable
    data class Ext(
        // After BSIP77, price feed producers can feed ICR too
        val initial_collateral_ratio: Optional<uint16_t>  // BSIP-77
    ): Extension<Ext>
}

/* 20 */ @Serializable data class WitnessCreateOperation(
    val fee: asset,
    // The account which owns the witness. This account pays the fee for this operation.
    val witness_account: account_id_type,
    val url: string,
    val block_signing_key: PublicKeyType,
) : Operation()

/* 21 */ @Serializable data class WitnessUpdateOperation(
    val fee: asset,
    // The witness object to update.
    val witness: WitnessIdType,
    // The account which owns the witness. This account pays the fee for this operation.
    val witness_account: account_id_type,
    // The new URL.
    val new_url: Optional<string>,
    // The new block signing key.
    val new_signing_key: Optional<PublicKeyType>,
) : Operation()

/* 22 */ @Serializable data class ProposalCreateOperation(
    val fee: asset,
    val fee_paying_account: account_id_type,
    val proposed_ops: List<op_wrapper>,
    val expiration_time: time_point_sec,
    val review_period_seconds: Optional<uint32_t>,
    val extensions: extensions_type,
) : Operation()

/* 23 */ @Serializable data class ProposalUpdateOperation(
    val fee_paying_account: account_id_type,
    val fee: asset,
    val proposal: ProposalIdType,
    val active_approvals_to_add: flat_set<account_id_type>,
    val active_approvals_to_remove: flat_set<account_id_type>,
    val owner_approvals_to_add: flat_set<account_id_type>,
    val owner_approvals_to_remove: flat_set<account_id_type>,
    val key_approvals_to_add: flat_set<PublicKeyType>,
    val key_approvals_to_remove: flat_set<PublicKeyType>,
    val extensions: extensions_type,
) : Operation()

/* 24 */ @Serializable data class ProposalDeleteOperation(
    val fee_paying_account: account_id_type,
    val using_owner_authority: bool = false,
    val fee: asset,
    val proposal: ProposalIdType,
    val extensions: extensions_type,
) : Operation()

/* 25 */ @Serializable data class WithdrawPermissionCreateOperation(
    val fee: asset,
    // The account authorizing withdrawals from its balances
    val withdraw_from_account: account_id_type,
    // The account authorized to make withdrawals from withdraw_from_account
    val authorized_account: account_id_type,
    // The maximum amount authorized_account is allowed to withdraw in a given withdrawal period
    val withdrawal_limit: asset,
    // Length of the withdrawal period in seconds
    val withdrawal_period_sec: uint32_t, // = 0
    // The number of withdrawal periods this permission is valid for
    val periods_until_expiration: uint32_t, // = 0
    // Time at which the first withdrawal period begins; must be in the future
    val period_start_time: time_point_sec,
) : Operation()
/* 26 */ @Serializable data class WithdrawPermissionUpdateOperation(
    val fee: asset,
    // This account pays the fee. Must match permission_to_update->withdraw_from_account
    val withdraw_from_account: account_id_type,
    // The account authorized to make withdrawals. Must match permission_to_update->authorized_account
    val authorized_account: account_id_type,
    // ID of the permission which is being updated
    val permission_to_update: WithdrawPermissionIdType,
    // New maximum amount the withdrawer is allowed to charge per withdrawal period
    val withdrawal_limit: asset,
    // New length of the period between withdrawals
    val withdrawal_period_sec: uint32_t = 0
    // New beginning of the next withdrawal period; must be in the future
    val period_start_time: time_point_sec,
    // The new number of withdrawal periods for which this permission will be valid
    val periods_until_expiration: uint32_t = 0
) : Operation()

/* 27 */ @Serializable data class WithdrawPermissionClaimOperation(
    // Paid by withdraw_to_account
    val fee: asset,
    // ID of the permission authorizing this withdrawal
    val withdraw_permission: WithdrawPermissionIdType,
    // Must match withdraw_permission->withdraw_from_account
    val withdraw_from_account: account_id_type,
    // Must match withdraw_permision->authorized_account
    val withdraw_to_account: account_id_type,
    // Amount to withdraw. Must not exceed withdraw_permission->withdrawal_limit
    val amount_to_withdraw: asset,
    // Memo for withdraw_from_account. Should generally be encrypted with withdraw_from_account->memo_key
    val memo: Optional<memo_data>,
) : Operation()

/* 28 */ @Serializable data class WithdrawPermissionDeleteOperation(
    val fee: asset,
    // Must match withdrawal_permission->withdraw_from_account. This account pays the fee.
    val withdraw_from_account: account_id_type,
    // The account previously authorized to make withdrawals. Must match withdrawal_permission->authorized_account
    val authorized_account: account_id_type,
    // ID of the permission to be revoked.
    val withdrawal_permission: WithdrawPermissionIdType,
) : Operation()

/* 29 */ @Serializable data class CommitteeMemberCreateOperation(
    val fee: asset,
    // The account which owns the committee_member. This account pays the fee for this operation.
    val committee_member_account: account_id_type,
    val url: string,
) : Operation()

/* 30 */ @Serializable data class CommitteeMemberUpdateOperation(
    val fee: asset,
    // The committee member to update.
    val committee_member: CommitteeMemberIdType,
    // The account which owns the committee_member. This account pays the fee for this operation.
    val committee_member_account: account_id_type,
    val new_url: Optional<string>,
) : Operation()
/* 31 */ @Serializable data class CommitteeMemberUpdateGlobalParametersOperation(
    val fee: asset,
    val new_parameters: ChainParameters,
) : Operation()

/* 32 */ @Serializable data class VestingBalanceCreateOperation(
    val fee: asset,
    val creator: account_id_type, // Who provides funds initially
    val owner: account_id_type, // Who is able to withdraw the balance
    val amount: asset,
    val policy: VestingPolicyInitializer,
) : Operation()


/* 33 */ @Serializable data class VestingBalanceWithdrawOperation(
    val fee: asset,
    val vesting_balance: VestingBalanceIdType,
    val owner // Must be vesting_balance.owner
    : account_id_type,
    val amount: asset,
) : Operation()
/* 34 */ @Serializable data class WorkerCreateOperation(
    val fee: asset,
    val owner: account_id_type,
    val work_begin_date: time_point_sec,
    val work_end_date: time_point_sec,
    val daily_pay: share_type,
    val name: string,
    val url: string,
    // This should be set to the initializer appropriate for the type of worker to be created.
    val initializer: WorkerInitializer,
) : Operation()

/* 35 */ @Serializable data class CustomOperation(
    val fee: asset,
    val payer: account_id_type,
    val required_auths: flat_set<account_id_type>,
    val id: uint16_t = 0
    val data: List<Char>,
) : Operation()

/* 36 */ @Serializable data class AssertOperation(
    val fee: asset,
    val fee_paying_account: account_id_type,
    val predicates: List<Predicate>,
    val required_auths: flat_set<account_id_type>,
    val extensions: extensions_type,
) : Operation()

/* 37 */ @Serializable data class BalanceClaimOperation(
    val fee: asset,
    val deposit_to_account: account_id_type,
    val balance_to_claim: BalanceIdType,
    val balance_owner_key: PublicKeyType,
    val total_claimed: asset,
) : Operation()

/* 38 */ @Serializable data class OverrideTransferOperation(
    val fee: asset,
    val issuer: account_id_type,
    // Account to transfer asset from
    val from: account_id_type,
    // Account to transfer asset to
    val to: account_id_type,
    // The amount of asset to transfer from @ref from to @ref to
    val amount: asset,
    // User provided data encrypted to the memo key of the "to" account
    val memo: Optional<memo_data>,
    val extensions: extensions_type,
) : Operation()

/* 39 */ @Serializable data class TransferToBlindOperation(
    val fee: asset,
    val amount: asset,
    val from: account_id_type,
    val blinding_factor: BlindFactorType,
    val outputs: List<BlindOutput>,
) : Operation()

/* 40 */ @Serializable data class BlindTransferOperation(
    val fee: asset,
    val inputs: List<BlindInput>,
    val outputs: List<BlindOutput>,
) : Operation()

/* 41 */ @Serializable data class TransferFromBlindOperation(
    val fee: asset,
    val amount: asset,
    val to: account_id_type,
    val blinding_factor: BlindFactorType,
    val inputs: List<BlindInput>,
) : Operation()

/* 42 */ @Serializable data class AssetSettleCancelOperation(
    val fee: asset,
    val settlement: ForceSettlementIdType,
    // Account requesting the force settlement. This account pays the fee
    val account: account_id_type,
    // Amount of asset to force settle. This must be a market-issued asset
    val amount: asset,
) : Operation()  // Virtual

/* 43 */ @Serializable data class AssetClaimFeesOperation(
    val fee: asset,
    val issuer: account_id_type, // must match issuer of asset from which we claim fees
    val amount_to_claim: asset,
    val extensions: extension<AdditionalOptionsType>,
) : Operation()

/* 44 */ @Serializable data class FbaDistributeOperation(
    val fee: asset, // always zero
    val account_id: account_id_type,
    // We use object_id_type because this is an implementaton object, and therefore is not known to the protocol library
    val fba_id: ObjectIdType,
    val amount: share_type,
) : Operation()       // Virtual

/* 45 */ @Serializable data class BidCollateralOperation(
    val fee: asset,
    val bidder: account_id_type, // pays fee and additional collateral
    val additional_collateral: asset, // the amount of collateral to bid for the debt
    val debt_covered: asset, // the amount of debt to take over
    val extensions: extensions_type,
) : Operation()

/* 46 */ @Serializable data class ExecuteBidOperation(
    val bidder: account_id_type,
    val debt: asset,
    val collateral: asset,
    val fee: asset,
) : Operation() // Virtual

/* 47 */ @Serializable data class AssetClaimPoolOperation(
    val fee: asset,
    val issuer: account_id_type,
    val asset_id: asset_id_type, // fee.asset_id must != asset_id
    val amount_to_claim: asset, // core asset
    val extensions: extensions_type,
) : Operation()

/* 48 */ @Serializable data class AssetUpdateIssuerOperation(
    val fee: asset,
    val issuer: account_id_type,
    val asset_to_update: asset_id_type,
    val new_issuer: account_id_type,
    val extensions: extensions_type,
) : Operation()

/* 49 */ @Serializable data class HtlcCreateOperation(
    // paid to network
    val fee: asset,
    // where the held monies are to come from
    val from: account_id_type,
    // where the held monies will go if the preimage is provided
    val to: account_id_type,
    // the amount to hold
    val amount: asset,
    // the (typed) hash of the preimage
    val preimage_hash: HtlcHash,
    // the size of the preimage
    val preimage_size: uint16_t,
    // The time the funds will be returned to the source if not claimed
    val claim_period_seconds: uint32_t,
) : Operation()

/* 50 */ @Serializable data class HtlcRedeemOperation(
    // paid to network
    val fee: asset,
    // the object we are attempting to update
    val htlc_id: HtlcIdType,
    // who is attempting to update the transaction
    val redeemer: account_id_type,
    // the preimage (not used if after epoch timeout)
    val preimage: List<Char>,
    // for future expansion
    val extensions: extensions_type,
) : Operation()

/* 51 */ @Serializable data class HtlcRedeemedOperation(
    val htlc_id: HtlcIdType,
    val from: account_id_type,, var to:account_id_type,, var redeemer:account_id_type,
    val amount: asset,
    val htlc_preimage_hash: HtlcHash,
    val htlc_preimage_size: uint16_t,
    val fee: asset,
    val preimage: List<Char>,
) : Operation()         // Virtual

/* 52 */ @Serializable data class HtlcExtendOperation(
    // paid to network
    val fee: asset,
    // the object we are attempting to update
    val htlc_id: HtlcIdType,
    // who is attempting to update the transaction
    val update_issuer: account_id_type,
    // how much to add
    val seconds_to_add: uint32_t,
    // for future expansion
    val extensions: extensions_type,
) : Operation()

// TODO: 2022/4/5
/* 53 */ @Serializable data class HtlcRefundOperation(
    val fee: asset,
    val htlc_id: HtlcIdType, // of the associated htlc object; it is deleted during emittance of this operation
    val to: account_id_type,
    val original_htlc_recipient: account_id_type,
    val htlc_amount: asset,
    val htlc_preimage_hash: HtlcHash,
    val htlc_preimage_size: uint16_t,
) : Operation()           // Virtual

/* 54 */ @Serializable data class CustomAuthorityCreateOperation(
    // Operation fee
    val fee: asset,
    // Account which is setting the custom authority; also pays the fee
    val account: account_id_type,
    // Whether the custom authority is enabled or not
    val enabled: bool,
    // Date when custom authority becomes active
    val valid_from: time_point_sec,
    // Expiration date for custom authority
    val valid_to: time_point_sec,
    // Tag of the operation this custom authority can authorize
    val operation_type: UnsignedInt,
    // Authentication requirements for the custom authority
    val auth: Authority,
    // Restrictions on operations this custom authority can authenticate
    val restrictions: List<Restriction>,
    val extensions: extensions_type,
) : Operation()

/* 55 */ @Serializable data class CustomAuthorityUpdateOperation(
    // Operation fee
    val fee: asset,
    // Account which owns the custom authority to update; also pays the fee
    val account: account_id_type,
    // ID of the custom authority to update
    val authority_to_update: CustomAuthorityIdType,
    // Change to whether the custom authority is enabled or not
    val new_enabled: Optional<bool>,
    // Change to the custom authority begin date
    val new_valid_from: Optional<time_point_sec>,
    // Change to the custom authority expiration date
    val new_valid_to: Optional<time_point_sec>,
    // Change to the authentication for the custom authority
    val new_auth: Optional<Authority>,
    // Set of IDs of restrictions to remove
    val restrictions_to_remove: flat_set<uint16_t>,
    // Vector of new restrictions
    val restrictions_to_add: List<Restriction>,
    val extensions: extensions_type,
) : Operation()
/* 56 */ @Serializable data class CustomAuthorityDeleteOperation(
    // Operation fee
    val fee: asset,
    // Account which owns the custom authority to update; also pays the fee
    val account: account_id_type,
    // ID of the custom authority to delete
    val authority_to_delete: CustomAuthorityIdType,
    val extensions: extensions_type,
) : Operation()

/* 57 */ @Serializable data class TicketCreateOperation(
    val fee: asset, // Operation fee
    val account: account_id_type, // The account who creates the ticket
    val target_type: UnsignedInt, // The target ticket type, see @ref ticket_type
    val amount: asset, // The amount of the ticket
    val extensions: extensions_type, // Unused. Reserved for future use.
) : Operation()

/* 58 */ @Serializable data class TicketUpdateOperation(
    val fee: asset, // Operation fee
    val ticket: TicketIdType, // The ticket to update
    val account: account_id_type, // The account who owns the ticket
    val target_type: UnsignedInt, // New target ticket type, see @ref ticket_type
    val amount_for_new_target: Optional<asset>, // The amount to be used for the new target
    val extensions: extensions_type, // Unused. Reserved for future use.
) : Operation()

/* 59 */ @Serializable data class LiquidityPoolCreateOperation(
    val fee: asset, // Operation fee
    val account: account_id_type, // The account who creates the liquidity pool
    val asset_a: asset_id_type, // Type of the first asset in the pool
    val asset_b: asset_id_type, // Type of the second asset in the pool
    val share_asset: asset_id_type, // Type of the share asset aka the LP token
    val taker_fee_percent: uint16_t = 0 // Taker fee percent
    val withdrawal_fee_percent: uint16_t = 0 // Withdrawal fee percent
    val extensions: extensions_type, // Unused. Reserved for future use.
) : Operation()

/* 60 */ @Serializable data class LiquidityPoolDeleteOperation(
    val fee: asset, // Operation fee
    val account: account_id_type, // The account who owns the liquidity pool
    val pool: LiquidityPoolIdType, // ID of the liquidity pool
    val extensions: extensions_type, // Unused. Reserved for future use.
) : Operation()


/* 61 */ @Serializable data class LiquidityPoolDepositOperation(
    val fee: asset, // Operation fee
    val account: account_id_type, // The account who deposits to the liquidity pool
    val pool: LiquidityPoolIdType, // ID of the liquidity pool
    val amount_a: asset, // The amount of the first asset to deposit
    val amount_b: asset, // The amount of the second asset to deposit
    val extensions: extensions_type, // Unused. Reserved for future use.

) : Operation()
/* 62 */ @Serializable data class LiquidityPoolWithdrawOperation(
    val fee: asset, // Operation fee
    val account: account_id_type, // The account who withdraws from the liquidity pool
    val pool: LiquidityPoolIdType, // ID of the liquidity pool
    val share_amount: asset, // The amount of the share asset to use
    val extensions: extensions_type, // Unused. Reserved for future use.
) : Operation()

/* 63 */ @Serializable data class LiquidityPoolExchangeOperation(
    val fee: asset, // Operation fee
    val account: account_id_type, // The account who exchanges with the liquidity pool
    val pool: LiquidityPoolIdType, // ID of the liquidity pool
    val amount_to_sell: asset, // The amount of one asset type to sell
    val min_to_receive: asset, // The minimum amount of the other asset type to receive
    val extensions: extensions_type, // Unused. Reserved for future use.
) : Operation()

/* 64 */ @Serializable data class SametFundCreateOperation(
    val fee: asset, // Operation fee
    val owner_account: account_id_type, // Owner of the fund
    val asset_type: asset_id_type, // Asset type in the fund
    val balance: share_type,// Usable amount in the fund
    val fee_rate: uint32_t = 0 // Fee rate, the demominator is GRAPHENE_FEE_RATE_DENOM
    val extensions: extensions_type, // Unused. Reserved for future use.
) : Operation()

/* 65 */ @Serializable data class SametFundDeleteOperation(
    val fee: asset, // Operation fee
    val owner_account: account_id_type, // The account who owns the SameT Fund object
    val fund_id: SametFundIdType, // ID of the SameT Fund object
    val extensions: extensions_type, // Unused. Reserved for future use.
) : Operation()

/* 66 */ @Serializable data class SametFundUpdateOperation(
    val fee: asset, // Operation fee
    val owner_account: account_id_type, // Owner of the fund
    val fund_id: SametFundIdType, // ID of the SameT Fund object
    val delta_amount: Optional<asset>, // Delta amount, optional
    val new_fee_rate: Optional<uint32_t>, // New fee rate, optional
    val extensions: extensions_type, // Unused. Reserved for future use.
) : Operation()

/* 67 */ @Serializable data class SametFundBorrowOperation(
    val fee: asset, // Operation fee
    val borrower: account_id_type, // The account who borrows from the fund
    val fund_id: SametFundIdType, // ID of the SameT Fund
    val borrow_amount: asset, // The amount to borrow
    val extensions: extensions_type, // Unused. Reserved for future use.

) : Operation()

/* 68 */ @Serializable data class SametFundRepayOperation(
    val fee: asset, // Operation fee
    val account: account_id_type, // The account who repays to the SameT Fund
    val fund_id: SametFundIdType, // ID of the SameT Fund
    val repay_amount: asset, // The amount to repay
    val fund_fee: asset, // Fee for using the fund
    val extensions: extensions_type, // Unused. Reserved for future use.

) : Operation()


/* 69 */ @Serializable data class CreditOfferCreateOperation(
    val fee: asset, // Operation fee
    val owner_account: account_id_type, // Owner of the credit offer
    val asset_type: asset_id_type, // Asset type in the credit offer
    val balance: share_type, // Usable amount in the credit offer
    val fee_rate: uint32_t = 0 // Fee rate, the demominator is GRAPHENE_FEE_RATE_DENOM
    val max_duration_seconds: uint32_t = 0 // The time limit that borrowed funds should be repaid
    val min_deal_amount: share_type, // Minimum amount to borrow for each new deal
    val enabled: bool = false // Whether this offer is available
    val auto_disable_time: time_point_sec, // The time when this offer will be disabled automatically
// Types and rates of acceptable collateral
    val acceptable_collateral: flat_map<asset_id_type, PriceType>,
// Allowed borrowers and their maximum amounts to borrow. No limitation if empty.
    val acceptable_borrowers: flat_map<account_id_type, share_type>,
    val extensions: extensions_type, // Unused. Reserved for future use.
) : Operation()

/* 70 */ @Serializable data class CreditOfferDeleteOperation(
    val fee: asset, // Operation fee
    val owner_account: account_id_type, // The account who owns the credit offer
    val offer_id: CreditOfferIdType, // ID of the credit offer
    val extensions: extensions_type,// Unused. Reserved for future use.
) : Operation()

/* 71 */ @Serializable data class CreditOfferUpdateOperation(
    val fee: asset, // Operation fee
    val owner_account: account_id_type, // Owner of the credit offer
    val offer_id: CreditOfferIdType, // ID of the credit offer
    val delta_amount: Optional<asset>, // Delta amount, optional
    val fee_rate: Optional<uint32_t>, // New fee rate, optional
    val max_duration_seconds: Optional<uint32_t>, // New repayment time limit, optional
    val min_deal_amount : Optional<share_type>, // Minimum amount to borrow for each new deal, optional
    val enabled: Optional<bool>, // Whether this offer is available, optional
    val auto_disable_time: Optional<time_point_sec>, // New time to disable automatically, optional
// New types and rates of acceptable collateral, optional
    val acceptable_collateral: Optional<flat_map<asset_id_type, PriceType>>,
// New allowed borrowers and their maximum amounts to borrow, optional
    val acceptable_borrowers: Optional<flat_map<account_id_type, share_type>>,
    val extensions: extensions_type, // Unused. Reserved for future use.
) : Operation()

/* 72 */ @Serializable data class CreditOfferAcceptOperation(
    val fee: asset, // Operation fee
    val borrower: account_id_type, // The account who accepts the offer
    val offer_id: CreditOfferIdType, // ID of the credit offer
    val borrow_amount: asset, // The amount to borrow
    val collateral: asset, // The collateral
    val max_fee_rate: uint32_t = 0 // The maximum acceptable fee rate
    val min_duration_seconds: uint32_t = 0 // The minimum acceptable duration
    val extensions: extensions_type, // Unused. Reserved for future use.
) : Operation()

/* 73 */ @Serializable data class CreditDealRepayOperation(
    val fee: asset, // Operation fee
    val account: account_id_type, // The account who repays to the credit offer
    val deal_id: credit_deal_id_type, // ID of the credit deal
    val repay_amount: asset, // The amount to repay
    val credit_fee: asset, // The credit fee relative to the amount to repay
    val extensions: extensions_type, // Unused. Reserved for future use.
) : Operation()

/* 74 */ @Serializable data class CreditDealExpiredOperation(
    val fee: asset, // Only for compatibility, unused
    val deal_id: credit_deal_id_type, // ID of the credit deal
    val offer_id: CreditOfferIdType, // ID of the credit offer
    val offer_owner : account_id_type, // Owner of the credit offer
    val borrower: account_id_type, // The account who repays to the credit offer
    val unpaid_amount: asset, // The amount that is unpaid
    val collateral: asset, // The collateral liquidated
    val fee_rate: uint32_t = 0 // Fee rate, the demominator is GRAPHENE_FEE_RATE_DENOM
) : Operation()    // Virtual