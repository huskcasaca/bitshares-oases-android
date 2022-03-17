package graphene.protocol

import kotlinx.serialization.Serializable

interface AbstractType: Comparable<AbstractType> {
    val id: AbstractIdType                                  get() = this as AbstractIdType

    override fun compareTo(other: AbstractType): Int {
        return id.instance.compareTo(other.id.instance)
    }
}

interface K100_NullType: AbstractType {
    override val id: K100_NullIdType                        get() = this as K100_NullIdType
}

interface K101_BaseType: AbstractType {
    override val id: K101_BaseIdType                        get() = this as K101_BaseIdType
}

interface K102_AccountType: AbstractType {
    override val id: K102_AccountIdType                     get() = this as K102_AccountIdType
    val membershipExpirationDate: ChainTimePoint            get() = ChainTimePoint.STANDARD_EXPIRATION_DATE_TIME
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

    val ownerSpecialAuthority: SpecialAuthority             get() = emptyComponent()
    val activeSpecialAuthority: SpecialAuthority            get() = emptyComponent()
    val topNControlFlags: UInt8                             get() = 0U
}

interface K103_AssetType: AbstractType {
    override val id: K103_AssetIdType                       get() = this as K103_AssetIdType
    val symbol: String                                      get() = emptyString()
    val issuerId: K102_AccountType                          get() = emptyIdType()
    val precision: UByte                                    get() = 0U
    val options: AssetOptions                               get() = emptyComponent()
    val dynamicDataId: K203_AssetDynamicType                get() = emptyIdType()
//    var bitassetDataId: KAssetBitassetIdType = emptyIdType(),
}

interface K104_ForceSettlementType: AbstractType {
    override val id: K104_ForceSettlementIdType             get() = this as K104_ForceSettlementIdType
}

interface K105_CommitteeMemberType: AbstractType {
    override val id: K105_CommitteeMemberIdType             get() = this as K105_CommitteeMemberIdType
}

interface K106_WitnessType: AbstractType {
    override val id: K106_WitnessIdType                     get() = this as K106_WitnessIdType
}

interface K107_LimitOrderType: AbstractType {
    override val id: K107_LimitOrderIdType                  get() = this as K107_LimitOrderIdType
}

interface K108_CallOrderType: AbstractType {
    override val id: K108_CallOrderIdType                   get() = this as K108_CallOrderIdType
}

interface K109_CustomType: AbstractType {
    override val id: K109_CustomIdType                      get() = this as K109_CustomIdType
}

interface K110_ProposalType: AbstractType {
    override val id: K110_ProposalIdType                    get() = this as K110_ProposalIdType
}

interface K111_OperationHistoryType: AbstractType {
    override val id: K111_OperationHistoryIdType            get() = this as K111_OperationHistoryIdType
}

interface K112_WithdrawPermissionType: AbstractType {
    override val id: K112_WithdrawPermissionIdType          get() = this as K112_WithdrawPermissionIdType
}

interface K113_VestingBalanceType: AbstractType {
    override val id: K113_VestingBalanceIdType              get() = this as K113_VestingBalanceIdType
}

interface K114_WorkerType: AbstractType {
    override val id: K114_WorkerIdType                      get() = this as K114_WorkerIdType
}

interface K115_BalanceType: AbstractType {
    override val id: K115_BalanceIdType                     get() = this as K115_BalanceIdType
}


interface K116_HtlcType: AbstractType {
    override val id: K116_HtlcIdType                        get() = this as K116_HtlcIdType
}


interface K117_CustomAuthorityType: AbstractType {
    override val id: K117_CustomAuthorityIdType             get() = this as K117_CustomAuthorityIdType
}


interface K118_TicketType: AbstractType {
    override val id: K118_TicketIdType                      get() = this as K118_TicketIdType
}


interface K119_LiquidityPoolType: AbstractType {
    override val id: K119_LiquidityPoolIdType               get() = this as K119_LiquidityPoolIdType
}


interface K120_SametFundType: AbstractType {
    override val id: K120_SametFundIdType                   get() = this as K120_SametFundIdType
}


interface K121_CreditOfferType: AbstractType {
    override val id: K121_CreditOfferIdType                 get() = this as K121_CreditOfferIdType
}

interface K122_CreditDealType: AbstractType {
    override val id: K122_CreditDealIdType                  get() = this as K122_CreditDealIdType
}


interface K200_GlobalPropertyType: AbstractType {
    override val id: K200_GlobalPropertyIdType              get() = this as K200_GlobalPropertyIdType
}

interface K201_DynamicGlobalPropertyType: AbstractType {
    override val id: K201_DynamicGlobalPropertyIdType       get() = this as K201_DynamicGlobalPropertyIdType
}

interface K202_ReservedType: AbstractType {
    override val id: K202_ReservedIdType                    get() = this as K202_ReservedIdType
}

interface K203_AssetDynamicType: AbstractType {
    override val id: K203_AssetDynamicIdType                get() = this as K203_AssetDynamicIdType
    val currentSupply: UInt64                               get() = 0U
    val confidentialSupply: UInt64                          get() = 0U
    val accumulatedFees: UInt64                             get() = 0U
    val accumulatedCollateralFees: UInt64                   get() = 0U
    val feePool: UInt64                                     get() = 0U
}

interface K204_AssetBitassetType: AbstractType {
    override val id: K204_AssetBitassetIdType               get() = this as K204_AssetBitassetIdType
    val currentSupply: UInt64                               get() = 0U
    val confidentialSupply: UInt64                          get() = 0U
    val accumulatedFees: UInt64                             get() = 0U
    val accumulatedCollateralFees: UInt64                   get() = 0U
    val feePool: UInt64                                     get() = 0U
}

interface K205_AccountBalanceType: AbstractType {
    override val id: K205_AccountBalanceIdType              get() = this as K205_AccountBalanceIdType
}


interface K206_AccountStatisticsType: AbstractType {
    override val id: K206_AccountStatisticsIdType           get() = this as K206_AccountStatisticsIdType
}

interface K207_TransactionHistoryType: AbstractType {
    override val id: K207_TransactionHistoryIdType          get() = this as K207_TransactionHistoryIdType
}

interface K208_BlockSummaryType: AbstractType {
    override val id: K208_BlockSummaryIdType                get() = this as K208_BlockSummaryIdType
}

interface K209_AccountTransactionHistoryType: AbstractType {
    override val id: K209_AccountTransactionHistoryIdType   get() = this as K209_AccountTransactionHistoryIdType
}

interface K210_BlindedBalanceType: AbstractType {
    override val id: K210_BlindedBalanceIdType              get() = this as K210_BlindedBalanceIdType
}

interface K211_ChainPropertyType: AbstractType {
    override val id: K211_ChainPropertyIdType               get() = this as K211_ChainPropertyIdType
}

interface K212_WitnessScheduleType: AbstractType {
    override val id: K211_ChainPropertyIdType               get() = this as K211_ChainPropertyIdType
}

interface K213_BudgetRecordType: AbstractType {
    override val id: K213_BudgetRecordIdType                get() = this as K213_BudgetRecordIdType
}

interface K214_SpecialAuthorityType: AbstractType {
    override val id: K214_SpecialAuthorityIdType            get() = this as K214_SpecialAuthorityIdType
}

interface K215_BuybackType: AbstractType {
    override val id: K215_BuybackIdType                     get() = this as K215_BuybackIdType
}

interface K216_FbaAccumulatorType: AbstractType {
    override val id: K216_FbaAccumulatorIdType              get() = this as K216_FbaAccumulatorIdType
}

interface K217_CollateralBidType: AbstractType {
    override val id: K217_CollateralBidIdType               get() = this as K217_CollateralBidIdType
}

interface K218_CreditDealSummaryType: AbstractType {
    override val id: K218_CreditDealSummaryIdType           get() = this as K218_CreditDealSummaryIdType
}