
package graphene.protocol

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

abstract class AbstractIdType(
    val space: ObjectSpace,
    val type: ObjectType
) : Cloneable, AbstractType {
    abstract val instance: ObjectInstance
}

// PROTOCOL_IDS
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K100_NullIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.NULL), K100_NullType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K101_BaseIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.BASE), K101_BaseType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K102_AccountIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.ACCOUNT), K102_AccountType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K103_AssetIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.ASSET), K103_AssetType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K104_ForceSettlementIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.FORCE_SETTLEMENT), K104_ForceSettlementType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K105_CommitteeMemberIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.COMMITTEE_MEMBER), K105_CommitteeMemberType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K106_WitnessIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.WITNESS), K106_WitnessType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K107_LimitOrderIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.LIMIT_ORDER), K107_LimitOrderType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K108_CallOrderIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CALL_ORDER), K108_CallOrderType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K109_CustomIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CUSTOM), K109_CustomType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K110_ProposalIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.PROPOSAL), K110_ProposalType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K111_OperationHistoryIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), K111_OperationHistoryType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K112_WithdrawPermissionIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.WITHDRAW_PERMISSION), K112_WithdrawPermissionType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K113_VestingBalanceIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.VESTING_BALANCE), K113_VestingBalanceType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K114_WorkerIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.WORKER), K114_WorkerType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K115_BalanceIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.BALANCE), K115_BalanceType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K116_HtlcIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.HTLC), K116_HtlcType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K117_CustomAuthorityIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CUSTOM_AUTHORITY), K117_CustomAuthorityType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K118_TicketIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.TICKET), K118_TicketType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K119_LiquidityPoolIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.LIQUIDITY_POOL), K119_LiquidityPoolType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K120_SametFundIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.SAMET_FUND), K120_SametFundType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K121_CreditOfferIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CREDIT_OFFER), K121_CreditOfferType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K122_CreditDealIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CREDIT_DEAL), K122_CreditDealType

// IMPLEMENTATION_IDS
@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K200_GlobalPropertyIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.GLOBAL_PROPERTY), K200_GlobalPropertyType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K201_DynamicGlobalPropertyIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.DYNAMIC_GLOBAL_PROPERTY), K201_DynamicGlobalPropertyType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K202_ReservedIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.RESERVED), K202_ReservedType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K203_AssetDynamicIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ASSET_DYNAMIC_DATA), K203_AssetDynamicType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K204_AssetBitassetIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ASSET_BITASSET_DATA), K204_AssetBitassetType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K205_AccountBalanceIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ACCOUNT_BALANCE), K205_AccountBalanceType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K206_AccountStatisticsIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ACCOUNT_STATISTICS), K206_AccountStatisticsType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K207_TransactionHistoryIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.TRANSACTION_HISTORY), K207_TransactionHistoryType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K208_BlockSummaryIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.BLOCK_SUMMARY), K208_BlockSummaryType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K209_AccountTransactionHistoryIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ACCOUNT_TRANSACTION_HISTORY), K209_AccountTransactionHistoryType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K210_BlindedBalanceIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.BLINDED_BALANCE), K210_BlindedBalanceType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K211_ChainPropertyIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.CHAIN_PROPERTY), K211_ChainPropertyType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K212_WitnessScheduleIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.WITNESS_SCHEDULE), K212_WitnessScheduleType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K213_BudgetRecordIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.BUDGET_RECORD), K213_BudgetRecordType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K214_SpecialAuthorityIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.SPECIAL_AUTHORITY), K214_SpecialAuthorityType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K215_BuybackIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.BUYBACK), K215_BuybackType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K216_FbaAccumulatorIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.FBA_ACCUMULATOR), K216_FbaAccumulatorType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K217_CollateralBidIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.COLLATERAL_BID), K217_CollateralBidType

@Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
@Serializable(with = KGrapheneIdSerializer::class)
data class K218_CreditDealSummaryIdType(
    override val instance: ObjectInstance = ObjectInstance.INVALID_ID
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.CREDIT_DEAL_SUMMARY), K218_CreditDealSummaryType



// id serializer
class KGrapheneIdSerializer<T: AbstractIdType> : KSerializer<T> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("GrapheneId", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): T = decoder.decodeString().toGrapheneObjectId()
    override fun serialize(encoder: Encoder, value: T) = encoder.encodeString(value.standardId)
}
