package graphene.protocol

import graphene.chain.*
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

val GRAPHENE_SPACE_ENUM_INDEX: Map<uint8_t, ObjectSpace> =
    ObjectSpace.values().associateBy { it.id }
val GRAPHENE_PROTOCOL_TYPE_ENUM_INDEX: Map<uint8_t, ProtocolType> =
    ProtocolType.values().associateBy { it.id }
val GRAPHENE_IMPLEMENTATION_TYPE_ENUM_INDEX: Map<uint8_t, ObjectType> =
    ImplementationType.values().associateBy { it.id }

val GRAPHENE_SPACE_TYPE_INDEX: Map<ObjectSpace, Map<uint8_t, ObjectType>> = mapOf(
    ObjectSpace.PROTOCOL to GRAPHENE_PROTOCOL_TYPE_ENUM_INDEX,
    ObjectSpace.IMPLEMENTATION to GRAPHENE_IMPLEMENTATION_TYPE_ENUM_INDEX,
)

val GRAPHENE_SPACE_ENUMS: List<ObjectSpace> = ObjectSpace.values().toList()

val GRAPHENE_PRO_TYP_ENUMS: List<ProtocolType> = ProtocolType.values().toList()
val GRAPHENE_IMP_TYP_ENUMS: List<ImplementationType> = ImplementationType.values().toList()
val GRAPHENE_OBJ_TYP_ENUMS: List<ObjectType> = GRAPHENE_PRO_TYP_ENUMS + GRAPHENE_IMP_TYP_ENUMS

val GRAPHENE_TYP_CLASS: List<KClass<out AbstractType>> = listOf(
    NullType::class,
    BaseType::class,
    AccountType::class,
    AssetType::class,
    ForceSettlementType::class,
    CommitteeMemberType::class,
    WitnessType::class,
    LimitOrderType::class,
    CallOrderType::class,
    CustomType::class,
    ProposalType::class,
    OperationHistoryType::class,
    WithdrawPermissionType::class,
    VestingBalanceType::class,
    WorkerType::class,
    BalanceType::class,
    HtlcType::class,
    CustomAuthorityType::class,
    TicketType::class,
    LiquidityPoolType::class,
    SametFundType::class,
    CreditOfferType::class,
    CreditDealType::class,
    GlobalPropertyType::class,
    DynamicGlobalPropertyType::class,
    ReservedType::class,
    AssetDynamicDataType::class,
    AssetBitassetDataType::class,
    AccountBalanceType::class,
    AccountStatisticsType::class,
    TransactionHistoryType::class,
    BlockSummaryType::class,
    AccountTransactionHistoryType::class,
    BlindedBalanceType::class,
    ChainPropertyType::class,
    WitnessScheduleType::class,
    BudgetRecordType::class,
    SpecialAuthorityType::class,
    BuybackType::class,
    FbaAccumulatorType::class,
    CollateralBidType::class,
    CreditDealSummaryType::class,
)
val GRAPHENE_IDT_CLASS: List<KClass<out AbstractIdType>> = listOf(
    NullIdType::class,
    BaseIdType::class,
    AccountIdType::class,
    AssetIdType::class,
    ForceSettlementIdType::class,
    CommitteeMemberIdType::class,
    WitnessIdType::class,
    LimitOrderIdType::class,
    CallOrderIdType::class,
    CustomIdType::class,
    ProposalIdType::class,
    OperationHistoryIdType::class,
    WithdrawPermissionIdType::class,
    VestingBalanceIdType::class,
    WorkerIdType::class,
    BalanceIdType::class,
    HtlcIdType::class,
    CustomAuthorityIdType::class,
    TicketIdType::class,
    LiquidityPoolIdType::class,
    SametFundIdType::class,
    CreditOfferIdType::class,
    CreditDealIdType::class,
    GlobalPropertyIdType::class,
    DynamicGlobalPropertyIdType::class,
    ReservedIdType::class,
    AssetDynamicDataIdType::class,
    AssetBitassetDataIdType::class,
    AccountBalanceIdType::class,
    AccountStatisticsIdType::class,
    TransactionHistoryIdType::class,
    BlockSummaryIdType::class,
    AccountTransactionHistoryIdType::class,
    BlindedBalanceIdType::class,
    ChainPropertyIdType::class,
    WitnessScheduleIdType::class,
    BudgetRecordIdType::class,
    SpecialAuthorityIdType::class,
    BuybackIdType::class,
    FbaAccumulatorIdType::class,
    CollateralBidIdType::class,
    CreditDealSummaryIdType::class,
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

    K200_GlobalPropertyObject::class,
    K201_DynamicGlobalPropertyObject::class,
    K202_ReservedObject::class,
    K203_AssetDynamicDataObject::class,
    K204_AssetBitassetDataObject::class,
    K205_AccountBalanceObject::class, //    K205_AccountBalanceObject::class,
    K206_AccountStatisticsObject::class, //    K206_AccountStatisticsObject::class,
    K207_TransactionHistoryObject::class, //    K207_TransactionHistoryObject::class,
    K208_BlockSummaryObject::class, //    K208_BlockSummaryObject::class,
    K209_AccountTransactionHistoryObject::class, //    K209_AccountTransactionHistoryObject::class,
    K210_BlindedBalanceObject::class, //    K210_BlindedBalanceObject::class,
    K211_ChainPropertyObject::class, //    K211_ChainPropertyObject::class,
    K212_WitnessScheduleObject::class, //    K212_WitnessScheduleObject::class,
    K213_BudgetRecordObject::class, //    K213_BudgetRecordObject::class,
    K214_SpecialAuthorityObject::class, //    K214_SpecialAuthorityObject::class,
    K215_BuybackObject::class, //    K215_BuybackObject::class,
    K216_FbaAccumulatorObject::class, //    K216_FbaAccumulatorObject::class,
    K217_CollateralBidObject::class, //    K217_CollateralBidObject::class,
    K218_CreditDealSummaryObject::class, //    K218_CreditDealSummaryObject::class,
)

val GRAPHENE_ID_TYPE_INSTANCES: List<AbstractIdType> = listOf(
    NullIdType(),
    BaseIdType(),
    AccountIdType(),
    AssetIdType(),
    ForceSettlementIdType(),
    CommitteeMemberIdType(),
    WitnessIdType(),
    LimitOrderIdType(),
    CallOrderIdType(),
    CustomIdType(),
    ProposalIdType(),
    OperationHistoryIdType(),
    WithdrawPermissionIdType(),
    VestingBalanceIdType(),
    WorkerIdType(),
    BalanceIdType(),
    HtlcIdType(),
    CustomAuthorityIdType(),
    TicketIdType(),
    LiquidityPoolIdType(),
    SametFundIdType(),
    CreditOfferIdType(),
    CreditDealIdType(),
    GlobalPropertyIdType(),
    DynamicGlobalPropertyIdType(),
    ReservedIdType(),
    AssetDynamicDataIdType(),
    AssetBitassetDataIdType(),
    AccountBalanceIdType(),
    AccountStatisticsIdType(),
    TransactionHistoryIdType(),
    BlockSummaryIdType(),
    AccountTransactionHistoryIdType(),
    BlindedBalanceIdType(),
    ChainPropertyIdType(),
    WitnessScheduleIdType(),
    BudgetRecordIdType(),
    SpecialAuthorityIdType(),
    BuybackIdType(),
    FbaAccumulatorIdType(),
    CollateralBidIdType(),
    CreditDealSummaryIdType(),
)

//val GRAPHENE_OBJECT_TYPE_INSTANCES: List<AbstractObject> by lazy {
//    listOf(
//        INVALID_NULL_OBJECT, // K100_NullIdType(),
//        INVALID_BASE_OBJECT, // K101_BaseIdType(),
//        INVALID_ACCOUNT_OBJECT, // K102_AccountIdType(),
//        K100_NullObject(emptyIdType()), // K103_AssetIdType(),
//        K100_NullObject(emptyIdType()), // K104_ForceSettlementIdType(),
//        K100_NullObject(emptyIdType()), // K105_CommitteeMemberIdType(),
//        K100_NullObject(emptyIdType()), // K106_WitnessIdType(),
//        K100_NullObject(emptyIdType()), // K107_LimitOrderIdType(),
//        K100_NullObject(emptyIdType()), // K108_CallOrderIdType(),
//        K100_NullObject(emptyIdType()), // K109_CustomIdType(),
//        K100_NullObject(emptyIdType()), // K110_ProposalIdType(),
//        K100_NullObject(emptyIdType()), // K111_OperationHistoryIdType(),
//        K100_NullObject(emptyIdType()), // K112_WithdrawPermissionIdType(),
//        K100_NullObject(emptyIdType()), // K113_VestingBalanceIdType(),
//        K100_NullObject(emptyIdType()), // K114_WorkerIdType(),
//        K100_NullObject(emptyIdType()), // K115_BalanceIdType(),
//        K100_NullObject(emptyIdType()), // K116_HtlcIdType(),
//        K100_NullObject(emptyIdType()), // K117_CustomAuthorityIdType(),
//        K100_NullObject(emptyIdType()), // K118_TicketIdType(),
//        K100_NullObject(emptyIdType()), // K119_LiquidityPoolIdType(),
//        K100_NullObject(emptyIdType()), // K120_SametFundIdType(),
//        K100_NullObject(emptyIdType()), // K121_CreditOfferIdType(),
//        K100_NullObject(emptyIdType()), // K122_CreditDealIdType(),
//        K100_NullObject(emptyIdType()), // K200_GlobalPropertyIdType(),
//        K100_NullObject(emptyIdType()), // K201_DynamicGlobalPropertyIdType(),
//        K100_NullObject(emptyIdType()), // K202_ReservedIdType(),
//        K100_NullObject(emptyIdType()), // K203_AssetDynamicIdType(),
//        K100_NullObject(emptyIdType()), // K204_AssetBitassetIdType(),
//        K100_NullObject(emptyIdType()), // K205_AccountBalanceIdType(),
//        K100_NullObject(emptyIdType()), // K206_AccountStatisticsIdType(),
//        K100_NullObject(emptyIdType()), // K207_TransactionHistoryIdType(),
//        K100_NullObject(emptyIdType()), // K208_BlockSummaryIdType(),
//        K100_NullObject(emptyIdType()), // K209_AccountTransactionHistoryIdType(),
//        K100_NullObject(emptyIdType()), // K210_BlindedBalanceIdType(),
//        K100_NullObject(emptyIdType()), // K211_ChainPropertyIdType(),
//        K100_NullObject(emptyIdType()), // K212_WitnessScheduleIdType(),
//        K100_NullObject(emptyIdType()), // K213_BudgetRecordIdType(),
//        K100_NullObject(emptyIdType()), // K214_SpecialAuthorityIdType(),
//        K100_NullObject(emptyIdType()), // K215_BuybackIdType(),
//        K100_NullObject(emptyIdType()), // K216_FbaAccumulatorIdType(),
//        K100_NullObject(emptyIdType()), // K217_CollateralBidIdType(),
//        K100_NullObject(emptyIdType()), // K218_CreditDealSummaryIdType(),
//    )
//}


// maps

val GRAPHENE_TYPE_TO_IDT_CLASS: Map<ObjectType, KClass<out AbstractIdType>> = GRAPHENE_OBJ_TYP_ENUMS.zip(GRAPHENE_IDT_CLASS).toMap()
val GRAPHENE_TYPE_TO_IDT_CONSTRUCTOR: Map<ObjectType, KFunction<AbstractIdType>> = GRAPHENE_TYPE_TO_IDT_CLASS.mapValues { it.value.constructors.first() }

val GRAPHENE_TYPE_TO_OBJ_CLASS: Map<ObjectType, KClass<out AbstractObject>> = GRAPHENE_OBJ_TYP_ENUMS.zip(GRAPHENE_OBJ_CLASS).toMap()

val GRAPHENE_OBJ_CLASS_TO_IDT_CLASS: Map<KClass<out AbstractObject>, KClass<out AbstractIdType>> = GRAPHENE_OBJ_CLASS.zip(GRAPHENE_IDT_CLASS).toMap()



@OptIn(InternalSerializationApi::class)
val GRAPHENE_TYPE_TO_OBJ_SERIALIZER: Map<ObjectType, KSerializer<out AbstractObject>> = GRAPHENE_OBJ_TYP_ENUMS.zip(GRAPHENE_OBJ_CLASS.map { it.serializer() }).toMap()





val GRAPHENE_ID_TYPE_FAST_ALOC: Map<KClass<out AbstractType>, AbstractIdType> =
    GRAPHENE_TYP_CLASS.zip(GRAPHENE_ID_TYPE_INSTANCES).toMap() + GRAPHENE_IDT_CLASS.zip(GRAPHENE_ID_TYPE_INSTANCES).toMap()

//val GRAPHENE_OBJECT_TYPE_FAST_ALOC: Map<KClass<out AbstractType>, AbstractObject> =
//    GRAPHENE_TYP_CLASS.zip(GRAPHENE_OBJECT_TYPE_INSTANCES).toMap() + GRAPHENE_IDT_CLASS.zip(GRAPHENE_OBJECT_TYPE_INSTANCES).toMap()


val GRAPHENE_COMPONENTS_FAST_ALOC: Map<KClass<out GrapheneComponent>, GrapheneComponent> =
    mapOf(
//        Authority::class            to Authority(),
//        AccountOptions::class       to AccountOptions(),
//        AssetOptions::class         to AssetOptions(),
//        PublicKeyType::class        to PublicKeyType(),
//        PrivateKeyType::class       to PrivateKeyType(),
//        SpecialAuthority::class     to SpecialAuthority(),
    )
