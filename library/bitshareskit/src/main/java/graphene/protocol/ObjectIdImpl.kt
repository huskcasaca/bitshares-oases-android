@file:Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
package graphene.protocol

import kotlinx.serialization.Serializable

@Serializable(with = ObjectIdTypeSerializer::class)
abstract class AbstractIdType(
    val space: ObjectSpace,
    val type: ObjectType
) : Cloneable, AbstractType {
    abstract val instance: ObjectInstance

    override fun toString(): String {
        return "${space.id}${type.id}$instance"
    }
    override val id: AbstractIdType = this

}

// PROTOCOL_IDS

@Serializable(with = ObjectIdTypeSerializer::class)
data class NullIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.NULL), NullType {
    override val id: NullIdType = this


}

@Serializable(with = ObjectIdTypeSerializer::class)
data class BaseIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.BASE), BaseType {
    override val id: BaseIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class AccountIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.ACCOUNT), AccountType {
    override val id: AccountIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class AssetIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.ASSET), AssetType {
    override val id: AssetIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class ForceSettlementIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.FORCE_SETTLEMENT), ForceSettlementType {
    override val id: ForceSettlementIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class CommitteeMemberIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.COMMITTEE_MEMBER), CommitteeMemberType {
    override val id: CommitteeMemberIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class WitnessIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.WITNESS), WitnessType {
    override val id: WitnessIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class LimitOrderIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.LIMIT_ORDER), LimitOrderType {
    override val id: LimitOrderIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class CallOrderIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CALL_ORDER), CallOrderType {
    override val id: CallOrderIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class CustomIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CUSTOM), CustomType {
    override val id: CustomIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class ProposalIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.PROPOSAL), ProposalType {
    override val id: ProposalIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class OperationHistoryIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY), OperationHistoryType {
    override val id: OperationHistoryIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class WithdrawPermissionIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.WITHDRAW_PERMISSION), WithdrawPermissionType {
    override val id: WithdrawPermissionIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class VestingBalanceIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.VESTING_BALANCE), VestingBalanceType {
    override val id: VestingBalanceIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class WorkerIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.WORKER), WorkerType {
    override val id: WorkerIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class BalanceIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.BALANCE), BalanceType {
    override val id: BalanceIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class HtlcIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.HTLC), HtlcType {
    override val id: HtlcIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class CustomAuthorityIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CUSTOM_AUTHORITY), CustomAuthorityType {
    override val id: CustomAuthorityIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class TicketIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.TICKET), TicketType {
    override val id: TicketIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class LiquidityPoolIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.LIQUIDITY_POOL), LiquidityPoolType {
    override val id: LiquidityPoolIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class SametFundIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.SAMET_FUND), SametFundType {
    override val id: SametFundIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class CreditOfferIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CREDIT_OFFER), CreditOfferType {
    override val id: CreditOfferIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class CreditDealIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.PROTOCOL, ProtocolType.CREDIT_DEAL), CreditDealType {
    override val id: CreditDealIdType = this
}

// IMPLEMENTATION_IDS
@Serializable(with = ObjectIdTypeSerializer::class)
data class GlobalPropertyIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.GLOBAL_PROPERTY), GlobalPropertyType {
    override val id: GlobalPropertyIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class DynamicGlobalPropertyIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.DYNAMIC_GLOBAL_PROPERTY), DynamicGlobalPropertyType {
    override val id: DynamicGlobalPropertyIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class ReservedIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.RESERVED), ReservedType {
    override val id: ReservedIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class AssetDynamicDataIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ASSET_DYNAMIC_DATA), AssetDynamicDataType {
    override val id: AssetDynamicDataIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class AssetBitassetDataIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ASSET_BITASSET_DATA), AssetBitassetDataType {
    override val id: AssetBitassetDataIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class AccountBalanceIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ACCOUNT_BALANCE), AccountBalanceType {
    override val id: AccountBalanceIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class AccountStatisticsIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ACCOUNT_STATISTICS), AccountStatisticsType {
    override val id: AccountStatisticsIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class TransactionHistoryIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.TRANSACTION_HISTORY), TransactionHistoryType {
    override val id: TransactionHistoryIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class BlockSummaryIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.BLOCK_SUMMARY), BlockSummaryType {
    override val id: BlockSummaryIdType = this
}

@Serializable(with = ObjectIdTypeSerializer::class)
data class AccountTransactionHistoryIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.ACCOUNT_TRANSACTION_HISTORY), AccountTransactionHistoryType {
    override val id: AccountTransactionHistoryIdType = this
}


@Serializable(with = ObjectIdTypeSerializer::class)
data class BlindedBalanceIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.BLINDED_BALANCE), BlindedBalanceType {
    override val id: BlindedBalanceIdType = this
}


@Serializable(with = ObjectIdTypeSerializer::class)
data class ChainPropertyIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.CHAIN_PROPERTY), ChainPropertyType {
    override val id: ChainPropertyIdType = this
}


@Serializable(with = ObjectIdTypeSerializer::class)
data class WitnessScheduleIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.WITNESS_SCHEDULE), WitnessScheduleType {
    override val id: WitnessScheduleIdType = this
}


@Serializable(with = ObjectIdTypeSerializer::class)
data class BudgetRecordIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.BUDGET_RECORD), BudgetRecordType {
    override val id: BudgetRecordIdType = this
}


@Serializable(with = ObjectIdTypeSerializer::class)
data class SpecialAuthorityIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.SPECIAL_AUTHORITY), SpecialAuthorityType {
    override val id: SpecialAuthorityIdType = this
}


@Serializable(with = ObjectIdTypeSerializer::class)
data class BuybackIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.BUYBACK), BuybackType {
    override val id: BuybackIdType = this
}


@Serializable(with = ObjectIdTypeSerializer::class)
data class FbaAccumulatorIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.FBA_ACCUMULATOR), FbaAccumulatorType {
    override val id: FbaAccumulatorIdType = this
}


@Serializable(with = ObjectIdTypeSerializer::class)
data class CollateralBidIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.COLLATERAL_BID), CollateralBidType {
    override val id: CollateralBidIdType = this
}


@Serializable(with = ObjectIdTypeSerializer::class)
data class CreditDealSummaryIdType(
    override val instance: ObjectInstance = INVALID_INSTANCE
) : AbstractIdType(ObjectSpace.IMPLEMENTATION, ImplementationType.CREDIT_DEAL_SUMMARY), CreditDealSummaryType {
    override val id: CreditDealSummaryIdType = this
}