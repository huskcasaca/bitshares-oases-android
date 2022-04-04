@file:Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
package graphene.protocol

import graphene.serializers.ObjectIdSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

private fun throwNIE(): Nothing = throw NotImplementedError()

@Serializable(with = ObjectIdSerializer::class)
interface ObjectIdType: Comparable<ObjectIdType> {
    val id: ObjectId
    val space: ObjectSpace
    val type: ObjectType
    val instance: ObjectInstance
    val number: UInt64

    override fun compareTo(other: ObjectIdType): Int {
        return number.compareTo(other.number)
    }
}

@Serializable(with = ObjectIdSerializer::class)
interface NullIdType: ObjectIdType { // K100
    override val id: NullId
}

@Serializable(with = ObjectIdSerializer::class)
interface BaseIdType: ObjectIdType { // K101
    override val id: BaseId
}

@Serializable(with = ObjectIdSerializer::class)
interface AccountIdType: ObjectIdType { // K102
    override val id: AccountId
    val membershipExpirationDate: Instant                   get() = throwNIE()
    val registrar: AccountIdType                            get() = throwNIE()
    val referrer: AccountIdType                             get() = throwNIE()
    val lifetimeReferrer: AccountIdType                     get() = throwNIE()
    val networkFeePercentage: UInt16                        get() = throwNIE()
    val lifetimeReferrerFeePercentage: UInt16               get() = throwNIE()
    val referrerRewardsFeePercentage: UInt16                get() = throwNIE()
    val name: String                                        get() = throwNIE()
    val owner: Authority                                    get() = throwNIE()
    val active: Authority                                   get() = throwNIE()
    val options: AccountOptions                             get() = throwNIE()

    val numCommitteeVoted: UInt16                           get() = throwNIE()
    val statistics: AccountStatisticsIdType                 get() = throwNIE()
    val whiteListingAccounts: Set<AccountIdType>            get() = throwNIE()
    val blackListingAccounts: Set<AccountIdType>            get() = throwNIE()
    val whiteListedAccounts: Set<AccountIdType>             get() = throwNIE()
    val blackListedAccounts: Set<AccountIdType>             get() = throwNIE()
    val cashbackVestingBalance: Optional<VestingBalanceId>  get() = throwNIE()

    val ownerSpecialAuthority: SpecialAuthority             get() = throwNIE()
    val activeSpecialAuthority: SpecialAuthority            get() = throwNIE()
    val topNControlFlags: UInt8                             get() = throwNIE()
    val allowedAssets: Optional<FlatSet<AccountIdType>>     get() = throwNIE()
}

@Serializable(with = ObjectIdSerializer::class)
interface AssetIdType: ObjectIdType { // K103
    override val id: AssetId
    val symbol: String                                      get() = throwNIE()
    val issuer: AccountIdType                               get() = throwNIE()
    val precision: UByte                                    get() = throwNIE()
    val options: AssetOptions                               get() = throwNIE()

    val dynamicData: AssetDynamicDataIdType                 get() = throwNIE()
    val bitassetData: Optional<AssetBitassetDataIdType>     get() = throwNIE()
    val buybackAccount: Optional<AccountIdType>             get() = throwNIE()
    val liquidityPool: Optional<LiquidityPoolIdType>        get() = throwNIE()
}

@Serializable(with = ObjectIdSerializer::class)
interface ForceSettlementIdType: ObjectIdType { // K104
    override val id: ForceSettlementId
}

@Serializable(with = ObjectIdSerializer::class)
interface CommitteeMemberIdType: ObjectIdType { // K105
    override val id: CommitteeMemberId
}

@Serializable(with = ObjectIdSerializer::class)
interface WitnessIdType: ObjectIdType { // K106
    override val id: WitnessId
}

@Serializable(with = ObjectIdSerializer::class)
interface LimitOrderIdType: ObjectIdType { // K107
    override val id: LimitOrderId
}

@Serializable(with = ObjectIdSerializer::class)
interface CallOrderIdType: ObjectIdType { // K108
    override val id: CallOrderId
}

@Serializable(with = ObjectIdSerializer::class)
interface CustomIdType: ObjectIdType { // K109
    override val id: CustomId
}

@Serializable(with = ObjectIdSerializer::class)
interface ProposalIdType: ObjectIdType { // K110
    override val id: ProposalId
}

@Serializable(with = ObjectIdSerializer::class)
interface OperationHistoryIdType: ObjectIdType { // K111
    override val id: OperationHistoryId
}

@Serializable(with = ObjectIdSerializer::class)
interface WithdrawPermissionIdType: ObjectIdType { // K112
    override val id: WithdrawPermissionId
}

@Serializable(with = ObjectIdSerializer::class)
interface VestingBalanceIdType: ObjectIdType { // K113
    override val id: VestingBalanceId
}

@Serializable(with = ObjectIdSerializer::class)
interface WorkerIdType: ObjectIdType { // K114
    override val id: WorkerId
}

@Serializable(with = ObjectIdSerializer::class)
interface BalanceIdType: ObjectIdType { // K115
    override val id: BalanceId
}


@Serializable(with = ObjectIdSerializer::class)
interface HtlcIdType: ObjectIdType { // K116
    override val id: HtlcId
}


@Serializable(with = ObjectIdSerializer::class)
interface CustomAuthorityIdType: ObjectIdType { // K117
    override val id: CustomAuthorityId
}


@Serializable(with = ObjectIdSerializer::class)
interface TicketIdType: ObjectIdType { // K118
    override val id: TicketId
}


@Serializable(with = ObjectIdSerializer::class)
interface LiquidityPoolIdType: ObjectIdType { // K119
    override val id: LiquidityPoolId
}


@Serializable(with = ObjectIdSerializer::class)
interface SametFundIdType: ObjectIdType { // K120
    override val id: SametFundId
}


@Serializable(with = ObjectIdSerializer::class)
interface CreditOfferIdType: ObjectIdType { // K121
    override val id: CreditOfferId
}

@Serializable(with = ObjectIdSerializer::class)
interface CreditDealIdType: ObjectIdType { // K122
    override val id: CreditDealId
}


@Serializable(with = ObjectIdSerializer::class)
interface GlobalPropertyIdType: ObjectIdType { // K200
    override val id: GlobalPropertyId
    val parameters: ChainParameters                         get() = throwNIE()
    val pendingParameters: Optional<ChainParameters>        get() = throwNIE()
    val nextAvailableVoteId: UInt32                         get() = throwNIE()
    val activeCommitteeMembers: List<CommitteeMemberIdType> get() = throwNIE()
    val activeWitnesses: FlatSet<WitnessIdType>             get() = throwNIE()
}

@Serializable(with = ObjectIdSerializer::class)
interface DynamicGlobalPropertyIdType: ObjectIdType { // K201
    override val id: DynamicGlobalPropertyId
    val headBlockNumber: UInt32                             get() = throwNIE()
    val headBlockId: BlockIdType                            get() = throwNIE()
    val time: Instant                                       get() = throwNIE()
    val currentWitness: WitnessIdType                       get() = throwNIE()
    val nextMaintenanceTime: Instant                        get() = throwNIE()
    val lastVoteTallyTime: Instant                          get() = throwNIE()
    val lastBudgetTime: Instant                             get() = throwNIE()
    val witnessBudget: ShareType                            get() = throwNIE()
    val totalPob: ShareType                                 get() = throwNIE()
    val totalInactive: ShareType                            get() = throwNIE()
    val accountsRegisteredThisInterval: UInt32              get() = throwNIE()
    val recentlyMissedCount: UInt32                         get() = throwNIE()
    val currentAslot: UInt64                                get() = throwNIE()
    val recentSlotsFilled: UInt128                          get() = throwNIE()
    val dynamicFlags: UInt32                                get() = throwNIE()
    val lastIrreversibleBlockNum: UInt32                    get() = throwNIE()
}

@Serializable(with = ObjectIdSerializer::class)
interface ReservedIdType: ObjectIdType { // K202
    override val id: ReservedId
}

@Serializable(with = ObjectIdSerializer::class)
interface AssetDynamicDataIdType: ObjectIdType { // K203
    override val id: AssetDynamicDataId
    val currentSupply: ShareType                            get() = throwNIE()
    val confidentialSupply: ShareType                       get() = throwNIE()
    val accumulatedFees: ShareType                          get() = throwNIE()
    val accumulatedCollateralFees: ShareType                get() = throwNIE()
    val feePool: ShareType                                  get() = throwNIE()
}

@Serializable(with = ObjectIdSerializer::class)
interface AssetBitassetDataIdType: ObjectIdType { // K204
    override val id: AssetBitassetDataId
    val asset: AssetIdType                                  get() = throwNIE()
    val options: BitassetOptions                            get() = throwNIE()
    val feeds: PriceFeeds                                   get() = throwNIE()
    val medianFeed: PriceFeedWithIcr                        get() = throwNIE()
    val currentFeed: PriceFeedWithIcr                       get() = throwNIE()
    val currentFeedPublicationTime: Instant                 get() = throwNIE()
    val currentMaintenanceCollateralization: PriceType      get() = throwNIE()
    val currentInitialCollateralization: PriceType          get() = throwNIE()
    val isPredictionMarket: Boolean                         get() = throwNIE()
    val forceSettledVolume: ShareType                       get() = throwNIE()
    val settlementPrice: PriceType                          get() = throwNIE()
    val settlementFund: ShareType                           get() = throwNIE()
    val individualSettlementDebt: ShareType                 get() = throwNIE()
    val individualSettlementFund: ShareType                 get() = throwNIE()
    val assetCerUpdated: Boolean                            get() = throwNIE()
    val feedCerUpdated: Boolean                             get() = throwNIE()
}

@Serializable(with = ObjectIdSerializer::class)
interface AccountBalanceIdType: ObjectIdType { // K205
    override val id: AccountBalanceId
    val owner: AccountIdType                                get() = throwNIE()
    val asset: AssetIdType                                  get() = throwNIE()
    val balance: ShareType                                  get() = throwNIE()
    val maintenanceFlag: Boolean                            get() = throwNIE()
}

@Serializable(with = ObjectIdSerializer::class)
interface AccountStatisticsIdType: ObjectIdType { // K206
    override val id: AccountStatisticsId
}

@Serializable(with = ObjectIdSerializer::class)
interface TransactionHistoryIdType: ObjectIdType { // K207
    override val id: TransactionHistoryId
}

@Serializable(with = ObjectIdSerializer::class)
interface BlockSummaryIdType: ObjectIdType { // K208
    override val id: BlockSummaryId
}

@Serializable(with = ObjectIdSerializer::class)
interface AccountTransactionHistoryIdType: ObjectIdType { // K209
    override val id: AccountTransactionHistoryId
}

@Serializable(with = ObjectIdSerializer::class)
interface BlindedBalanceIdType: ObjectIdType { // K210
    override val id: BlindedBalanceId
}

@Serializable(with = ObjectIdSerializer::class)
interface ChainPropertyIdType: ObjectIdType { // K211
    override val id: ChainPropertyId
}

@Serializable(with = ObjectIdSerializer::class)
interface WitnessScheduleIdType: ObjectIdType { // K212
    override val id: WitnessScheduleId
}

@Serializable(with = ObjectIdSerializer::class)
interface BudgetRecordIdType: ObjectIdType { // K213
    override val id: BudgetRecordId
}

@Serializable(with = ObjectIdSerializer::class)
interface SpecialAuthorityIdType: ObjectIdType { // K214
    override val id: SpecialAuthorityId
}

@Serializable(with = ObjectIdSerializer::class)
interface BuybackIdType: ObjectIdType { // K215
    override val id: BuybackId
}

@Serializable(with = ObjectIdSerializer::class)
interface FbaAccumulatorIdType: ObjectIdType { // K216
    override val id: FbaAccumulatorId
}

@Serializable(with = ObjectIdSerializer::class)
interface CollateralBidIdType: ObjectIdType { // K217
    override val id: CollateralBidId
}

@Serializable(with = ObjectIdSerializer::class)
interface CreditDealSummaryIdType: ObjectIdType { // K218
    override val id: CreditDealSummaryId
}