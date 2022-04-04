package graphene.protocol

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/*  0 */ @Serializable
data class TransferOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_kbyte") val pricePerKbyte: UInt32, // = 10U * GRAPHENE_BLOCKCHAIN_PRECISION, // only required for large memos.
) : FeeParameter()
/*  1 */ @Serializable
data class LimitOrderCreateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 5UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/*  2 */ @Serializable
data class LimitOrderCancelOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 0UL,
) : FeeParameter()
/*  3 */ @Serializable
data class CallOrderUpdateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION;
) : FeeParameter()
/*  4 */ @Serializable
data class FillOrderOperationFeeParameter(
    @Transient val reserved: Unit = Unit
) : FeeParameter()           // Virtual
/*  5 */ @Serializable
data class AccountCreateOperationFeeParameter(
    @SerialName("basic_fee") val basicFee: UInt64, // = 5UL * GRAPHENE_BLOCKCHAIN_PRECISION ///< the cost to register the cheapest non-free account
    @SerialName("premium_fee") val premiumFee: UInt64, // = 2000UL * GRAPHENE_BLOCKCHAIN_PRECISION ///< the cost to register the cheapest non-free account
    @SerialName("price_per_kbyte") val pricePerKbyte: UInt32, // = GRAPHENE_BLOCKCHAIN_PRECISION
) : FeeParameter()
/*  6 */ @Serializable
data class AccountUpdateOperationFeeParameter(
    @SerialName("fee") val fee: ShareType, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_kbyte") val pricePerKbyte: UInt32, // = GRAPHENE_BLOCKCHAIN_PRECISION
) : FeeParameter()
/*  7 */ @Serializable
data class AccountWhitelistOperationFeeParameter(
    @SerialName("fee") val fee: ShareType, //  = 300000
) : FeeParameter()
/*  8 */ @Serializable
data class AccountUpgradeOperationFeeParameter(
    @SerialName("membership_annual_fee") val membershipAnnualFee: UInt64, // = 2000UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("membership_lifetime_fee") val membershipLifetimeFee: UInt64, // = 10000UL * GRAPHENE_BLOCKCHAIN_PRECISION, //< the cost to upgrade to a lifetime member
) : FeeParameter()
/*  9 */ @Serializable
data class AccountTransferOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 500UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 10 */ @Serializable
data class AssetCreateOperationFeeParameter(
    @SerialName("symbol3") val symbol3: UInt64, // = 500000UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("symbol4") val symbol4: UInt64, // = 300000UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("long_symbol") val longSymbol: UInt64, // = 500UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_kbyte") val pricePerKbyte: UInt32, // = 10U, // only required for large memos.
) : FeeParameter()
/* 11 */ @Serializable
data class AssetUpdateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 500UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_kbyte") val pricePerKbyte: UInt32, // = 10,
) : FeeParameter()
/* 12 */ @Serializable
data class AssetUpdateBitassetOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 500UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 13 */ @Serializable
data class AssetUpdateFeedProducersOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 500UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 14 */ @Serializable
data class AssetIssueOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_kbyte") val pricePerKbyte: UInt32, // = GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 15 */ @Serializable
data class AssetReserveOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 16 */ @Serializable
data class AssetFundFeePoolOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 17 */ @Serializable
data class AssetSettleOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 100UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 18 */ @Serializable
data class AssetGlobalSettleOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 500UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 19 */ @Serializable
data class AssetPublishFeedOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 20 */ @Serializable
data class WitnessCreateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 5000UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 21 */ @Serializable
data class WitnessUpdateOperationFeeParameter(
    @SerialName("fee") val fee: ShareType, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 22 */ @Serializable
data class ProposalCreateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_kbyte") val pricePerKbyte: UInt32, // = 10,
) : FeeParameter()
/* 23 */ @Serializable
data class ProposalUpdateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_kbyte") val pricePerKbyte: UInt32, // = 10,
) : FeeParameter()
/* 24 */ @Serializable
data class ProposalDeleteOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 25 */ @Serializable
data class WithdrawPermissionCreateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 26 */ @Serializable
data class WithdrawPermissionUpdateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 27 */ @Serializable
data class WithdrawPermissionClaimOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_kbyte") val pricePerKbyte: UInt32, // = 10,
) : FeeParameter()
/* 28 */ @Serializable
data class WithdrawPermissionDeleteOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 0UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 29 */ @Serializable
data class CommitteeMemberCreateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 5000UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 30 */ @Serializable
data class CommitteeMemberUpdateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 31 */ @Serializable
data class CommitteeMemberUpdateGlobalParametersOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 32 */ @Serializable
data class VestingBalanceCreateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 33 */ @Serializable
data class VestingBalanceWithdrawOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 34 */ @Serializable
data class WorkerCreateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 35 */ @Serializable
data class CustomOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 500UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_kbyte") val pricePerKbyte: UInt32, // = 10,
) : FeeParameter()
/* 36 */ @Serializable
data class AssertOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 37 */ @Serializable
data class BalanceClaimOperationFeeParameter(
    @Transient val reserved: Unit = Unit
) : FeeParameter()
/* 38 */ @Serializable
data class OverrideTransferOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_kbyte") val pricePerKbyte: UInt32, // = 10,
) : FeeParameter()
/* 39 */ @Serializable
data class TransferToBlindOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 5UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_output") val pricePerOutput: UInt32, // = 5U * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 40 */ @Serializable
data class BlindTransferOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 5UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_output") val pricePerOutput: UInt32, // = 5U * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 41 */ @Serializable
data class TransferFromBlindOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 5UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 42 */ @Serializable
data class AssetSettleCancelOperationFeeParameter(
    @Transient val reserved: Unit = Unit
) : FeeParameter()  // Virtual
/* 43 */ @Serializable
data class AssetClaimFeesOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 44 */ @Serializable
data class FbaDistributeOperationFeeParameter(
    @Transient val reserved: Unit = Unit
) : FeeParameter()       // Virtual
/* 45 */ @Serializable
data class BidCollateralOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 46 */ @Serializable
data class ExecuteBidOperationFeeParameter(
    @Transient val reserved: Unit = Unit
) : FeeParameter()          // Virtual
/* 47 */ @Serializable
data class AssetClaimPoolOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 48 */ @Serializable
data class AssetUpdateIssuerOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 20UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 49 */ @Serializable
data class HtlcCreateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("fee_per_day") val feePerDay: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 50 */ @Serializable
data class HtlcRedeemOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("fee_per_kb") val feePerKb: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 51 */ @Serializable
data class HtlcRedeemedOperationFeeParameter(
    @Transient val reserved: Unit = Unit
) : FeeParameter()         // Virtual
/* 52 */ @Serializable
data class HtlcExtendOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("fee_per_day") val feePerDay: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 53 */ @Serializable
data class HtlcRefundOperationFeeParameter(
    @Transient val reserved: Unit = Unit
) : FeeParameter()           // Virtual
/* 54 */ @Serializable
data class CustomAuthorityCreateOperationFeeParameter(
    @SerialName("basic_fee") val basicFee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_byte") val pricePerByte: UInt32, // = GRAPHENE_BLOCKCHAIN_PRECISION / 10UL,
) : FeeParameter()
/* 55 */ @Serializable
data class CustomAuthorityUpdateOperationFeeParameter(
    @SerialName("basic_fee") val basicFee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_byte") val pricePerByte: UInt32, // = GRAPHENE_BLOCKCHAIN_PRECISION / 10UL,
) : FeeParameter()
/* 56 */ @Serializable
data class CustomAuthorityDeleteOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 57 */ @Serializable
data class TicketCreateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 50UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 58 */ @Serializable
data class TicketUpdateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 50UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 59 */ @Serializable
data class LiquidityPoolCreateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 50UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 60 */ @Serializable
data class LiquidityPoolDeleteOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 0UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 61 */ @Serializable
data class LiquidityPoolDepositOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = GRAPHENE_BLOCKCHAIN_PRECISION / 10UL,
) : FeeParameter()
/* 62 */ @Serializable
data class LiquidityPoolWithdrawOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 5UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 63 */ @Serializable
data class LiquidityPoolExchangeOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 64 */ @Serializable
data class SametFundCreateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 65 */ @Serializable
data class SametFundDeleteOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 0UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 66 */ @Serializable
data class SametFundUpdateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 67 */ @Serializable
data class SametFundBorrowOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 68 */ @Serializable
data class SametFundRepayOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 69 */ @Serializable
data class CreditOfferCreateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_kbyte") val pricePerKbyte: UInt32, // = 1U * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 70 */ @Serializable
data class CreditOfferDeleteOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 0UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 71 */ @Serializable
data class CreditOfferUpdateOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
    @SerialName("price_per_kbyte") val pricePerKbyte: UInt32, // = 1U * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 72 */ @Serializable
data class CreditOfferAcceptOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 73 */ @Serializable
data class CreditDealRepayOperationFeeParameter(
    @SerialName("fee") val fee: UInt64, // = 1UL * GRAPHENE_BLOCKCHAIN_PRECISION,
) : FeeParameter()
/* 74 */ @Serializable
data class CreditDealExpiredOperationFeeParameter(
    @Transient val reserved: Unit = Unit
) : FeeParameter()    // Virtual



