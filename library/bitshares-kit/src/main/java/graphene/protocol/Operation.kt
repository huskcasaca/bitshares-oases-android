package graphene.protocol

import graphene.serializers.StaticVarSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OperationWrapper(
    @SerialName("op") val operation: Operation,
)

@Serializable(with = OperationSerializer::class)
sealed class Operation

object OperationSerializer : StaticVarSerializer<Operation>(
    listOf(
        /*  0 */ TransferOperation::class,
        /*  1 */ LimitOrderCreateOperation::class,
        /*  2 */ LimitOrderCancelOperation::class,
        /*  3 */ CallOrderUpdateOperation::class,
        /*  4 */ FillOrderOperation::class,           // Virtual
        /*  5 */ AccountCreateOperation::class,
        /*  6 */ AccountUpdateOperation::class,
        /*  7 */ AccountWhitelistOperation::class,
        /*  8 */ AccountUpgradeOperation::class,
        /*  9 */ AccountTransferOperation::class,
        /* 10 */ AssetCreateOperation::class,
        /* 11 */ AssetUpdateOperation::class,
        /* 12 */ AssetUpdateBitassetOperation::class,
        /* 13 */ AssetUpdateFeedProducersOperation::class,
        /* 14 */ AssetIssueOperation::class,
        /* 15 */ AssetReserveOperation::class,
        /* 16 */ AssetFundFeePoolOperation::class,
        /* 17 */ AssetSettleOperation::class,
        /* 18 */ AssetGlobalSettleOperation::class,
        /* 19 */ AssetPublishFeedOperation::class,
        /* 20 */ WitnessCreateOperation::class,
        /* 21 */ WitnessUpdateOperation::class,
        /* 22 */ ProposalCreateOperation::class,
        /* 23 */ ProposalUpdateOperation::class,
        /* 24 */ ProposalDeleteOperation::class,
        /* 25 */ WithdrawPermissionCreateOperation::class,
        /* 26 */ WithdrawPermissionUpdateOperation::class,
        /* 27 */ WithdrawPermissionClaimOperation::class,
        /* 28 */ WithdrawPermissionDeleteOperation::class,
        /* 29 */ CommitteeMemberCreateOperation::class,
        /* 30 */ CommitteeMemberUpdateOperation::class,
        /* 31 */ CommitteeMemberUpdateGlobalParametersOperation::class,
        /* 32 */ VestingBalanceCreateOperation::class,
        /* 33 */ VestingBalanceWithdrawOperation::class,
        /* 34 */ WorkerCreateOperation::class,
        /* 35 */ CustomOperation::class,
        /* 36 */ AssertOperation::class,
        /* 37 */ BalanceClaimOperation::class,
        /* 38 */ OverrideTransferOperation::class,
        /* 39 */ TransferToBlindOperation::class,
        /* 40 */ BlindTransferOperation::class,
        /* 41 */ TransferFromBlindOperation::class,
        /* 42 */ AssetSettleCancelOperation::class,  // Virtual
        /* 43 */ AssetClaimFeesOperation::class,
        /* 44 */ FbaDistributeOperation::class,       // Virtual
        /* 45 */ BidCollateralOperation::class,
        /* 46 */ ExecuteBidOperation::class,          // Virtual
        /* 47 */ AssetClaimPoolOperation::class,
        /* 48 */ AssetUpdateIssuerOperation::class,
        /* 49 */ HtlcCreateOperation::class,
        /* 50 */ HtlcRedeemOperation::class,
        /* 51 */ HtlcRedeemedOperation::class,         // Virtual
        /* 52 */ HtlcExtendOperation::class,
        /* 53 */ HtlcRefundOperation::class,           // Virtual
        /* 54 */ CustomAuthorityCreateOperation::class,
        /* 55 */ CustomAuthorityUpdateOperation::class,
        /* 56 */ CustomAuthorityDeleteOperation::class,
        /* 57 */ TicketCreateOperation::class,
        /* 58 */ TicketUpdateOperation::class,
        /* 59 */ LiquidityPoolCreateOperation::class,
        /* 60 */ LiquidityPoolDeleteOperation::class,
        /* 61 */ LiquidityPoolDepositOperation::class,
        /* 62 */ LiquidityPoolWithdrawOperation::class,
        /* 63 */ LiquidityPoolExchangeOperation::class,
        /* 64 */ SametFundCreateOperation::class,
        /* 65 */ SametFundDeleteOperation::class,
        /* 66 */ SametFundUpdateOperation::class,
        /* 67 */ SametFundBorrowOperation::class,
        /* 68 */ SametFundRepayOperation::class,
        /* 69 */ CreditOfferCreateOperation::class,
        /* 70 */ CreditOfferDeleteOperation::class,
        /* 71 */ CreditOfferUpdateOperation::class,
        /* 72 */ CreditOfferAcceptOperation::class,
        /* 73 */ CreditDealRepayOperation::class,
        /* 74 */ CreditDealExpiredOperation::class,    // Virtual
    )
)
