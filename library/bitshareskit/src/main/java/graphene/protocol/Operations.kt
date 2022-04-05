package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/*  0 */ @Serializable data class TransferOperation(
    val fee: Asset,
    val from: AccountIdType, // Account to transfer asset from
    val to: AccountIdType, // Account to transfer asset to
    val amount: Asset, // The amount of asset to transfer from @ref from to @ref to
    val memo: Optional<MemoData> = optional(), // User provided data encrypted to the memo key of the "to" account
    val extensions: ExtensionsType,
) : Operation()
/*  1 */ @Serializable data class LimitOrderCreateOperation(
    val fee: Asset,
    val seller: AccountIdType,
    val amount_to_sell: Asset,
    val min_to_receive: Asset,
    val expiration: time_point_sec, // = MAXIMUM // The order will be removed from the books if not filled by expiration // Upon expiration, all unsold asset will be returned to seller
    val fill_or_kill: Boolean = false,  // If this flag is set the entire order must be filled or the operation is rejected
    val extensions: ExtensionsType,
) : Operation()
/*  2 */ @Serializable data class LimitOrderCancelOperation(
    val fee: Asset,
    val order: LimitOrderIdType,
    val fee_paying_account: AccountIdType, // must be order->seller
    val extensions: ExtensionsType,
) : Operation()

/*  3 */ @Serializable data class CallOrderUpdateOperation(
    val fee: Asset,
    val funding_account: AccountIdType,// pays fee, collateral, and cover
    val delta_collateral: Asset,// the amount of collateral to add to the margin position
    val delta_debt: Asset,// the amount of the debt to be paid off, may be negative to issue new debt
    val extensions: OptionsType,
) : Operation() {
    //    typealias extensions_type = extension<options_type>, // note: this will be jsonified to {...} but no longer [...]
    @Serializable
    data class OptionsType(
        val target_collateral_ratio: Optional<UInt16> = optional() // maximum CR to maintain when selling collateral on margin call
    ): Extension<OptionsType>
}

/*  4 */ @Serializable data class FillOrderOperation(
    val order_id: ObjectIdType,
    val account_id: AccountIdType,
    val pays: Asset,
    val receives: Asset,
    val fee // paid by receiving account
    : Asset,
    val fill_price: PriceType,
    val is_maker: Boolean,
) : Operation()           // Virtual

/*  5 */ @Serializable data class AccountCreateOperation(
    val fee: Asset,
    val registrar: AccountIdType, // This account pays the fee. Must be a lifetime member.
    val referrer: AccountIdType, // This account receives a portion of the fee split between registrar and referrer. Must be a member.
    val referrer_percent: UInt16, // = 0/ / Of the fee split between registrar and referrer, this percentage goes to the referrer. The rest goes to the registrar.
    val name: String,
    val owner: Authority,
    val active: Authority,
    val options: AccountOptions,
    val extensions: Ext,
) : Operation() {
    @Serializable
    data class Ext(
        val null_ext: Optional<Unit> = optional(),
        val owner_special_authority: Optional<SpecialAuthority> = optional(),
        val active_special_authority: Optional<SpecialAuthority> = optional(),
        val buyback_options: Optional<BuybackAccountOptions> = optional(),
    ): Extension<Ext>
}

/*  6 */ @Serializable data class AccountUpdateOperation(
    val fee: Asset,
    val account: AccountIdType, // The account to update
    val owner: Optional<Authority> = optional(), // New owner authority. If set, this operation requires owner authority to execute.
    val active: Optional<Authority> = optional(), // New active authority. This can be updated by the current active authority.
    val new_options: Optional<AccountOptions> = optional(), // New account options
    val extensions: Ext,
) : Operation() {
    @Serializable
    data class Ext(
        val null_ext: Optional<Unit> = optional(),
        val owner_special_authority: Optional<SpecialAuthority> = optional(),
        val active_special_authority: Optional<SpecialAuthority> = optional(),
    )
}

/*  7 */ @Serializable data class AccountWhitelistOperation(
    val fee: Asset, // Paid by authorizing_account
    val authorizing_account: AccountIdType, // The account which is specifying an opinion of another account
    val account_to_list: AccountIdType, // The account being opined about
    val new_listing: UInt8, // = AccountListing.NO_LISTING // The new white and blacklist status of account_to_list, as determined by authorizing_account // This is a bitfield using values defined in the account_listing enum
    val extensions: ExtensionsType,
) : Operation() {
    @Serializable(with = AccountListingSerializer::class)
    enum class AccountListing(val value: UInt8) {
        NO_LISTING(0x00U), // No opinion is specified about this account
        WHITE_LISTED(0x01U), // This account is whitelisted, but not blacklisted
        BLACK_LISTED(0x02U), // This account is blacklisted, but not whitelisted
        WHITE_AND_BLACK_LISTED(0x03U) // This account is both whitelisted and blacklisted
    }
    object AccountListingSerializer : KSerializer<AccountListing> {
        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AccountListing", PrimitiveKind.SHORT)
        override fun serialize(encoder: Encoder, value: AccountListing) =
            UInt8.serializer().serialize(encoder, value.value)
        override fun deserialize(decoder: Decoder): AccountListing =
            AccountListing.values()[UInt8.serializer().deserialize(decoder).toInt()]
    }
}

/*  8 */ @Serializable data class AccountUpgradeOperation(
    val fee: Asset, // The account to upgrade; must not already be a lifetime member
    val account_to_upgrade: AccountIdType, // If true, the account will be upgraded to a lifetime member; otherwise, it will add a year to the subscription
    val upgrade_to_lifetime_member: Boolean, // = false
    val extensions: ExtensionsType,
) : Operation()

/*  9 */ @Serializable data class AccountTransferOperation(
    val fee: Asset,
    val account_id: AccountIdType,
    val new_owner: AccountIdType,
    val extensions: ExtensionsType,
) : Operation()

/* 10 */ @Serializable data class AssetCreateOperation(
    val fee: Asset,
    val issuer: AccountIdType, // This account must sign and pay the fee for this operation. Later, this account may update the asset
    val symbol: String, // The ticker symbol of this asset
    val precision: UInt8, // = 0 // Number of digits to the right of decimal point, must be less than or equal to 12
    // Options common to all assets.
    // @note common_options.core_exchange_rate technically needs to store the asset ID of this new asset. Since this
    // ID is not known at the time this operation is created, create this price as though the new asset has instance
    // ID 1, and the chain will overwrite it with the new asset's ID.
    val common_options: AssetOptions,
    val bitasset_opts: Optional<BitassetOptions> = optional(), // Options only available for BitAssets. MUST be non-null if and only if the asset is market-issued.
    val is_prediction_market: Boolean, // = false, // For BitAssets, set this to true if the asset implements a prediction market; false otherwise
    val extensions: ExtensionsType,
) : Operation()

/* 11 */ @Serializable data class AssetUpdateOperation(
    val fee: Asset,
    val issuer: AccountIdType,
    val asset_to_update: AssetIdType,
    val new_issuer: Optional<AccountIdType> = optional(), // If the asset is to be given a new issuer, specify his ID here.
    val new_options: AssetOptions,
    val extensions: Ext,
) : Operation() {
    @Serializable
    data class Ext(
        // After BSIP48, the precision of an asset can be updated if no supply is available
        // @note The parties involved still need to be careful
        val new_precision: Optional<UInt8> = optional(),
        // After BSIP48, if this option is set to true, the asset's core_exchange_rate won't be updated.
        // This is especially useful for committee-owned bitassets which can not be updated quickly.
        val skip_core_exchange_rate: Optional<Boolean> = optional(),
    ) : Extension<Ext>
}

/* 12 */ @Serializable data class AssetUpdateBitassetOperation(
    val fee: Asset,
    val issuer: AccountIdType,
    val asset_to_update: AssetIdType,
    val new_options: BitassetOptions,
    val extensions: ExtensionsType,
) : Operation()

/* 13 */ @Serializable data class AssetUpdateFeedProducersOperation(
    val fee: Asset,
    val issuer: AccountIdType,
    val asset_to_update: AssetIdType,
    val new_feed_producers: FlatSet<AccountIdType>,
    val extensions: ExtensionsType,
) : Operation()

/* 14 */ @Serializable data class AssetIssueOperation(
    val fee: Asset,
    val issuer: AccountIdType, // Must be asset_to_issue->asset_id->issuer
    val asset_to_issue: Asset,
    val issue_to_account: AccountIdType,
    val memo: Optional<MemoData> = optional(), // user provided data encrypted to the memo key of the "to" account
    val extensions: ExtensionsType,
) : Operation()

/* 15 */ @Serializable data class AssetReserveOperation(
    val fee: Asset,
    val payer: AccountIdType,
    val amount_to_reserve: Asset,
    val extensions: ExtensionsType,
) : Operation()

/* 16 */ @Serializable data class AssetFundFeePoolOperation(
    val fee: Asset, // core asset
    val from_account: AccountIdType,
    val asset_id: AssetIdType,
    val amount: ShareType, // core asset
    val extensions: ExtensionsType,
) : Operation()

/* 17 */ @Serializable data class AssetSettleOperation(
    val fee: Asset,
    val account: AccountIdType, // Account requesting the force settlement. This account pays the fee
    val amount: Asset, // Amount of asset to force settle. This must be a market-issued asset
    val extensions: ExtensionsType,
) : Operation()

/* 18 */ @Serializable data class AssetGlobalSettleOperation(
    val fee: Asset,
    val issuer: AccountIdType, // must equal issuer of @ref asset_to_settle
    val asset_to_settle: AssetIdType,
    val settle_price: PriceType,
    val extensions: ExtensionsType,
) : Operation()

/* 19 */ @Serializable data class AssetPublishFeedOperation(
    val fee: Asset,// paid for by publisher
    val publisher: AccountIdType,
    val asset_id: AssetIdType, // asset for which the feed is published
    val feed: PriceFeed,
    val extensions: Ext,
) : Operation() {
    @Serializable
    data class Ext(
        // After BSIP77, price feed producers can feed ICR too
        val initial_collateral_ratio: Optional<UInt16> = optional()  // BSIP-77
    ): Extension<Ext>
}

/* 20 */ @Serializable data class WitnessCreateOperation(
    val fee: Asset,
    // The account which owns the witness. This account pays the fee for this operation.
    val witness_account: AccountIdType,
    val url: String,
    val block_signing_key: PublicKeyType,
) : Operation()

/* 21 */ @Serializable data class WitnessUpdateOperation(
    val fee: Asset,
    val witness: WitnessIdType, // The witness object to update.
    val witness_account: AccountIdType, // The account which owns the witness. This account pays the fee for this operation.
    val new_url: Optional<String> = optional(), // The new URL.
    val new_signing_key: Optional<PublicKeyType> = optional(), // The new block signing key.
) : Operation()

/* 22 */ @Serializable data class ProposalCreateOperation(
    val fee: Asset,
    val fee_paying_account: AccountIdType,
    val proposed_ops: List<OperationWrapper>,
    val expiration_time: time_point_sec,
    val review_period_seconds: Optional<UInt32> = optional(),
    val extensions: ExtensionsType,
) : Operation()

/* 23 */ @Serializable data class ProposalUpdateOperation(
    val fee_paying_account: AccountIdType,
    val fee: Asset,
    val proposal: ProposalIdType,
    val active_approvals_to_add: FlatSet<AccountIdType>,
    val active_approvals_to_remove: FlatSet<AccountIdType>,
    val owner_approvals_to_add: FlatSet<AccountIdType>,
    val owner_approvals_to_remove: FlatSet<AccountIdType>,
    val key_approvals_to_add: FlatSet<PublicKeyType>,
    val key_approvals_to_remove: FlatSet<PublicKeyType>,
    val extensions: ExtensionsType,
) : Operation()

/* 24 */ @Serializable data class ProposalDeleteOperation(
    val fee_paying_account: AccountIdType,
    val using_owner_authority: Boolean = false,
    val fee: Asset,
    val proposal: ProposalIdType,
    val extensions: ExtensionsType,
) : Operation()

/* 25 */ @Serializable data class WithdrawPermissionCreateOperation(
    val fee: Asset,
    val withdraw_from_account: AccountIdType, // The account authorizing withdrawals from its balances
    val authorized_account: AccountIdType, // The account authorized to make withdrawals from withdraw_from_account
    val withdrawal_limit: Asset, // The maximum amount authorized_account is allowed to withdraw in a given withdrawal period
    val withdrawal_period_sec: UInt32, // = 0 // Length of the withdrawal period in seconds
    val periods_until_expiration: UInt32, // = 0 // The number of withdrawal periods this permission is valid for
    val period_start_time: time_point_sec, // Time at which the first withdrawal period begins; must be in the future
) : Operation()
/* 26 */ @Serializable data class WithdrawPermissionUpdateOperation(
    val fee: Asset,
    val withdraw_from_account: AccountIdType, // This account pays the fee. Must match permission_to_update->withdraw_from_account
    val authorized_account: AccountIdType, // The account authorized to make withdrawals. Must match permission_to_update->authorized_account
    val permission_to_update: WithdrawPermissionIdType, // ID of the permission which is being updated
    val withdrawal_limit: Asset, // New maximum amount the withdrawer is allowed to charge per withdrawal period
    val withdrawal_period_sec: UInt32, // = 0 // New length of the period between withdrawals
    val period_start_time: time_point_sec, // New beginning of the next withdrawal period; must be in the future
    val periods_until_expiration: UInt32, // = 0 // The new number of withdrawal periods for which this permission will be valid
) : Operation()

/* 27 */ @Serializable data class WithdrawPermissionClaimOperation(
    val fee: Asset, // Paid by withdraw_to_account
    val withdraw_permission: WithdrawPermissionIdType, // ID of the permission authorizing this withdrawal
    val withdraw_from_account: AccountIdType, // Must match withdraw_permission->withdraw_from_account
    val withdraw_to_account: AccountIdType, // Must match withdraw_permision->authorized_account
    val amount_to_withdraw: Asset, // Amount to withdraw. Must not exceed withdraw_permission->withdrawal_limit
    val memo: Optional<MemoData> = optional(), // Memo for withdraw_from_account. Should generally be encrypted with withdraw_from_account->memo_key
) : Operation()

/* 28 */ @Serializable data class WithdrawPermissionDeleteOperation(
    val fee: Asset,
    // Must match withdrawal_permission->withdraw_from_account. This account pays the fee.
    val withdraw_from_account: AccountIdType,
    // The account previously authorized to make withdrawals. Must match withdrawal_permission->authorized_account
    val authorized_account: AccountIdType,
    // ID of the permission to be revoked.
    val withdrawal_permission: WithdrawPermissionIdType,
) : Operation()

/* 29 */ @Serializable data class CommitteeMemberCreateOperation(
    val fee: Asset,
    // The account which owns the committee_member. This account pays the fee for this operation.
    val committee_member_account: AccountIdType,
    val url: String,
) : Operation()

/* 30 */ @Serializable data class CommitteeMemberUpdateOperation(
    val fee: Asset,
    // The committee member to update.
    val committee_member: CommitteeMemberIdType,
    // The account which owns the committee_member. This account pays the fee for this operation.
    val committee_member_account: AccountIdType,
    val new_url: Optional<String> = optional(),
) : Operation()
/* 31 */ @Serializable data class CommitteeMemberUpdateGlobalParametersOperation(
    val fee: Asset,
    val new_parameters: ChainParameters,
) : Operation()

/* 32 */ @Serializable data class VestingBalanceCreateOperation(
    val fee: Asset,
    val creator: AccountIdType, // Who provides funds initially
    val owner: AccountIdType, // Who is able to withdraw the balance
    val amount: Asset,
    val policy: VestingPolicyInitializer,
) : Operation()


/* 33 */ @Serializable data class VestingBalanceWithdrawOperation(
    val fee: Asset,
    val vesting_balance: VestingBalanceIdType,
    val owner // Must be vesting_balance.owner
    : AccountIdType,
    val amount: Asset,
) : Operation()
/* 34 */ @Serializable data class WorkerCreateOperation(
    val fee: Asset,
    val owner: AccountIdType,
    val work_begin_date: time_point_sec,
    val work_end_date: time_point_sec,
    val daily_pay: ShareType,
    val name: String,
    val url: String,
    // This should be set to the initializer appropriate for the type of worker to be created.
    val initializer: WorkerInitializer,
) : Operation()

/* 35 */ @Serializable data class CustomOperation(
    val fee: Asset,
    val payer: AccountIdType,
    val required_auths: FlatSet<AccountIdType>,
    val id: UInt16, // = 0
    val data: List<Char>,
) : Operation()

/* 36 */ @Serializable data class AssertOperation(
    val fee: Asset,
    val fee_paying_account: AccountIdType,
    val predicates: List<Predicate>,
    val required_auths: FlatSet<AccountIdType>,
    val extensions: ExtensionsType,
) : Operation()

/* 37 */ @Serializable data class BalanceClaimOperation(
    val fee: Asset,
    val deposit_to_account: AccountIdType,
    val balance_to_claim: BalanceIdType,
    val balance_owner_key: PublicKeyType,
    val total_claimed: Asset,
) : Operation()

/* 38 */ @Serializable data class OverrideTransferOperation(
    val fee: Asset,
    val issuer: AccountIdType, // Account to transfer asset from
    val from: AccountIdType, // Account to transfer asset to
    val to: AccountIdType, // The amount of asset to transfer from @ref from to @ref to
    val amount: Asset, // User provided data encrypted to the memo key of the "to" account
    val memo: Optional<MemoData> = optional(),
    val extensions: ExtensionsType,
) : Operation()

/* 39 */ @Serializable data class TransferToBlindOperation(
    val fee: Asset,
    val amount: Asset,
    val from: AccountIdType,
    val blinding_factor: BlindFactorType,
    val outputs: List<BlindOutput>,
) : Operation()

/* 40 */ @Serializable data class BlindTransferOperation(
    val fee: Asset,
    val inputs: List<BlindInput>,
    val outputs: List<BlindOutput>,
) : Operation()

/* 41 */ @Serializable data class TransferFromBlindOperation(
    val fee: Asset,
    val amount: Asset,
    val to: AccountIdType,
    val blinding_factor: BlindFactorType,
    val inputs: List<BlindInput>,
) : Operation()

/* 42 */ @Serializable data class AssetSettleCancelOperation(
    val fee: Asset,
    val settlement: ForceSettlementIdType,
    val account: AccountIdType, // Account requesting the force settlement. This account pays the fee
    val amount: Asset, // Amount of asset to force settle. This must be a market-issued asset
) : Operation()  // Virtual

/* 43 */ @Serializable data class AssetClaimFeesOperation(
    val fee: Asset,
    val issuer: AccountIdType, // must match issuer of asset from which we claim fees
    val amount_to_claim: Asset,
    val extensions: AdditionalOptionsType,
) : Operation() {
    @Serializable
    data class AdditionalOptionsType(
        // Which asset to claim fees from. This is needed, e.g., to claim collateral-
        // denominated fees from a collateral-backed smart asset. If unset, assumed to be same
        // asset as amount_to_claim is denominated in, such as would be the case when claiming
        // market fees. If set, validation requires it to be a different asset_id than
        // amount_to_claim (else there would exist two ways to form the same request).
        @SerialName("claim_from_asset_id") val claimFromAssetId: Optional<AssetIdType> = optional()
    ) : Extension<AdditionalOptionsType>
}

/* 44 */ @Serializable data class FbaDistributeOperation(
    val fee: Asset, // always zero
    val account_id: AccountIdType,
    val fba_id: ObjectIdType, // We use object_id_type because this is an implementaton object, and therefore is not known to the protocol library
    val amount: ShareType,
) : Operation()       // Virtual

/* 45 */ @Serializable data class BidCollateralOperation(
    val fee: Asset,
    val bidder: AccountIdType, // pays fee and additional collateral
    val additional_collateral: Asset, // the amount of collateral to bid for the debt
    val debt_covered: Asset, // the amount of debt to take over
    val extensions: ExtensionsType,
) : Operation()

/* 46 */ @Serializable data class ExecuteBidOperation(
    val bidder: AccountIdType,
    val debt: Asset,
    val collateral: Asset,
    val fee: Asset,
) : Operation() // Virtual

/* 47 */ @Serializable data class AssetClaimPoolOperation(
    val fee: Asset,
    val issuer: AccountIdType,
    val asset_id: AssetIdType, // fee.asset_id must != asset_id
    val amount_to_claim: Asset, // core asset
    val extensions: ExtensionsType,
) : Operation()

/* 48 */ @Serializable data class AssetUpdateIssuerOperation(
    val fee: Asset,
    val issuer: AccountIdType,
    val asset_to_update: AssetIdType,
    val new_issuer: AccountIdType,
    val extensions: ExtensionsType,
) : Operation()

/* 49 */ @Serializable data class HtlcCreateOperation(
    val fee: Asset,
    val from: AccountIdType, // where the held monies are to come from
    val to: AccountIdType, // where the held monies will go if the preimage is provided
    val amount: Asset, // the amount to hold
    val preimage_hash: HtlcHash, // the (typed) hash of the preimage
    val preimage_size: UInt16, // the size of the preimage
    val claim_period_seconds: UInt32, // The time the funds will be returned to the source if not claimed
    val extensions: AdditionalOptionsType, // additional extensions
) : Operation() {
    @Serializable
    data class AdditionalOptionsType(
        @SerialName("memo") val memo: Optional<MemoData> = optional(),
    ) : Extension<AdditionalOptionsType>
}

/* 50 */ @Serializable data class HtlcRedeemOperation(
    val fee: Asset, // paid to network
    val htlc_id: HtlcIdType, // the object we are attempting to update
    val redeemer: AccountIdType, // who is attempting to update the transaction
    val preimage: List<Char>, // the preimage (not used if after epoch timeout)
    val extensions: ExtensionsType, // for future expansion
) : Operation()

/* 51 */ @Serializable data class HtlcRedeemedOperation(
    val htlc_id: HtlcIdType,
    val from: AccountIdType,
    val to: AccountIdType,
    val redeemer: AccountIdType,
    val amount: Asset,
    val htlc_preimage_hash: HtlcHash,
    val htlc_preimage_size: UInt16,
    val fee: Asset,
    val preimage: List<Char>,
) : Operation()         // Virtual

/* 52 */ @Serializable data class HtlcExtendOperation(
    val fee: Asset, // paid to network
    val htlc_id: HtlcIdType, // the object we are attempting to update
    val update_issuer: AccountIdType, // who is attempting to update the transaction
    val seconds_to_add: UInt32, // how much to add
    val extensions: ExtensionsType, // for future expansion
) : Operation()

// TODO: 2022/4/5
/* 53 */ @Serializable data class HtlcRefundOperation(
    val fee: Asset,
    val htlc_id: HtlcIdType, // of the associated htlc object; it is deleted during emittance of this operation
    val to: AccountIdType,
    val original_htlc_recipient: AccountIdType,
    val htlc_amount: Asset,
    val htlc_preimage_hash: HtlcHash,
    val htlc_preimage_size: UInt16,
) : Operation()           // Virtual

/* 54 */ @Serializable data class CustomAuthorityCreateOperation(
    val fee: Asset,
    val account: AccountIdType, // Account which is setting the custom authority; also pays the fee
    val enabled: Boolean, // Whether the custom authority is enabled or not
    val valid_from: time_point_sec, // Date when custom authority becomes active
    val valid_to: time_point_sec, // Expiration date for custom authority
    val operation_type: UnsignedInt, // Tag of the operation this custom authority can authorize
    val auth: Authority, // Authentication requirements for the custom authority
    val restrictions: List<Restriction>, // Restrictions on operations this custom authority can authenticate
    val extensions: ExtensionsType,
) : Operation()

/* 55 */ @Serializable data class CustomAuthorityUpdateOperation(
    val fee: Asset,
    val account: AccountIdType, // Account which owns the custom authority to update; also pays the fee
    val authority_to_update: CustomAuthorityIdType, // ID of the custom authority to update
    val new_enabled: Optional<Boolean> = optional(), // Change to whether the custom authority is enabled or not
    val new_valid_from: Optional<time_point_sec> = optional(), // Change to the custom authority begin date
    val new_valid_to: Optional<time_point_sec> = optional(), // Change to the custom authority expiration date
    val new_auth: Optional<Authority> = optional(), // Change to the authentication for the custom authority
    val restrictions_to_remove: FlatSet<UInt16>, // Set of IDs of restrictions to remove
    val restrictions_to_add: List<Restriction>, // Vector of new restrictions
    val extensions: ExtensionsType,
) : Operation()
/* 56 */ @Serializable data class CustomAuthorityDeleteOperation(
    val fee: Asset,
    val account: AccountIdType, // Account which owns the custom authority to update; also pays the fee
    val authority_to_delete: CustomAuthorityIdType, // ID of the custom authority to delete
    val extensions: ExtensionsType,
) : Operation()

/* 57 */ @Serializable data class TicketCreateOperation(
    val fee: Asset, // Operation fee
    val account: AccountIdType, // The account who creates the ticket
    val target_type: UnsignedInt, // The target ticket type, see @ref ticket_type
    val amount: Asset, // The amount of the ticket
    val extensions: ExtensionsType, // Unused. Reserved for future use.
) : Operation()

/* 58 */ @Serializable data class TicketUpdateOperation(
    val fee: Asset, // Operation fee
    val ticket: TicketIdType, // The ticket to update
    val account: AccountIdType, // The account who owns the ticket
    val target_type: UnsignedInt, // New target ticket type, see @ref ticket_type
    val amount_for_new_target: Optional<Asset> = optional(), // The amount to be used for the new target
    val extensions: ExtensionsType, // Unused. Reserved for future use.
) : Operation()

/* 59 */ @Serializable data class LiquidityPoolCreateOperation(
    val fee: Asset, // Operation fee
    val account: AccountIdType, // The account who creates the liquidity pool
    val asset_a: AssetIdType, // Type of the first asset in the pool
    val asset_b: AssetIdType, // Type of the second asset in the pool
    val share_asset: AssetIdType, // Type of the share asset aka the LP token
    val taker_fee_percent: UInt16, // = 0 // Taker fee percent
    val withdrawal_fee_percent: UInt16, // = 0 // Withdrawal fee percent
    val extensions: ExtensionsType, // Unused. Reserved for future use.
) : Operation()

/* 60 */ @Serializable data class LiquidityPoolDeleteOperation(
    val fee: Asset, // Operation fee
    val account: AccountIdType, // The account who owns the liquidity pool
    val pool: LiquidityPoolIdType, // ID of the liquidity pool
    val extensions: ExtensionsType, // Unused. Reserved for future use.
) : Operation()


/* 61 */ @Serializable data class LiquidityPoolDepositOperation(
    val fee: Asset, // Operation fee
    val account: AccountIdType, // The account who deposits to the liquidity pool
    val pool: LiquidityPoolIdType, // ID of the liquidity pool
    val amount_a: Asset, // The amount of the first asset to deposit
    val amount_b: Asset, // The amount of the second asset to deposit
    val extensions: ExtensionsType, // Unused. Reserved for future use.

) : Operation()
/* 62 */ @Serializable data class LiquidityPoolWithdrawOperation(
    val fee: Asset, // Operation fee
    val account: AccountIdType, // The account who withdraws from the liquidity pool
    val pool: LiquidityPoolIdType, // ID of the liquidity pool
    val share_amount: Asset, // The amount of the share asset to use
    val extensions: ExtensionsType, // Unused. Reserved for future use.
) : Operation()

/* 63 */ @Serializable data class LiquidityPoolExchangeOperation(
    val fee: Asset, // Operation fee
    val account: AccountIdType, // The account who exchanges with the liquidity pool
    val pool: LiquidityPoolIdType, // ID of the liquidity pool
    val amount_to_sell: Asset, // The amount of one asset type to sell
    val min_to_receive: Asset, // The minimum amount of the other asset type to receive
    val extensions: ExtensionsType, // Unused. Reserved for future use.
) : Operation()

/* 64 */ @Serializable data class SametFundCreateOperation(
    val fee: Asset, // Operation fee
    val owner_account: AccountIdType, // Owner of the fund
    val asset_type: AssetIdType, // Asset type in the fund
    val balance: ShareType,// Usable amount in the fund
    val fee_rate: UInt32, // = 0 // Fee rate, the demominator is GRAPHENE_FEE_RATE_DENOM
    val extensions: ExtensionsType, // Unused. Reserved for future use.
) : Operation()

/* 65 */ @Serializable data class SametFundDeleteOperation(
    val fee: Asset, // Operation fee
    val owner_account: AccountIdType, // The account who owns the SameT Fund object
    val fund_id: SametFundIdType, // ID of the SameT Fund object
    val extensions: ExtensionsType, // Unused. Reserved for future use.
) : Operation()

/* 66 */ @Serializable data class SametFundUpdateOperation(
    val fee: Asset, // Operation fee
    val owner_account: AccountIdType, // Owner of the fund
    val fund_id: SametFundIdType, // ID of the SameT Fund object
    val delta_amount: Optional<Asset> = optional(), // Delta amount, optional
    val new_fee_rate: Optional<UInt32> = optional(), // New fee rate, optional
    val extensions: ExtensionsType, // Unused. Reserved for future use.
) : Operation()

/* 67 */ @Serializable data class SametFundBorrowOperation(
    val fee: Asset, // Operation fee
    val borrower: AccountIdType, // The account who borrows from the fund
    val fund_id: SametFundIdType, // ID of the SameT Fund
    val borrow_amount: Asset, // The amount to borrow
    val extensions: ExtensionsType, // Unused. Reserved for future use.

) : Operation()

/* 68 */ @Serializable data class SametFundRepayOperation(
    val fee: Asset, // Operation fee
    val account: AccountIdType, // The account who repays to the SameT Fund
    val fund_id: SametFundIdType, // ID of the SameT Fund
    val repay_amount: Asset, // The amount to repay
    val fund_fee: Asset, // Fee for using the fund
    val extensions: ExtensionsType, // Unused. Reserved for future use.

) : Operation()


/* 69 */ @Serializable data class CreditOfferCreateOperation(
    val fee: Asset, // Operation fee
    val owner_account: AccountIdType, // Owner of the credit offer
    val asset_type: AssetIdType, // Asset type in the credit offer
    val balance: ShareType, // Usable amount in the credit offer
    val fee_rate: UInt32, // = 0 // Fee rate, the demominator is GRAPHENE_FEE_RATE_DENOM
    val max_duration_seconds: UInt32, // = 0 // The time limit that borrowed funds should be repaid
    val min_deal_amount: ShareType, // Minimum amount to borrow for each new deal
    val enabled: Boolean, // = false // Whether this offer is available
    val auto_disable_time: time_point_sec, // The time when this offer will be disabled automatically
    val acceptable_collateral: FlatMap<AssetIdType, PriceType>, // Types and rates of acceptable collateral
    val acceptable_borrowers: FlatMap<AccountIdType, ShareType>, // Allowed borrowers and their maximum amounts to borrow. No limitation if empty.
    val extensions: ExtensionsType, // Unused. Reserved for future use.
) : Operation()

/* 70 */ @Serializable data class CreditOfferDeleteOperation(
    val fee: Asset, // Operation fee
    val owner_account: AccountIdType, // The account who owns the credit offer
    val offer_id: CreditOfferIdType, // ID of the credit offer
    val extensions: ExtensionsType,// Unused. Reserved for future use.
) : Operation()

/* 71 */ @Serializable data class CreditOfferUpdateOperation(
    val fee: Asset, // Operation fee
    val owner_account: AccountIdType, // Owner of the credit offer
    val offer_id: CreditOfferIdType, // ID of the credit offer
    val delta_amount: Optional<Asset> = optional(), // Delta amount, optional
    val fee_rate: Optional<UInt32> = optional(), // New fee rate, optional
    val max_duration_seconds: Optional<UInt32> = optional(), // New repayment time limit, optional
    val min_deal_amount: Optional<ShareType> = optional(), // Minimum amount to borrow for each new deal, optional
    val enabled: Optional<Boolean> = optional(), // Whether this offer is available, optional
    val auto_disable_time: Optional<time_point_sec>, // New time to disable automatically, optional
    val acceptable_collateral: Optional<FlatMap<AssetIdType, PriceType>> = optional(), // New types and rates of acceptable collateral, optional
    val acceptable_borrowers: Optional<FlatMap<AccountIdType, ShareType>> = optional(), // New allowed borrowers and their maximum amounts to borrow, optional
    val extensions: ExtensionsType, // Unused. Reserved for future use.
) : Operation()

/* 72 */ @Serializable data class CreditOfferAcceptOperation(
    val fee: Asset, // Operation fee
    val borrower: AccountIdType, // The account who accepts the offer
    val offer_id: CreditOfferIdType, // ID of the credit offer
    val borrow_amount: Asset, // The amount to borrow
    val collateral: Asset, // The collateral
    val max_fee_rate: UInt32, // = 0 // The maximum acceptable fee rate
    val min_duration_seconds: UInt32, // = 0 // The minimum acceptable duration
    val extensions: ExtensionsType, // Unused. Reserved for future use.
) : Operation()

/* 73 */ @Serializable data class CreditDealRepayOperation(
    val fee: Asset, // Operation fee
    val account: AccountIdType, // The account who repays to the credit offer
    val deal_id: CreditDealIdType, // ID of the credit deal
    val repay_amount: Asset, // The amount to repay
    val credit_fee: Asset, // The credit fee relative to the amount to repay
    val extensions: ExtensionsType, // Unused. Reserved for future use.
) : Operation()

/* 74 */ @Serializable data class CreditDealExpiredOperation(
    val fee: Asset, // Only for compatibility, unused
    val deal_id: CreditDealIdType, // ID of the credit deal
    val offer_id: CreditOfferIdType, // ID of the credit offer
    val offer_owner: AccountIdType, // Owner of the credit offer
    val borrower: AccountIdType, // The account who repays to the credit offer
    val unpaid_amount: Asset, // The amount that is unpaid
    val collateral: Asset, // The collateral liquidated
    val fee_rate: UInt32, // = 0 // Fee rate, the demominator is GRAPHENE_FEE_RATE_DENOM
) : Operation()    // Virtual