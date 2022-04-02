package graphene.protocol

import kotlinx.serialization.Serializable


@Serializable(with = ObjectIdTypeSerializer::class)
interface AbstractType: Comparable<AbstractType> {
    val id: AbstractIdType                                  get() = this as AbstractIdType

    override fun compareTo(other: AbstractType): Int {
        return id.instance.compareTo(other.id.instance)
    }
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K100_NullType: AbstractType {
    override val id: K100_NullIdType                        get() = this as K100_NullIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K101_BaseType: AbstractType {
    override val id: K101_BaseIdType                        get() = this as K101_BaseIdType
}
@Serializable(with = ObjectIdTypeSerializer::class)
interface K102_AccountType: AbstractType {
    override val id: K102_AccountIdType                     get() = this as K102_AccountIdType
    val membershipExpirationDate: ChainTimePoint            get() = ChainTimePoint.DEFAULT_EXPIRATION_TIME
    val registrar: K102_AccountType                         get() = emptyIdType()
    val referrer: K102_AccountType                          get() = emptyIdType()
    val lifetimeReferrer: K102_AccountType                  get() = emptyIdType()
    val networkFeePercentage: UInt16                        get() = 0U
    val lifetimeReferrerFeePercentage: UInt16               get() = 0U
    val referrerRewardsFeePercentage: UInt16                get() = 0U
    val name: String                                        get() = emptyString()
    val owner: Authority                                    get() = emptyComponent()
    val active: Authority                                   get() = emptyComponent()
    val options: AccountOptions                             get() = emptyComponent()

    val numCommitteeVoted: UInt16                           get() = 0U
    val statistics: K206_AccountStatisticsType              get() = emptyIdType()
    val whiteListingAccounts: Set<K102_AccountType>         get() = emptySet()
    val blackListingAccounts: Set<K102_AccountType>         get() = emptySet()
    val whiteListedAccounts: Set<K102_AccountType>          get() = emptySet()
    val blackListedAccounts: Set<K102_AccountType>          get() = emptySet()

    val ownerSpecialAuthority: TypedSpecialAuthority             get() = emptyComponent()
    val activeSpecialAuthority: TypedSpecialAuthority            get() = emptyComponent()
    val topNControlFlags: UInt8                             get() = 0U
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K103_AssetType: AbstractType {
    override val id: K103_AssetIdType                       get() = this as K103_AssetIdType
    val symbol: String                                      get() = emptyString()
    val issuer: K102_AccountType                            get() = emptyIdType()
    val precision: UByte                                    get() = 0U
    val options: AssetOptions                               get() = emptyComponent()
    val dynamicData: K203_AssetDynamicDataType              get() = emptyIdType()

    val bitassetData: Optional<K204_AssetBitassetDataType>  get() = optional()
    val buybackAccount: Optional<K102_AccountType>          get() = optional()
    val forLiquidityPool: Optional<K119_LiquidityPoolType>  get() = optional()

//    var bitassetDataId: KAssetBitassetIdType = emptyIdType(),
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K104_ForceSettlementType: AbstractType {
    override val id: K104_ForceSettlementIdType             get() = this as K104_ForceSettlementIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K105_CommitteeMemberType: AbstractType {
    override val id: K105_CommitteeMemberIdType             get() = this as K105_CommitteeMemberIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K106_WitnessType: AbstractType {
    override val id: K106_WitnessIdType                     get() = this as K106_WitnessIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K107_LimitOrderType: AbstractType {
    override val id: K107_LimitOrderIdType                  get() = this as K107_LimitOrderIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K108_CallOrderType: AbstractType {
    override val id: K108_CallOrderIdType                   get() = this as K108_CallOrderIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K109_CustomType: AbstractType {
    override val id: K109_CustomIdType                      get() = this as K109_CustomIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K110_ProposalType: AbstractType {
    override val id: K110_ProposalIdType                    get() = this as K110_ProposalIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K111_OperationHistoryType: AbstractType {
    override val id: K111_OperationHistoryIdType            get() = this as K111_OperationHistoryIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K112_WithdrawPermissionType: AbstractType {
    override val id: K112_WithdrawPermissionIdType          get() = this as K112_WithdrawPermissionIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K113_VestingBalanceType: AbstractType {
    override val id: K113_VestingBalanceIdType              get() = this as K113_VestingBalanceIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K114_WorkerType: AbstractType {
    override val id: K114_WorkerIdType                      get() = this as K114_WorkerIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K115_BalanceType: AbstractType {
    override val id: K115_BalanceIdType                     get() = this as K115_BalanceIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface K116_HtlcType: AbstractType {
    override val id: K116_HtlcIdType                        get() = this as K116_HtlcIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface K117_CustomAuthorityType: AbstractType {
    override val id: K117_CustomAuthorityIdType             get() = this as K117_CustomAuthorityIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface K118_TicketType: AbstractType {
    override val id: K118_TicketIdType                      get() = this as K118_TicketIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface K119_LiquidityPoolType: AbstractType {
    override val id: K119_LiquidityPoolIdType               get() = this as K119_LiquidityPoolIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface K120_SametFundType: AbstractType {
    override val id: K120_SametFundIdType                   get() = this as K120_SametFundIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface K121_CreditOfferType: AbstractType {
    override val id: K121_CreditOfferIdType                 get() = this as K121_CreditOfferIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K122_CreditDealType: AbstractType {
    override val id: K122_CreditDealIdType                  get() = this as K122_CreditDealIdType
}


@Serializable(with = ObjectIdTypeSerializer::class)
interface K200_GlobalPropertyType: AbstractType {
    override val id: K200_GlobalPropertyIdType              get() = this as K200_GlobalPropertyIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K201_DynamicGlobalPropertyType: AbstractType {
    override val id: K201_DynamicGlobalPropertyIdType       get() = this as K201_DynamicGlobalPropertyIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K202_ReservedType: AbstractType {
    override val id: K202_ReservedIdType                    get() = this as K202_ReservedIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K203_AssetDynamicDataType: AbstractType {
    override val id: K203_AssetDynamicDataIdType            get() = this as K203_AssetDynamicDataIdType
    val currentSupply: UInt64                               get() = 0U
    val confidentialSupply: UInt64                          get() = 0U
    val accumulatedFees: UInt64                             get() = 0U
    val accumulatedCollateralFees: UInt64                   get() = 0U
    val feePool: UInt64                                     get() = 0U
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K204_AssetBitassetDataType: AbstractType {
    override val id: K204_AssetBitassetDataIdType           get() = this as K204_AssetBitassetDataIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K205_AccountBalanceType: AbstractType {
    override val id: K205_AccountBalanceIdType              get() = this as K205_AccountBalanceIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K206_AccountStatisticsType: AbstractType {
    override val id: K206_AccountStatisticsIdType           get() = this as K206_AccountStatisticsIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K207_TransactionHistoryType: AbstractType {
    override val id: K207_TransactionHistoryIdType          get() = this as K207_TransactionHistoryIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K208_BlockSummaryType: AbstractType {
    override val id: K208_BlockSummaryIdType                get() = this as K208_BlockSummaryIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K209_AccountTransactionHistoryType: AbstractType {
    override val id: K209_AccountTransactionHistoryIdType   get() = this as K209_AccountTransactionHistoryIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K210_BlindedBalanceType: AbstractType {
    override val id: K210_BlindedBalanceIdType              get() = this as K210_BlindedBalanceIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K211_ChainPropertyType: AbstractType {
    override val id: K211_ChainPropertyIdType               get() = this as K211_ChainPropertyIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K212_WitnessScheduleType: AbstractType {
    override val id: K211_ChainPropertyIdType               get() = this as K211_ChainPropertyIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K213_BudgetRecordType: AbstractType {
    override val id: K213_BudgetRecordIdType                get() = this as K213_BudgetRecordIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K214_SpecialAuthorityType: AbstractType {
    override val id: K214_SpecialAuthorityIdType            get() = this as K214_SpecialAuthorityIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K215_BuybackType: AbstractType {
    override val id: K215_BuybackIdType                     get() = this as K215_BuybackIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K216_FbaAccumulatorType: AbstractType {
    override val id: K216_FbaAccumulatorIdType              get() = this as K216_FbaAccumulatorIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K217_CollateralBidType: AbstractType {
    override val id: K217_CollateralBidIdType               get() = this as K217_CollateralBidIdType
}

@Serializable(with = ObjectIdTypeSerializer::class)
interface K218_CreditDealSummaryType: AbstractType {
    override val id: K218_CreditDealSummaryIdType           get() = this as K218_CreditDealSummaryIdType
}