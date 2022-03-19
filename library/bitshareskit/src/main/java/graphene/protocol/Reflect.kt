package graphene.protocol

import graphene.chain.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

val GRAPHENE_SPACE_ENUM_INDEX: Map<UInt8, ObjectSpace> =
    ObjectSpace.values().associateBy { it.id }
val GRAPHENE_PROTOCOL_TYPE_ENUM_INDEX: Map<UInt8, ProtocolType> =
    ProtocolType.values().associateBy { it.id }
val GRAPHENE_IMPLEMENTATION_TYPE_ENUM_INDEX: Map<UInt8, ObjectType> =
    ImplementationType.values().associateBy { it.id }

val GRAPHENE_SPACE_TYPE_INDEX: Map<ObjectSpace, Map<UInt8, ObjectType>> = mapOf(
    ObjectSpace.PROTOCOL to GRAPHENE_PROTOCOL_TYPE_ENUM_INDEX,
    ObjectSpace.IMPLEMENTATION to GRAPHENE_IMPLEMENTATION_TYPE_ENUM_INDEX,
)

val GRAPHENE_SPACE_ENUMS: List<ObjectSpace> = ObjectSpace.values().toList()

val GRAPHENE_PRO_TYP_ENUMS: List<ProtocolType> = ProtocolType.values().toList()
val GRAPHENE_IMP_TYP_ENUMS: List<ImplementationType> = ImplementationType.values().toList()
val GRAPHENE_OBJ_TYP_ENUMS: List<ObjectType> = GRAPHENE_PRO_TYP_ENUMS + GRAPHENE_IMP_TYP_ENUMS

val GRAPHENE_TYP_CLASS: List<KClass<out AbstractType>> = listOf(
    K100_NullType::class,
    K101_BaseType::class,
    K102_AccountType::class,
    K103_AssetType::class,
    K104_ForceSettlementType::class,
    K105_CommitteeMemberType::class,
    K106_WitnessType::class,
    K107_LimitOrderType::class,
    K108_CallOrderType::class,
    K109_CustomType::class,
    K110_ProposalType::class,
    K111_OperationHistoryType::class,
    K112_WithdrawPermissionType::class,
    K113_VestingBalanceType::class,
    K114_WorkerType::class,
    K115_BalanceType::class,
    K116_HtlcType::class,
    K117_CustomAuthorityType::class,
    K118_TicketType::class,
    K119_LiquidityPoolType::class,
    K120_SametFundType::class,
    K121_CreditOfferType::class,
    K122_CreditDealType::class,
    K200_GlobalPropertyType::class,
    K201_DynamicGlobalPropertyType::class,
    K202_ReservedType::class,
    K203_AssetDynamicType::class,
    K204_AssetBitassetType::class,
    K205_AccountBalanceType::class,
    K206_AccountStatisticsType::class,
    K207_TransactionHistoryType::class,
    K208_BlockSummaryType::class,
    K209_AccountTransactionHistoryType::class,
    K210_BlindedBalanceType::class,
    K211_ChainPropertyType::class,
    K212_WitnessScheduleType::class,
    K213_BudgetRecordType::class,
    K214_SpecialAuthorityType::class,
    K215_BuybackType::class,
    K216_FbaAccumulatorType::class,
    K217_CollateralBidType::class,
    K218_CreditDealSummaryType::class,
)
val GRAPHENE_IDT_CLASS: List<KClass<out AbstractIdType>> = listOf(
    K100_NullIdType::class,
    K101_BaseIdType::class,
    K102_AccountIdType::class,
    K103_AssetIdType::class,
    K104_ForceSettlementIdType::class,
    K105_CommitteeMemberIdType::class,
    K106_WitnessIdType::class,
    K107_LimitOrderIdType::class,
    K108_CallOrderIdType::class,
    K109_CustomIdType::class,
    K110_ProposalIdType::class,
    K111_OperationHistoryIdType::class,
    K112_WithdrawPermissionIdType::class,
    K113_VestingBalanceIdType::class,
    K114_WorkerIdType::class,
    K115_BalanceIdType::class,
    K116_HtlcIdType::class,
    K117_CustomAuthorityIdType::class,
    K118_TicketIdType::class,
    K119_LiquidityPoolIdType::class,
    K120_SametFundIdType::class,
    K121_CreditOfferIdType::class,
    K122_CreditDealIdType::class,
    K200_GlobalPropertyIdType::class,
    K201_DynamicGlobalPropertyIdType::class,
    K202_ReservedIdType::class,
    K203_AssetDynamicIdType::class,
    K204_AssetBitassetIdType::class,
    K205_AccountBalanceIdType::class,
    K206_AccountStatisticsIdType::class,
    K207_TransactionHistoryIdType::class,
    K208_BlockSummaryIdType::class,
    K209_AccountTransactionHistoryIdType::class,
    K210_BlindedBalanceIdType::class,
    K211_ChainPropertyIdType::class,
    K212_WitnessScheduleIdType::class,
    K213_BudgetRecordIdType::class,
    K214_SpecialAuthorityIdType::class,
    K215_BuybackIdType::class,
    K216_FbaAccumulatorIdType::class,
    K217_CollateralBidIdType::class,
    K218_CreditDealSummaryIdType::class,
)
val GRAPHENE_OBJ_CLASS: List<KClass<out AbstractObject>> = listOf(
    K100_NullObject::class,
    K101_BaseObject::class,
    K102_AccountObject::class,
    K103_AssetObject::class,

    AbstractObject::class, //    K104_ForceSettlementObject::class,
    AbstractObject::class, //    K105_CommitteeMemberObject::class,
    AbstractObject::class, //    K106_WitnessObject::class,
    AbstractObject::class, //    K107_LimitOrderObject::class,
    AbstractObject::class, //    K108_CallOrderObject::class,
    AbstractObject::class, //    K109_CustomObject::class,
    AbstractObject::class, //    K110_ProposalObject::class,
    AbstractObject::class, //    K111_OperationHistoryObject::class,
    AbstractObject::class, //    K112_WithdrawPermissionObject::class,
    AbstractObject::class, //    K113_VestingBalanceObject::class,
    AbstractObject::class, //    K114_WorkerObject::class,
    AbstractObject::class, //    K115_BalanceObject::class,
    AbstractObject::class, //    K116_HtlcObject::class,
    AbstractObject::class, //    K117_CustomAuthorityObject::class,
    AbstractObject::class, //    K118_TicketObject::class,
    AbstractObject::class, //    K119_LiquidityPoolObject::class,
    AbstractObject::class, //    K120_SametFundObject::class,
    AbstractObject::class, //    K121_CreditOfferObject::class,
    AbstractObject::class, //    K122_CreditDealObject::class,
    AbstractObject::class, //    K200_GlobalPropertyObject::class,
    AbstractObject::class, //    K201_DynamicGlobalPropertyObject::class,
    AbstractObject::class, //    K202_ReservedObject::class,
    AbstractObject::class, //    K203_AssetDynamicObject::class,
    AbstractObject::class, //    K204_AssetBitassetObject::class,
    AbstractObject::class, //    K205_AccountBalanceObject::class,
    AbstractObject::class, //    K206_AccountStatisticsObject::class,
    AbstractObject::class, //    K207_TransactionHistoryObject::class,
    AbstractObject::class, //    K208_BlockSummaryObject::class,
    AbstractObject::class, //    K209_AccountTransactionHistoryObject::class,
    AbstractObject::class, //    K210_BlindedBalanceObject::class,
    AbstractObject::class, //    K211_ChainPropertyObject::class,
    AbstractObject::class, //    K212_WitnessScheduleObject::class,
    AbstractObject::class, //    K213_BudgetRecordObject::class,
    AbstractObject::class, //    K214_SpecialAuthorityObject::class,
    AbstractObject::class, //    K215_BuybackObject::class,
    AbstractObject::class, //    K216_FbaAccumulatorObject::class,
    AbstractObject::class, //    K217_CollateralBidObject::class,
    AbstractObject::class, //    K218_CreditDealSummaryObject::class,
)

val GRAPHENE_ID_TYPE_INSTANCES: List<AbstractIdType> = listOf(
    K100_NullIdType(),
    K101_BaseIdType(),
    K102_AccountIdType(),
    K103_AssetIdType(),
    K104_ForceSettlementIdType(),
    K105_CommitteeMemberIdType(),
    K106_WitnessIdType(),
    K107_LimitOrderIdType(),
    K108_CallOrderIdType(),
    K109_CustomIdType(),
    K110_ProposalIdType(),
    K111_OperationHistoryIdType(),
    K112_WithdrawPermissionIdType(),
    K113_VestingBalanceIdType(),
    K114_WorkerIdType(),
    K115_BalanceIdType(),
    K116_HtlcIdType(),
    K117_CustomAuthorityIdType(),
    K118_TicketIdType(),
    K119_LiquidityPoolIdType(),
    K120_SametFundIdType(),
    K121_CreditOfferIdType(),
    K122_CreditDealIdType(),
    K200_GlobalPropertyIdType(),
    K201_DynamicGlobalPropertyIdType(),
    K202_ReservedIdType(),
    K203_AssetDynamicIdType(),
    K204_AssetBitassetIdType(),
    K205_AccountBalanceIdType(),
    K206_AccountStatisticsIdType(),
    K207_TransactionHistoryIdType(),
    K208_BlockSummaryIdType(),
    K209_AccountTransactionHistoryIdType(),
    K210_BlindedBalanceIdType(),
    K211_ChainPropertyIdType(),
    K212_WitnessScheduleIdType(),
    K213_BudgetRecordIdType(),
    K214_SpecialAuthorityIdType(),
    K215_BuybackIdType(),
    K216_FbaAccumulatorIdType(),
    K217_CollateralBidIdType(),
    K218_CreditDealSummaryIdType(),
)


// maps

val GRAPHENE_TYPE_TO_IDT_CLASS: Map<ObjectType, KClass<out AbstractIdType>> = GRAPHENE_OBJ_TYP_ENUMS.zip(GRAPHENE_IDT_CLASS).toMap()
val GRAPHENE_TYPE_TO_IDT_CONSTRUCTOR: Map<ObjectType, KFunction<AbstractIdType>> = GRAPHENE_TYPE_TO_IDT_CLASS.mapValues { it.value.constructors.first() }

val GRAPHENE_TYPE_TO_OBJ_CLASS: Map<ObjectType, KClass<out AbstractObject>> = GRAPHENE_OBJ_TYP_ENUMS.zip(GRAPHENE_OBJ_CLASS).toMap()

@OptIn(InternalSerializationApi::class)
val GRAPHENE_TYPE_TO_OBJ_SERIALIZER: Map<ObjectType, KSerializer<out AbstractObject>> = GRAPHENE_OBJ_TYP_ENUMS.zip(GRAPHENE_OBJ_CLASS.map { it.serializer() }).toMap()





val GRAPHENE_ID_TYPE_FAST_ALOC: Map<KClass<out AbstractType>, AbstractIdType> =
    GRAPHENE_TYP_CLASS.zip(GRAPHENE_ID_TYPE_INSTANCES).toMap() + GRAPHENE_IDT_CLASS.zip(GRAPHENE_ID_TYPE_INSTANCES).toMap()

val GRAPHENE_COMPONENTS_FAST_ALOC: Map<KClass<out GrapheneComponent>, GrapheneComponent> =
    mapOf(
        Authority::class            to Authority(),
        AccountOptions::class       to AccountOptions(),
        AssetOptions::class         to AssetOptions(),
        PublicKeyType::class        to PublicKeyType(),
        PrivateKeyType::class       to PrivateKeyType(),
        SpecialAuthority::class     to SpecialAuthority(),
    )
