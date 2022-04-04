package graphene.protocol

import kotlinx.serialization.Serializable

@Serializable
sealed class Operation

/*  0 */ @Serializable data class TransferOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/*  1 */ @Serializable data class LimitOrderCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/*  2 */ @Serializable data class LimitOrderCancelOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/*  3 */ @Serializable data class CallOrderUpdateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/*  4 */ @Serializable data class FillOrderOperation(
    @Transient val reserved: Unit = Unit
) : Operation()           // Virtual
/*  5 */ @Serializable data class AccountCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/*  6 */ @Serializable data class AccountUpdateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/*  7 */ @Serializable data class AccountWhitelistOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/*  8 */ @Serializable data class AccountUpgradeOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/*  9 */ @Serializable data class AccountTransferOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 10 */ @Serializable data class AssetCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 11 */ @Serializable data class AssetUpdateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 12 */ @Serializable data class AssetUpdateBitassetOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 13 */ @Serializable data class AssetUpdateFeedProducersOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 14 */ @Serializable data class AssetIssueOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 15 */ @Serializable data class AssetReserveOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 16 */ @Serializable data class AssetFundFeePoolOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 17 */ @Serializable data class AssetSettleOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 18 */ @Serializable data class AssetGlobalSettleOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 19 */ @Serializable data class AssetPublishFeedOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 20 */ @Serializable data class WitnessCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 21 */ @Serializable data class WitnessUpdateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 22 */ @Serializable data class ProposalCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 23 */ @Serializable data class ProposalUpdateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 24 */ @Serializable data class ProposalDeleteOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 25 */ @Serializable data class WithdrawPermissionCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 26 */ @Serializable data class WithdrawPermissionUpdateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 27 */ @Serializable data class WithdrawPermissionClaimOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 28 */ @Serializable data class WithdrawPermissionDeleteOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 29 */ @Serializable data class CommitteeMemberCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 30 */ @Serializable data class CommitteeMemberUpdateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 31 */ @Serializable data class CommitteeMemberUpdateGlobalParametersOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 32 */ @Serializable data class VestingBalanceCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 33 */ @Serializable data class VestingBalanceWithdrawOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 34 */ @Serializable data class WorkerCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 35 */ @Serializable data class CustomOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 36 */ @Serializable data class AssertOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 37 */ @Serializable data class BalanceClaimOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 38 */ @Serializable data class OverrideTransferOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 39 */ @Serializable data class TransferToBlindOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 40 */ @Serializable data class BlindTransferOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 41 */ @Serializable data class TransferFromBlindOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 42 */ @Serializable data class AssetSettleCancelOperation(
    @Transient val reserved: Unit = Unit
) : Operation()  // Virtual
/* 43 */ @Serializable data class AssetClaimFeesOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 44 */ @Serializable data class FbaDistributeOperation(
    @Transient val reserved: Unit = Unit
) : Operation()       // Virtual
/* 45 */ @Serializable data class BidCollateralOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 46 */ @Serializable data class ExecuteBidOperation(
    @Transient val reserved: Unit = Unit
) : Operation()          // Virtual
/* 47 */ @Serializable data class AssetClaimPoolOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 48 */ @Serializable data class AssetUpdateIssuerOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 49 */ @Serializable data class HtlcCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 50 */ @Serializable data class HtlcRedeemOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 51 */ @Serializable data class HtlcRedeemedOperation(
    @Transient val reserved: Unit = Unit
) : Operation()         // Virtual
/* 52 */ @Serializable data class HtlcExtendOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 53 */ @Serializable data class HtlcRefundOperation(
    @Transient val reserved: Unit = Unit
) : Operation()           // Virtual
/* 54 */ @Serializable data class CustomAuthorityCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 55 */ @Serializable data class CustomAuthorityUpdateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 56 */ @Serializable data class CustomAuthorityDeleteOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 57 */ @Serializable data class TicketCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 58 */ @Serializable data class TicketUpdateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 59 */ @Serializable data class LiquidityPoolCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 60 */ @Serializable data class LiquidityPoolDeleteOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 61 */ @Serializable data class LiquidityPoolDepositOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 62 */ @Serializable data class LiquidityPoolWithdrawOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 63 */ @Serializable data class LiquidityPoolExchangeOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 64 */ @Serializable data class SametFundCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 65 */ @Serializable data class SametFundDeleteOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 66 */ @Serializable data class SametFundUpdateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 67 */ @Serializable data class SametFundBorrowOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 68 */ @Serializable data class SametFundRepayOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 69 */ @Serializable data class CreditOfferCreateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 70 */ @Serializable data class CreditOfferDeleteOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 71 */ @Serializable data class CreditOfferUpdateOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 72 */ @Serializable data class CreditOfferAcceptOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 73 */ @Serializable data class CreditDealRepayOperation(
    @Transient val reserved: Unit = Unit
) : Operation()
/* 74 */ @Serializable data class CreditDealExpiredOperation(
    @Transient val reserved: Unit = Unit
) : Operation()    // Virtual