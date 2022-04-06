@file:Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
package graphene.protocol

import graphene.serializers.ObjectIdSerializer
import kotlinx.serialization.Serializable

@Serializable(with = ObjectIdSerializer::class)
sealed class ObjectId(
    final override val space: ObjectSpace,
    final override val type: ObjectType,
    final override val instance: ObjectInstance,
) : ObjectIdType {

    final override val number: UInt64
    init {
        require(instance shr 48 == 0UL) { "instance overflow" }
        number = space.id.toUInt64() shl 56 or (type.id.toUInt64() shl 48) or instance
    }
    override val id: ObjectId = this

    override fun toString(): String {
        return "${space.id}$GRAPHENE_ID_SEPARATOR${type.id}$GRAPHENE_ID_SEPARATOR$instance"
    }

    override fun hashCode(): Int = number.hashCode()
    override fun equals(other: Any?): Boolean = other is ObjectId && other.number == number
}

// PROTOCOL_IDS

@Serializable(with = ObjectIdSerializer::class)
class NullId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.NULL, instance), NullIdType {
    override val id: NullId = this
}

@Serializable(with = ObjectIdSerializer::class)
class BaseId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.BASE, instance), BaseIdType {
    override val id: BaseId = this
}

@Serializable(with = ObjectIdSerializer::class)
class AccountId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.ACCOUNT, instance), AccountIdType {
    override val id: AccountId = this
}

@Serializable(with = ObjectIdSerializer::class)
class AssetId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.ASSET, instance), AssetIdType {
    override val id: AssetId = this
}

@Serializable(with = ObjectIdSerializer::class)
class ForceSettlementId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.FORCE_SETTLEMENT, instance), ForceSettlementIdType {
    override val id: ForceSettlementId = this
}

@Serializable(with = ObjectIdSerializer::class)
class CommitteeMemberId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.COMMITTEE_MEMBER, instance), CommitteeMemberIdType {
    override val id: CommitteeMemberId = this
}

@Serializable(with = ObjectIdSerializer::class)
class WitnessId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.WITNESS, instance), WitnessIdType {
    override val id: WitnessId = this
}

@Serializable(with = ObjectIdSerializer::class)
class LimitOrderId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.LIMIT_ORDER, instance), LimitOrderIdType {
    override val id: LimitOrderId = this
}

@Serializable(with = ObjectIdSerializer::class)
class CallOrderId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.CALL_ORDER, instance), CallOrderIdType {
    override val id: CallOrderId = this
}

@Serializable(with = ObjectIdSerializer::class)
class CustomId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.CUSTOM, instance), CustomIdType {
    override val id: CustomId = this
}

@Serializable(with = ObjectIdSerializer::class)
class ProposalId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.PROPOSAL, instance), ProposalIdType {
    override val id: ProposalId = this
}

@Serializable(with = ObjectIdSerializer::class)
class OperationHistoryId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.OPERATION_HISTORY, instance), OperationHistoryIdType {
    override val id: OperationHistoryId = this
}

@Serializable(with = ObjectIdSerializer::class)
class WithdrawPermissionId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.WITHDRAW_PERMISSION, instance), WithdrawPermissionIdType {
    override val id: WithdrawPermissionId = this
}

@Serializable(with = ObjectIdSerializer::class)
class VestingBalanceId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.VESTING_BALANCE, instance), VestingBalanceIdType {
    override val id: VestingBalanceId = this
}

@Serializable(with = ObjectIdSerializer::class)
class WorkerId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.WORKER, instance), WorkerIdType {
    override val id: WorkerId = this
}

@Serializable(with = ObjectIdSerializer::class)
class BalanceId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.BALANCE, instance), BalanceIdType {
    override val id: BalanceId = this
}

@Serializable(with = ObjectIdSerializer::class)
class HtlcId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.HTLC, instance), HtlcIdType {
    override val id: HtlcId = this
}

@Serializable(with = ObjectIdSerializer::class)
class CustomAuthorityId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.CUSTOM_AUTHORITY, instance), CustomAuthorityIdType {
    override val id: CustomAuthorityId = this
}

@Serializable(with = ObjectIdSerializer::class)
class TicketId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.TICKET, instance), TicketIdType {
    override val id: TicketId = this
}

@Serializable(with = ObjectIdSerializer::class)
class LiquidityPoolId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.LIQUIDITY_POOL, instance), LiquidityPoolIdType {
    override val id: LiquidityPoolId = this
}

@Serializable(with = ObjectIdSerializer::class)
class SametFundId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.SAMET_FUND, instance), SametFundIdType {
    override val id: SametFundId = this
}

@Serializable(with = ObjectIdSerializer::class)
class CreditOfferId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.CREDIT_OFFER, instance), CreditOfferIdType {
    override val id: CreditOfferId = this
}

@Serializable(with = ObjectIdSerializer::class)
class CreditDealId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.PROTOCOL, ProtocolType.CREDIT_DEAL, instance), CreditDealIdType {
    override val id: CreditDealId = this
}

// IMPLEMENTATION_IDS
@Serializable(with = ObjectIdSerializer::class)
class GlobalPropertyId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.GLOBAL_PROPERTY, instance), GlobalPropertyIdType {
    override val id: GlobalPropertyId = this
}

@Serializable(with = ObjectIdSerializer::class)
class DynamicGlobalPropertyId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.DYNAMIC_GLOBAL_PROPERTY, instance), DynamicGlobalPropertyIdType {
    override val id: DynamicGlobalPropertyId = this
}

@Serializable(with = ObjectIdSerializer::class)
class ReservedId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.RESERVED, instance), ReservedIdType {
    override val id: ReservedId = this
}

@Serializable(with = ObjectIdSerializer::class)
class AssetDynamicDataId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.ASSET_DYNAMIC_DATA, instance), AssetDynamicDataIdType {
    override val id: AssetDynamicDataId = this
}

@Serializable(with = ObjectIdSerializer::class)
class AssetBitassetDataId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.ASSET_BITASSET_DATA, instance), AssetBitassetDataIdType {
    override val id: AssetBitassetDataId = this
}

@Serializable(with = ObjectIdSerializer::class)
class AccountBalanceId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.ACCOUNT_BALANCE, instance), AccountBalanceIdType {
    override val id: AccountBalanceId = this
}

@Serializable(with = ObjectIdSerializer::class)
class AccountStatisticsId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.ACCOUNT_STATISTICS, instance), AccountStatisticsIdType {
    override val id: AccountStatisticsId = this
}

@Serializable(with = ObjectIdSerializer::class)
class TransactionHistoryId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.TRANSACTION_HISTORY, instance), TransactionHistoryIdType {
    override val id: TransactionHistoryId = this
}

@Serializable(with = ObjectIdSerializer::class)
class BlockSummaryId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.BLOCK_SUMMARY, instance), BlockSummaryIdType {
    override val id: BlockSummaryId = this
}

@Serializable(with = ObjectIdSerializer::class)
class AccountTransactionHistoryId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.ACCOUNT_TRANSACTION_HISTORY, instance), AccountTransactionHistoryIdType {
    override val id: AccountTransactionHistoryId = this
}


@Serializable(with = ObjectIdSerializer::class)
class BlindedBalanceId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.BLINDED_BALANCE, instance), BlindedBalanceIdType {
    override val id: BlindedBalanceId = this
}


@Serializable(with = ObjectIdSerializer::class)
class ChainPropertyId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.CHAIN_PROPERTY, instance), ChainPropertyIdType {
    override val id: ChainPropertyId = this
}


@Serializable(with = ObjectIdSerializer::class)
class WitnessScheduleId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.WITNESS_SCHEDULE, instance), WitnessScheduleIdType {
    override val id: WitnessScheduleId = this
}


@Serializable(with = ObjectIdSerializer::class)
class BudgetRecordId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.BUDGET_RECORD, instance), BudgetRecordIdType {
    override val id: BudgetRecordId = this
}


@Serializable(with = ObjectIdSerializer::class)
class SpecialAuthorityId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.SPECIAL_AUTHORITY, instance), SpecialAuthorityIdType {
    override val id: SpecialAuthorityId = this
}


@Serializable(with = ObjectIdSerializer::class)
class BuybackId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.BUYBACK, instance), BuybackIdType {
    override val id: BuybackId = this
}


@Serializable(with = ObjectIdSerializer::class)
class FbaAccumulatorId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.FBA_ACCUMULATOR, instance), FbaAccumulatorIdType {
    override val id: FbaAccumulatorId = this
}


@Serializable(with = ObjectIdSerializer::class)
class CollateralBidId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.COLLATERAL_BID, instance), CollateralBidIdType {
    override val id: CollateralBidId = this
}


@Serializable(with = ObjectIdSerializer::class)
class CreditDealSummaryId(
    instance: ObjectInstance = INVALID_INSTANCE
) : ObjectId(ObjectSpace.IMPLEMENTATION, ImplementationType.CREDIT_DEAL_SUMMARY, instance), CreditDealSummaryIdType {
    override val id: CreditDealSummaryId = this
}