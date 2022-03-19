@file:Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = ObjectIdTypeSerializer::class)
abstract class AbstractIdType(
    val space: ObjectSpace,
    val type: ObjectType
) : Cloneable, AbstractType {
    abstract val instance: ObjectInstance

    override fun toString(): String {
        return "${space.id}${type.id}$instance"
    }

}

// PROTOCOL_IDS

@Serializable(with = ObjectIdTypeSerializer::class)
data class K100_NullIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.NULL), K100_NullType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K101_BaseIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.BASE), K101_BaseType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K102_AccountIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.ACCOUNT), K102_AccountType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K103_AssetIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.ASSET), K103_AssetType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K104_ForceSettlementIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.FORCE_SETTLEMENT), K104_ForceSettlementType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K105_CommitteeMemberIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.COMMITTEE_MEMBER), K105_CommitteeMemberType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K106_WitnessIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.WITNESS), K106_WitnessType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K107_LimitOrderIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.LIMIT_ORDER), K107_LimitOrderType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K108_CallOrderIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CALL_ORDER), K108_CallOrderType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K109_CustomIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CUSTOM), K109_CustomType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K110_ProposalIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.PROPOSAL), K110_ProposalType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K111_OperationHistoryIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K111_OperationHistoryType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K112_WithdrawPermissionIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.WITHDRAW_PERMISSION), K112_WithdrawPermissionType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K113_VestingBalanceIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.VESTING_BALANCE), K113_VestingBalanceType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K114_WorkerIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.WORKER), K114_WorkerType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K115_BalanceIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.BALANCE), K115_BalanceType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K116_HtlcIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.HTLC), K116_HtlcType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K117_CustomAuthorityIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CUSTOM_AUTHORITY), K117_CustomAuthorityType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K118_TicketIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.TICKET), K118_TicketType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K119_LiquidityPoolIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.LIQUIDITY_POOL), K119_LiquidityPoolType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K120_SametFundIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.SAMET_FUND), K120_SametFundType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K121_CreditOfferIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CREDIT_OFFER), K121_CreditOfferType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K122_CreditDealIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CREDIT_DEAL), K122_CreditDealType

// IMPLEMENTATION_IDS
@Serializable(with = ObjectIdTypeSerializer::class)
data class K200_GlobalPropertyIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.GLOBAL_PROPERTY), K200_GlobalPropertyType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K201_DynamicGlobalPropertyIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.DYNAMIC_GLOBAL_PROPERTY), K201_DynamicGlobalPropertyType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K202_ReservedIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.RESERVED), K202_ReservedType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K203_AssetDynamicIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ASSET_DYNAMIC_DATA), K203_AssetDynamicType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K204_AssetBitassetIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ASSET_BITASSET_DATA), K204_AssetBitassetType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K205_AccountBalanceIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ACCOUNT_BALANCE), K205_AccountBalanceType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K206_AccountStatisticsIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ACCOUNT_STATISTICS), K206_AccountStatisticsType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K207_TransactionHistoryIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.TRANSACTION_HISTORY), K207_TransactionHistoryType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K208_BlockSummaryIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.BLOCK_SUMMARY), K208_BlockSummaryType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K209_AccountTransactionHistoryIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ACCOUNT_TRANSACTION_HISTORY), K209_AccountTransactionHistoryType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K210_BlindedBalanceIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.BLINDED_BALANCE), K210_BlindedBalanceType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K211_ChainPropertyIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.CHAIN_PROPERTY), K211_ChainPropertyType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K212_WitnessScheduleIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.WITNESS_SCHEDULE), K212_WitnessScheduleType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K213_BudgetRecordIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.BUDGET_RECORD), K213_BudgetRecordType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K214_SpecialAuthorityIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.SPECIAL_AUTHORITY), K214_SpecialAuthorityType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K215_BuybackIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.BUYBACK), K215_BuybackType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K216_FbaAccumulatorIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.FBA_ACCUMULATOR), K216_FbaAccumulatorType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K217_CollateralBidIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.COLLATERAL_BID), K217_CollateralBidType

@Serializable(with = ObjectIdTypeSerializer::class)
data class K218_CreditDealSummaryIdType(
    override val instance: ObjectInstance = INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.CREDIT_DEAL_SUMMARY), K218_CreditDealSummaryType



