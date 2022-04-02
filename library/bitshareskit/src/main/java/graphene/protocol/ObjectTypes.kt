@file:Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private fun throwNIE(): Nothing = throw NotImplementedError()

@Serializable(with = ObjectIdTypeSerializer::class)
interface AbstractType: Comparable<AbstractType> {
    val id: AbstractIdType

    override fun compareTo(other: AbstractType): Int {
        return id.instance.compareTo(other.id.instance)
    }
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface NullType: AbstractType { // K100
    override val id: NullIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface BaseType: AbstractType { // K101
    override val id: BaseIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface AccountType: AbstractType { // K102
    override val id: AccountIdType
    val membershipExpirationDate: ChainTimePoint            get() = throwNIE()
    val registrar: AccountType                              get() = throwNIE()
    val referrer: AccountType                               get() = throwNIE()
    val lifetimeReferrer: AccountType                       get() = throwNIE()
    val networkFeePercentage: uint16_t                      get() = throwNIE()
    val lifetimeReferrerFeePercentage: uint16_t             get() = throwNIE()
    val referrerRewardsFeePercentage: uint16_t              get() = throwNIE()
    val name: String                                        get() = throwNIE()
    val owner: Authority                                    get() = throwNIE()
    val active: Authority                                   get() = throwNIE()
    val options: AccountOptions                             get() = throwNIE()

    val numCommitteeVoted: uint16_t                         get() = throwNIE()
    val statistics: AccountStatisticsType                   get() = throwNIE()
    val whiteListingAccounts: Set<AccountType>              get() = throwNIE()
    val blackListingAccounts: Set<AccountType>              get() = throwNIE()
    val whiteListedAccounts: Set<AccountType>               get() = throwNIE()
    val blackListedAccounts: Set<AccountType>               get() = throwNIE()

    val cashbackVestingBalance: Optional<VestingBalanceIdType>
                                                            get() = throwNIE()

    val ownerSpecialAuthority: TypedSpecialAuthority        get() = throwNIE()
    val activeSpecialAuthority: TypedSpecialAuthority       get() = throwNIE()
    val topNControlFlags: uint8_t                           get() = throwNIE()
    val allowedAssets: Optional<FlatSet<AccountType>>       get() = throwNIE()
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface AssetType: AbstractType { // K103
    override val id: AssetIdType
    val symbol: String                                      get() = throwNIE()
    val issuer: AccountType                                 get() = throwNIE()
    val precision: UByte                                    get() = throwNIE()
    val options: AssetOptions                               get() = throwNIE()

    val dynamicData: AssetDynamicDataType                   get() = throwNIE()
    val bitassetData: Optional<AssetBitassetDataType>       get() = throwNIE()
    val buybackAccount: Optional<AccountType>               get() = throwNIE()
    val liquidityPool: Optional<LiquidityPoolType>          get() = throwNIE()
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface ForceSettlementType: AbstractType { // K104
    override val id: ForceSettlementIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface CommitteeMemberType: AbstractType { // K105
    override val id: CommitteeMemberIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface WitnessType: AbstractType { // K106
    override val id: WitnessIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface LimitOrderType: AbstractType { // K107
    override val id: LimitOrderIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface CallOrderType: AbstractType { // K108
    override val id: CallOrderIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface CustomType: AbstractType { // K109
    override val id: CustomIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface ProposalType: AbstractType { // K110
    override val id: ProposalIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface OperationHistoryType: AbstractType { // K111
    override val id: OperationHistoryIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface WithdrawPermissionType: AbstractType { // K112
    override val id: WithdrawPermissionIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface VestingBalanceType: AbstractType { // K113
    override val id: VestingBalanceIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface WorkerType: AbstractType { // K114
    override val id: WorkerIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface BalanceType: AbstractType { // K115
    override val id: BalanceIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface HtlcType: AbstractType { // K116
    override val id: HtlcIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface CustomAuthorityType: AbstractType { // K117
    override val id: CustomAuthorityIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface TicketType: AbstractType { // K118
    override val id: TicketIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface LiquidityPoolType: AbstractType { // K119
    override val id: LiquidityPoolIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface SametFundType: AbstractType { // K120
    override val id: SametFundIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface CreditOfferType: AbstractType { // K121
    override val id: CreditOfferIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface CreditDealType: AbstractType { // K122
    override val id: CreditDealIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface GlobalPropertyType: AbstractType { // K200
    override val id: GlobalPropertyIdType
    val parameters: ChainParameters                         get() = throwNIE()
    val pendingParameters: Optional<ChainParameters>        get() = throwNIE()
    val nextAvailableVoteId: uint32_t                       get() = throwNIE()
    val activeCommitteeMembers: List<CommitteeMemberType>   get() = throwNIE()
    val activeWitnesses: FlatSet<WitnessType>               get() = throwNIE()
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface DynamicGlobalPropertyType: AbstractType { // K201
    override val id: DynamicGlobalPropertyIdType
    val headBlockNumber: uint32_t                           get() = throwNIE()
    val headBlockId: BlockIdType                            get() = throwNIE()
    val time: ChainTimePoint                                get() = throwNIE()
    val currentWitness: WitnessType                         get() = throwNIE()
    val nextMaintenanceTime: ChainTimePoint                 get() = throwNIE()
    val lastVoteTallyTime: ChainTimePoint                   get() = throwNIE()
    val lastBudgetTime: ChainTimePoint                      get() = throwNIE()
    val witnessBudget: share_type                           get() = throwNIE()
    val totalPob: share_type                                get() = throwNIE()
    val totalInactive: share_type                           get() = throwNIE()
    val accountsRegisteredThisInterval: uint32_t            get() = throwNIE()
    val recentlyMissedCount: uint32_t                       get() = throwNIE()
    val currentAslot: uint64_t                              get() = throwNIE()
    val recentSlotsFilled: UInt128                          get() = throwNIE()
    val dynamicFlags: uint32_t                              get() = throwNIE()
    val lastIrreversibleBlockNum: uint32_t                  get() = throwNIE()
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface ReservedType: AbstractType { // K202
    override val id: ReservedIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface AssetDynamicDataType: AbstractType { // K203
    override val id: AssetDynamicDataIdType                 get() = throwNIE()
    val currentSupply: uint64_t                             get() = throwNIE()
    val confidentialSupply: uint64_t                        get() = throwNIE()
    val accumulatedFees: uint64_t                           get() = throwNIE()
    val accumulatedCollateralFees: uint64_t                 get() = throwNIE()
    val feePool: uint64_t                                   get() = throwNIE()
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface AssetBitassetDataType: AbstractType { // K204
    override val id: AssetBitassetDataIdType
    val asset: AssetType                                    get() = throwNIE()
    val options: BitassetOptions                            get() = throwNIE()
    val feeds: PriceFeeds                                   get() = throwNIE()
    val medianFeed: PriceFeedWithIcr                        get() = throwNIE()
    val currentFeed: PriceFeedWithIcr                       get() = throwNIE()
    val currentFeedPublicationTime: ChainTimePoint          get() = throwNIE()
    val currentMaintenanceCollateralization: PriceType      get() = throwNIE()
    val currentInitialCollateralization: PriceType          get() = throwNIE()
    val isPredictionMarket: Boolean                         get() = throwNIE()
    val forceSettledVolume: share_type                      get() = throwNIE()
    val settlementPrice: PriceType                          get() = throwNIE()
    val settlementFund: share_type                          get() = throwNIE()
    val individualSettlementDebt: share_type                get() = throwNIE()
    val individualSettlementFund: share_type                get() = throwNIE()
    val assetCerUpdated: Boolean                            get() = throwNIE()
    val feedCerUpdated: Boolean                             get() = throwNIE()
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface AccountBalanceType: AbstractType { // K205
    override val id: AccountBalanceIdType
    val owner: AccountType                                  get() = throwNIE()
    val asset: AssetType                                    get() = throwNIE()
    val balance: share_type                                 get() = throwNIE()
    val maintenanceFlag: Boolean                            get() = throwNIE()
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface AccountStatisticsType: AbstractType { // K206
    override val id: AccountStatisticsIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface TransactionHistoryType: AbstractType { // K207
    override val id: TransactionHistoryIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface BlockSummaryType: AbstractType { // K208
    override val id: BlockSummaryIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface AccountTransactionHistoryType: AbstractType { // K209
    override val id: AccountTransactionHistoryIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface BlindedBalanceType: AbstractType { // K210
    override val id: BlindedBalanceIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface ChainPropertyType: AbstractType { // K211
    override val id: ChainPropertyIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface WitnessScheduleType: AbstractType { // K212
    override val id: WitnessScheduleIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface BudgetRecordType: AbstractType { // K213
    override val id: BudgetRecordIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface SpecialAuthorityType: AbstractType { // K214
    override val id: SpecialAuthorityIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface BuybackType: AbstractType { // K215
    override val id: BuybackIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface FbaAccumulatorType: AbstractType { // K216
    override val id: FbaAccumulatorIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface CollateralBidType: AbstractType { // K217
    override val id: CollateralBidIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface CreditDealSummaryType: AbstractType { // K218
    override val id: CreditDealSummaryIdType
}