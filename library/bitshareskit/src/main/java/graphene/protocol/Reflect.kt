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

val GRAPHENE_TYP_CLASS: List<KClass<out ObjectIdType>> = listOf(
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
val GRAPHENE_IDT_CLASS: List<KClass<out ObjectId>> = listOf(
    NullId::class,
    BaseId::class,
    AccountId::class,
    AssetId::class,
    ForceSettlementId::class,
    CommitteeMemberId::class,
    WitnessId::class,
    LimitOrderId::class,
    CallOrderId::class,
    CustomId::class,
    ProposalId::class,
    OperationHistoryId::class,
    WithdrawPermissionId::class,
    VestingBalanceId::class,
    WorkerId::class,
    BalanceId::class,
    HtlcId::class,
    CustomAuthorityId::class,
    TicketId::class,
    LiquidityPoolId::class,
    SametFundId::class,
    CreditOfferId::class,
    CreditDealId::class,
    GlobalPropertyId::class,
    DynamicGlobalPropertyId::class,
    ReservedId::class,
    AssetDynamicDataId::class,
    AssetBitassetDataId::class,
    AccountBalanceId::class,
    AccountStatisticsId::class,
    TransactionHistoryId::class,
    BlockSummaryId::class,
    AccountTransactionHistoryId::class,
    BlindedBalanceId::class,
    ChainPropertyId::class,
    WitnessScheduleId::class,
    BudgetRecordId::class,
    SpecialAuthorityId::class,
    BuybackId::class,
    FbaAccumulatorId::class,
    CollateralBidId::class,
    CreditDealSummaryId::class,
)
val GRAPHENE_OBJ_CLASS: List<KClass<out AbstractObject>> = listOf(
    K100_NullObject::class,
    K101_BaseObject::class,
    K102_AccountObject::class,
    K103_AssetObject::class,
    K104_ForceSettlementObject::class,
    K105_CommitteeMemberObject::class,
    K106_WitnessObject::class,
    K107_LimitOrderObject::class,
    K108_CallOrderObject::class,
    K109_CustomObject::class,
    K110_ProposalObject::class,
    K111_OperationHistoryObject::class,
    K112_WithdrawPermissionObject::class,
    K113_VestingBalanceObject::class,
    K114_WorkerObject::class,
    K115_BalanceObject::class,
    K116_HtlcObject::class,
    K117_CustomAuthorityObject::class,
    K118_TicketObject::class,
    K119_LiquidityPoolObject::class,
    K120_SametFundObject::class,
    K121_CreditOfferObject::class,
    K122_CreditDealObject::class,

    K200_GlobalPropertyObject::class,
    K201_DynamicGlobalPropertyObject::class,
    K202_ReservedObject::class,
    K203_AssetDynamicDataObject::class,
    K204_AssetBitassetDataObject::class,
    K205_AccountBalanceObject::class,
    K206_AccountStatisticsObject::class,
    K207_TransactionHistoryObject::class,
    K208_BlockSummaryObject::class,
    K209_AccountTransactionHistoryObject::class,
    K210_BlindedBalanceObject::class,
    K211_ChainPropertyObject::class,
    K212_WitnessScheduleObject::class,
    K213_BudgetRecordObject::class,
    K214_SpecialAuthorityObject::class,
    K215_BuybackObject::class,
    K216_FbaAccumulatorObject::class,
    K217_CollateralBidObject::class,
    K218_CreditDealSummaryObject::class,
)

val GRAPHENE_ID_TYPE_INSTANCES: List<ObjectId> = listOf(
    NullId(),
    BaseId(),
    AccountId(),
    AssetId(),
    ForceSettlementId(),
    CommitteeMemberId(),
    WitnessId(),
    LimitOrderId(),
    CallOrderId(),
    CustomId(),
    ProposalId(),
    OperationHistoryId(),
    WithdrawPermissionId(),
    VestingBalanceId(),
    WorkerId(),
    BalanceId(),
    HtlcId(),
    CustomAuthorityId(),
    TicketId(),
    LiquidityPoolId(),
    SametFundId(),
    CreditOfferId(),
    CreditDealId(),
    GlobalPropertyId(),
    DynamicGlobalPropertyId(),
    ReservedId(),
    AssetDynamicDataId(),
    AssetBitassetDataId(),
    AccountBalanceId(),
    AccountStatisticsId(),
    TransactionHistoryId(),
    BlockSummaryId(),
    AccountTransactionHistoryId(),
    BlindedBalanceId(),
    ChainPropertyId(),
    WitnessScheduleId(),
    BudgetRecordId(),
    SpecialAuthorityId(),
    BuybackId(),
    FbaAccumulatorId(),
    CollateralBidId(),
    CreditDealSummaryId(),
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

val GRAPHENE_TYPE_TO_IDT_CLASS: Map<ObjectType, KClass<out ObjectId>> = GRAPHENE_OBJ_TYP_ENUMS.zip(GRAPHENE_IDT_CLASS).toMap()
val GRAPHENE_TYPE_TO_IDT_CONSTRUCTOR: Map<ObjectType, KFunction<ObjectId>> = GRAPHENE_TYPE_TO_IDT_CLASS.mapValues { it.value.constructors.first() }

val GRAPHENE_TYPE_TO_OBJ_CLASS: Map<ObjectType, KClass<out AbstractObject>> = GRAPHENE_OBJ_TYP_ENUMS.zip(GRAPHENE_OBJ_CLASS).toMap()

val GRAPHENE_OBJ_CLASS_TO_IDT_CLASS: Map<KClass<out AbstractObject>, KClass<out ObjectId>> = GRAPHENE_OBJ_CLASS.zip(GRAPHENE_IDT_CLASS).toMap()



@OptIn(InternalSerializationApi::class)
val GRAPHENE_TYPE_TO_OBJ_SERIALIZER: Map<ObjectType, KSerializer<out AbstractObject>> = GRAPHENE_OBJ_TYP_ENUMS.zip(GRAPHENE_OBJ_CLASS.map { it.serializer() }).toMap()





val GRAPHENE_ID_TYPE_FAST_ALOC: Map<KClass<out ObjectIdType>, ObjectId> =
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
