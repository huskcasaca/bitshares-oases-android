package bitshareskit.models

import bitshareskit.extensions.buildJsonObject
import bitshareskit.extensions.optUInt
import bitshareskit.extensions.optULong
import bitshareskit.objects.ByteSerializable
import bitshareskit.objects.JsonSerializable
import bitshareskit.operations.OperationType
import kotlinx.io.core.buildPacket
import kotlinx.io.core.readBytes
import org.java_json.JSONArray
import org.java_json.JSONObject

enum class FeeParams {
    TRANSFER_OPERATION_FEE_PARAMETERS,
    LIMIT_ORDER_CREATE_OPERATION_FEE_PARAMETERS,
    LIMIT_ORDER_CANCEL_OPERATION_FEE_PARAMETERS,
    CALL_ORDER_UPDATE_OPERATION_FEE_PARAMETERS,
    FILL_ORDER_OPERATION_FEE_PARAMETERS,
    ACCOUNT_CREATE_OPERATION_FEE_PARAMETERS,
    ACCOUNT_UPDATE_OPERATION_FEE_PARAMETERS,
    ACCOUNT_WHITELIST_OPERATION_FEE_PARAMETERS,
    ACCOUNT_UPGRADE_OPERATION_FEE_PARAMETERS,
    ACCOUNT_TRANSFER_OPERATION_FEE_PARAMETERS,
    ASSET_CREATE_OPERATION_FEE_PARAMETERS,
    ASSET_UPDATE_OPERATION_FEE_PARAMETERS,
    ASSET_UPDATE_BITASSET_OPERATION_FEE_PARAMETERS,
    ASSET_UPDATE_FEED_PRODUCERS_OPERATION_FEE_PARAMETERS,
    ASSET_ISSUE_OPERATION_FEE_PARAMETERS,
    ASSET_RESERVE_OPERATION_FEE_PARAMETERS,
    ASSET_FUND_FEE_POOL_OPERATION_FEE_PARAMETERS,
    ASSET_SETTLE_OPERATION_FEE_PARAMETERS,
    ASSET_GLOBAL_SETTLE_OPERATION_FEE_PARAMETERS,
    ASSET_PUBLISH_FEED_OPERATION_FEE_PARAMETERS,
    WITNESS_CREATE_OPERATION_FEE_PARAMETERS,
    WITNESS_UPDATE_OPERATION_FEE_PARAMETERS,
    PROPOSAL_CREATE_OPERATION_FEE_PARAMETERS,
    PROPOSAL_UPDATE_OPERATION_FEE_PARAMETERS,
    PROPOSAL_DELETE_OPERATION_FEE_PARAMETERS,
    WITHDRAW_PERMISSION_CREATE_OPERATION_FEE_PARAMETERS,
    WITHDRAW_PERMISSION_UPDATE_OPERATION_FEE_PARAMETERS,
    WITHDRAW_PERMISSION_CLAIM_OPERATION_FEE_PARAMETERS,
    WITHDRAW_PERMISSION_DELETE_OPERATION_FEE_PARAMETERS,
    COMMITTEE_MEMBER_CREATE_OPERATION_FEE_PARAMETERS,
    COMMITTEE_MEMBER_UPDATE_OPERATION_FEE_PARAMETERS,
    COMMITTEE_MEMBER_UPDATE_GLOBAL_PARAMETERS_OPERATION_FEE_PARAMETERS,
    VESTING_BALANCE_CREATE_OPERATION_FEE_PARAMETERS,
    VESTING_BALANCE_WITHDRAW_OPERATION_FEE_PARAMETERS,
    WORKER_CREATE_OPERATION_FEE_PARAMETERS,
    CUSTOM_OPERATION_FEE_PARAMETERS,
    ASSERT_OPERATION_FEE_PARAMETERS,
    BALANCE_CLAIM_OPERATION_FEE_PARAMETERS,
    OVERRIDE_TRANSFER_OPERATION_FEE_PARAMETERS,
    TRANSFER_TO_BLIND_OPERATION_FEE_PARAMETERS,
    BLIND_TRANSFER_OPERATION_FEE_PARAMETERS,
    TRANSFER_FROM_BLIND_OPERATION_FEE_PARAMETERS,
    ASSET_SETTLE_CANCEL_OPERATION_FEE_PARAMETERS,
    ASSET_CLAIM_FEES_OPERATION_FEE_PARAMETERS,
    FBA_DISTRIBUTE_OPERATION_FEE_PARAMETERS,
    BID_COLLATERAL_OPERATION_FEE_PARAMETERS,
    EXECUTE_BID_OPERATION_FEE_PARAMETERS,
    ASSET_CLAIM_POOL_OPERATION_FEE_PARAMETERS,
    ASSET_UPDATE_ISSUER_OPERATION_FEE_PARAMETERS,
    HTLC_CREATE_OPERATION_FEE_PARAMETERS,
    HTLC_REDEEM_OPERATION_FEE_PARAMETERS,
    HTLC_REDEEMED_OPERATION_FEE_PARAMETERS,
    HTLC_EXTEND_OPERATION_FEE_PARAMETERS,
    HTLC_REFUND_OPERATION_FEE_PARAMETERS,
    UNKNOWN_FEE_PARAMETERS
}

val FeeParams.operationType get() = when (this) {
    FeeParams.TRANSFER_OPERATION_FEE_PARAMETERS -> OperationType.TRANSFER_OPERATION
    FeeParams.LIMIT_ORDER_CREATE_OPERATION_FEE_PARAMETERS -> OperationType.LIMIT_ORDER_CREATE_OPERATION
    FeeParams.LIMIT_ORDER_CANCEL_OPERATION_FEE_PARAMETERS -> OperationType.LIMIT_ORDER_CANCEL_OPERATION
    FeeParams.CALL_ORDER_UPDATE_OPERATION_FEE_PARAMETERS -> OperationType.CALL_ORDER_UPDATE_OPERATION
    FeeParams.FILL_ORDER_OPERATION_FEE_PARAMETERS -> OperationType.FILL_ORDER_OPERATION
    FeeParams.ACCOUNT_CREATE_OPERATION_FEE_PARAMETERS -> OperationType.ACCOUNT_CREATE_OPERATION
    FeeParams.ACCOUNT_UPDATE_OPERATION_FEE_PARAMETERS -> OperationType.ACCOUNT_UPDATE_OPERATION
    FeeParams.ACCOUNT_WHITELIST_OPERATION_FEE_PARAMETERS -> OperationType.ACCOUNT_WHITELIST_OPERATION
    FeeParams.ACCOUNT_UPGRADE_OPERATION_FEE_PARAMETERS -> OperationType.ACCOUNT_UPGRADE_OPERATION
    FeeParams.ACCOUNT_TRANSFER_OPERATION_FEE_PARAMETERS -> OperationType.TRANSFER_OPERATION
    FeeParams.ASSET_CREATE_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_CREATE_OPERATION
    FeeParams.ASSET_UPDATE_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_UPDATE_BITASSET_OPERATION
    FeeParams.ASSET_UPDATE_BITASSET_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_UPDATE_BITASSET_OPERATION
    FeeParams.ASSET_UPDATE_FEED_PRODUCERS_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_UPDATE_FEED_PRODUCERS_OPERATION
    FeeParams.ASSET_ISSUE_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_ISSUE_OPERATION
    FeeParams.ASSET_RESERVE_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_RESERVE_OPERATION
    FeeParams.ASSET_FUND_FEE_POOL_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_FUND_FEE_POOL_OPERATION
    FeeParams.ASSET_SETTLE_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_SETTLE_OPERATION
    FeeParams.ASSET_GLOBAL_SETTLE_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_GLOBAL_SETTLE_OPERATION
    FeeParams.ASSET_PUBLISH_FEED_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_PUBLISH_FEED_OPERATION
    FeeParams.WITNESS_CREATE_OPERATION_FEE_PARAMETERS -> OperationType.WITNESS_CREATE_OPERATION
    FeeParams.WITNESS_UPDATE_OPERATION_FEE_PARAMETERS -> OperationType.WITNESS_UPDATE_OPERATION
    FeeParams.PROPOSAL_CREATE_OPERATION_FEE_PARAMETERS -> OperationType.PROPOSAL_CREATE_OPERATION
    FeeParams.PROPOSAL_UPDATE_OPERATION_FEE_PARAMETERS -> OperationType.PROPOSAL_UPDATE_OPERATION
    FeeParams.PROPOSAL_DELETE_OPERATION_FEE_PARAMETERS -> OperationType.PROPOSAL_DELETE_OPERATION
    FeeParams.WITHDRAW_PERMISSION_CREATE_OPERATION_FEE_PARAMETERS -> OperationType.WORKER_CREATE_OPERATION
    FeeParams.WITHDRAW_PERMISSION_UPDATE_OPERATION_FEE_PARAMETERS -> OperationType.WITHDRAW_PERMISSION_UPDATE_OPERATION
    FeeParams.WITHDRAW_PERMISSION_CLAIM_OPERATION_FEE_PARAMETERS -> OperationType.WITHDRAW_PERMISSION_CLAIM_OPERATION
    FeeParams.WITHDRAW_PERMISSION_DELETE_OPERATION_FEE_PARAMETERS -> OperationType.WITHDRAW_PERMISSION_DELETE_OPERATION
    FeeParams.COMMITTEE_MEMBER_CREATE_OPERATION_FEE_PARAMETERS -> OperationType.COMMITTEE_MEMBER_CREATE_OPERATION
    FeeParams.COMMITTEE_MEMBER_UPDATE_OPERATION_FEE_PARAMETERS -> OperationType.COMMITTEE_MEMBER_UPDATE_OPERATION
    FeeParams.COMMITTEE_MEMBER_UPDATE_GLOBAL_PARAMETERS_OPERATION_FEE_PARAMETERS -> OperationType.COMMITTEE_MEMBER_UPDATE_GLOBAL_PARAMETERS_OPERATION
    FeeParams.VESTING_BALANCE_CREATE_OPERATION_FEE_PARAMETERS -> OperationType.VESTING_BALANCE_CREATE_OPERATION
    FeeParams.VESTING_BALANCE_WITHDRAW_OPERATION_FEE_PARAMETERS -> OperationType.VESTING_BALANCE_WITHDRAW_OPERATION
    FeeParams.WORKER_CREATE_OPERATION_FEE_PARAMETERS -> OperationType.WORKER_CREATE_OPERATION
    FeeParams.CUSTOM_OPERATION_FEE_PARAMETERS -> OperationType.CUSTOM_OPERATION
    FeeParams.ASSERT_OPERATION_FEE_PARAMETERS -> OperationType.ASSERT_OPERATION
    FeeParams.BALANCE_CLAIM_OPERATION_FEE_PARAMETERS -> OperationType.BALANCE_CLAIM_OPERATION
    FeeParams.OVERRIDE_TRANSFER_OPERATION_FEE_PARAMETERS -> OperationType.OVERRIDE_TRANSFER_OPERATION
    FeeParams.TRANSFER_TO_BLIND_OPERATION_FEE_PARAMETERS -> OperationType.TRANSFER_TO_BLIND_OPERATION
    FeeParams.BLIND_TRANSFER_OPERATION_FEE_PARAMETERS -> OperationType.BLIND_TRANSFER_OPERATION
    FeeParams.TRANSFER_FROM_BLIND_OPERATION_FEE_PARAMETERS -> OperationType.TRANSFER_FROM_BLIND_OPERATION
    FeeParams.ASSET_SETTLE_CANCEL_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_SETTLE_CANCEL_OPERATION
    FeeParams.ASSET_CLAIM_FEES_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_CLAIM_FEES_OPERATION
    FeeParams.FBA_DISTRIBUTE_OPERATION_FEE_PARAMETERS -> OperationType.FBA_DISTRIBUTE_OPERATION
    FeeParams.BID_COLLATERAL_OPERATION_FEE_PARAMETERS -> OperationType.BID_COLLATERAL_OPERATION
    FeeParams.EXECUTE_BID_OPERATION_FEE_PARAMETERS -> OperationType.EXECUTE_BID_OPERATION
    FeeParams.ASSET_CLAIM_POOL_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_PUBLISH_FEED_OPERATION
    FeeParams.ASSET_UPDATE_ISSUER_OPERATION_FEE_PARAMETERS -> OperationType.ASSET_UPDATE_OPERATION
    FeeParams.HTLC_CREATE_OPERATION_FEE_PARAMETERS -> OperationType.HTLC_CREATE_OPERATION
    FeeParams.HTLC_REDEEM_OPERATION_FEE_PARAMETERS -> OperationType.HTLC_REDEEM_OPERATION
    FeeParams.HTLC_REDEEMED_OPERATION_FEE_PARAMETERS -> OperationType.HTLC_REDEEMED_OPERATION
    FeeParams.HTLC_EXTEND_OPERATION_FEE_PARAMETERS -> OperationType.HTLC_EXTEND_OPERATION
    FeeParams.HTLC_REFUND_OPERATION_FEE_PARAMETERS -> OperationType.HTLC_REFUND_OPERATION
    FeeParams.UNKNOWN_FEE_PARAMETERS -> OperationType.UNDEFINED_OPERATION
}


abstract class FeeParameters: JsonSerializable, ByteSerializable {

    companion object {

        val values = FeeParams.values()

        fun fromJsonPair(rawJson: JSONArray): FeeParameters{
            return fromJson(rawJson.optJSONObject(1), getFeeParamsType(rawJson.optInt(0)))
        }

        fun fromJson(rawJson: JSONObject, type: FeeParams): FeeParameters {
            return when (type) {
                FeeParams.TRANSFER_OPERATION_FEE_PARAMETERS -> TransferOperationFeeParameters.fromJson(rawJson)
                FeeParams.LIMIT_ORDER_CREATE_OPERATION_FEE_PARAMETERS -> LimitOrderCreateOperationFeeParameters.fromJson(rawJson)
                FeeParams.LIMIT_ORDER_CANCEL_OPERATION_FEE_PARAMETERS -> LimitOrderCancelOperationFeeParameters.fromJson(rawJson)
                FeeParams.CALL_ORDER_UPDATE_OPERATION_FEE_PARAMETERS -> CallOrderUpdateOperationFeeParameters.fromJson(rawJson)
                FeeParams.FILL_ORDER_OPERATION_FEE_PARAMETERS -> FillOrderOperationFeeParameters.fromJson(rawJson)
                FeeParams.ACCOUNT_CREATE_OPERATION_FEE_PARAMETERS -> AccountCreateOperationFeeParameters.fromJson(rawJson)
                FeeParams.ACCOUNT_UPDATE_OPERATION_FEE_PARAMETERS -> AccountUpdateOperationFeeParameters.fromJson(rawJson)
                FeeParams.ACCOUNT_WHITELIST_OPERATION_FEE_PARAMETERS -> AccountWhitelistOperationFeeParameters.fromJson(rawJson)
                FeeParams.ACCOUNT_UPGRADE_OPERATION_FEE_PARAMETERS -> AccountUpgradeOperationFeeParameters.fromJson(rawJson)
                FeeParams.ACCOUNT_TRANSFER_OPERATION_FEE_PARAMETERS -> AccountTransferOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_CREATE_OPERATION_FEE_PARAMETERS -> AssetCreateOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_UPDATE_OPERATION_FEE_PARAMETERS -> AssetUpdateOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_UPDATE_BITASSET_OPERATION_FEE_PARAMETERS -> AssetUpdateBitassetOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_UPDATE_FEED_PRODUCERS_OPERATION_FEE_PARAMETERS -> AssetUpdateFeedProducersOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_ISSUE_OPERATION_FEE_PARAMETERS -> AssetIssueOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_RESERVE_OPERATION_FEE_PARAMETERS -> AssetReserveOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_FUND_FEE_POOL_OPERATION_FEE_PARAMETERS -> AssetFundFeePoolOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_SETTLE_OPERATION_FEE_PARAMETERS -> AssetSettleOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_GLOBAL_SETTLE_OPERATION_FEE_PARAMETERS -> AssetGlobalSettleOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_PUBLISH_FEED_OPERATION_FEE_PARAMETERS -> AssetPublishFeedOperationFeeParameters.fromJson(rawJson)
                FeeParams.WITNESS_CREATE_OPERATION_FEE_PARAMETERS -> WitnessCreateOperationFeeParameters.fromJson(rawJson)
                FeeParams.WITNESS_UPDATE_OPERATION_FEE_PARAMETERS -> WitnessUpdateOperationFeeParameters.fromJson(rawJson)
                FeeParams.PROPOSAL_CREATE_OPERATION_FEE_PARAMETERS -> ProposalCreateOperationFeeParameters.fromJson(rawJson)
                FeeParams.PROPOSAL_UPDATE_OPERATION_FEE_PARAMETERS -> ProposalUpdateOperationFeeParameters.fromJson(rawJson)
                FeeParams.PROPOSAL_DELETE_OPERATION_FEE_PARAMETERS -> ProposalDeleteOperationFeeParameters.fromJson(rawJson)
                FeeParams.WITHDRAW_PERMISSION_CREATE_OPERATION_FEE_PARAMETERS -> WithdrawPermissionCreateOperationFeeParameters.fromJson(rawJson)
                FeeParams.WITHDRAW_PERMISSION_UPDATE_OPERATION_FEE_PARAMETERS -> WithdrawPermissionUpdateOperationFeeParameters.fromJson(rawJson)
                FeeParams.WITHDRAW_PERMISSION_CLAIM_OPERATION_FEE_PARAMETERS -> WithdrawPermissionClaimOperationFeeParameters.fromJson(rawJson)
                FeeParams.WITHDRAW_PERMISSION_DELETE_OPERATION_FEE_PARAMETERS -> WithdrawPermissionDeleteOperationFeeParameters.fromJson(rawJson)
                FeeParams.COMMITTEE_MEMBER_CREATE_OPERATION_FEE_PARAMETERS -> CommitteeMemberCreateOperationFeeParameters.fromJson(rawJson)
                FeeParams.COMMITTEE_MEMBER_UPDATE_OPERATION_FEE_PARAMETERS -> CommitteeMemberUpdateOperationFeeParameters.fromJson(rawJson)
                FeeParams.COMMITTEE_MEMBER_UPDATE_GLOBAL_PARAMETERS_OPERATION_FEE_PARAMETERS -> CommitteeMemberUpdateGlobalParametersOperationFeeParameters.fromJson(rawJson)
                FeeParams.VESTING_BALANCE_CREATE_OPERATION_FEE_PARAMETERS -> VestingBalanceCreateOperationFeeParameters.fromJson(rawJson)
                FeeParams.VESTING_BALANCE_WITHDRAW_OPERATION_FEE_PARAMETERS -> VestingBalanceWithdrawOperationFeeParameters.fromJson(rawJson)
                FeeParams.WORKER_CREATE_OPERATION_FEE_PARAMETERS -> WorkerCreateOperationFeeParameters.fromJson(rawJson)
                FeeParams.CUSTOM_OPERATION_FEE_PARAMETERS -> CustomOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSERT_OPERATION_FEE_PARAMETERS -> AssertOperationFeeParameters.fromJson(rawJson)
                FeeParams.BALANCE_CLAIM_OPERATION_FEE_PARAMETERS -> BalanceClaimOperationFeeParameters.fromJson(rawJson)
                FeeParams.OVERRIDE_TRANSFER_OPERATION_FEE_PARAMETERS -> OverrideTransferOperationFeeParameters.fromJson(rawJson)
                FeeParams.TRANSFER_TO_BLIND_OPERATION_FEE_PARAMETERS -> TransferToBlindOperationFeeParameters.fromJson(rawJson)
                FeeParams.BLIND_TRANSFER_OPERATION_FEE_PARAMETERS -> BlindTransferOperationFeeParameters.fromJson(rawJson)
                FeeParams.TRANSFER_FROM_BLIND_OPERATION_FEE_PARAMETERS -> TransferFromBlindOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_SETTLE_CANCEL_OPERATION_FEE_PARAMETERS -> AssetSettleCancelOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_CLAIM_FEES_OPERATION_FEE_PARAMETERS -> AssetClaimFeesOperationFeeParameters.fromJson(rawJson)
                FeeParams.FBA_DISTRIBUTE_OPERATION_FEE_PARAMETERS -> FbaDistributeOperationFeeParameters.fromJson(rawJson)
                FeeParams.BID_COLLATERAL_OPERATION_FEE_PARAMETERS -> BidCollateralOperationFeeParameters.fromJson(rawJson)
                FeeParams.EXECUTE_BID_OPERATION_FEE_PARAMETERS -> ExecuteBidOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_CLAIM_POOL_OPERATION_FEE_PARAMETERS -> AssetClaimPoolOperationFeeParameters.fromJson(rawJson)
                FeeParams.ASSET_UPDATE_ISSUER_OPERATION_FEE_PARAMETERS -> AssetUpdateIssuerOperationFeeParameters.fromJson(rawJson)
                FeeParams.HTLC_CREATE_OPERATION_FEE_PARAMETERS -> HtlcCreateOperationFeeParameters.fromJson(rawJson)
                FeeParams.HTLC_REDEEM_OPERATION_FEE_PARAMETERS -> HtlcRedeemOperationFeeParameters.fromJson(rawJson)
                FeeParams.HTLC_REDEEMED_OPERATION_FEE_PARAMETERS -> HtlcRedeemedOperationFeeParameters.fromJson(rawJson)
                FeeParams.HTLC_EXTEND_OPERATION_FEE_PARAMETERS -> HtlcExtendOperationFeeParameters.fromJson(rawJson)
                FeeParams.HTLC_REFUND_OPERATION_FEE_PARAMETERS -> HtlcRefundOperationFeeParameters.fromJson(rawJson)
                else -> object : FeeParameters() { }
            }
        }

        private fun getFeeParamsType(type: Int): FeeParams {
            return values.getOrElse(type) { FeeParams.UNKNOWN_FEE_PARAMETERS }
        }

    }
    override fun toByteArray(): ByteArray = buildPacket {
    }.readBytes()

    override fun toJsonElement(): JSONObject = buildJsonObject {
    }
    open val type = FeeParams.UNKNOWN_FEE_PARAMETERS

}

// rawJson.optUInt(KEY_PRICE_PER_KBYTE)
const val KEY_PRICE_PER_KBYTE = "price_per_kbyte"
const val KEY_FEE = "fee"

const val KEY_BASIC_FEE = "basic_fee"
const val KEY_PREMIUM_FEE = "premium_fee"

const val KEY_MEMBERSHIP_ANNUAL_FEE = "membership_annual_fee"
const val KEY_MEMBERSHIP_LIFETIME_FEE = "membership_lifetime_fee"

const val KEY_SYMBOL3 = "symbol3"
const val KEY_SYMBOL4 = "symbol4"
const val KEY_LONG_SYMBOL = "long_symbol"

const val KEY_PRICE_PER_OUTPUT = "price_per_output"

const val KEY_FEE_PER_DAY = "fee_per_day"
const val KEY_FEE_PER_KB = "fee_per_kb"



data class TransferOperationFeeParameters(val fee: ULong, val pricePerKByte: UInt): FeeParameters() {
    override val type: FeeParams = FeeParams.TRANSFER_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = TransferOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optUInt(KEY_PRICE_PER_KBYTE)) }
}
data class LimitOrderCreateOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.LIMIT_ORDER_CREATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = LimitOrderCreateOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class LimitOrderCancelOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.LIMIT_ORDER_CANCEL_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = LimitOrderCancelOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class CallOrderUpdateOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.CALL_ORDER_UPDATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = CallOrderUpdateOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
class FillOrderOperationFeeParameters(): FeeParameters() {
    override val type: FeeParams = FeeParams.FILL_ORDER_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = FillOrderOperationFeeParameters() }
}
data class AccountCreateOperationFeeParameters(val basicFee: ULong, val premiumFee: ULong, val pricePerKByte: UInt): FeeParameters() {
    override val type: FeeParams = FeeParams.ACCOUNT_CREATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AccountCreateOperationFeeParameters(rawJson.optULong(KEY_BASIC_FEE), rawJson.optULong(KEY_PREMIUM_FEE), rawJson.optUInt(KEY_PRICE_PER_KBYTE)) }
}
data class AccountUpdateOperationFeeParameters(val fee: ULong, val pricePerKByte: UInt): FeeParameters() {
    override val type: FeeParams = FeeParams.ACCOUNT_UPDATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AccountUpdateOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optUInt(KEY_PRICE_PER_KBYTE)) }
}
data class AccountWhitelistOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ACCOUNT_WHITELIST_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AccountWhitelistOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class AccountUpgradeOperationFeeParameters(val membershipAnnualFee: ULong, val membershipLifetimeFee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ACCOUNT_UPGRADE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AccountUpgradeOperationFeeParameters(rawJson.optULong(KEY_MEMBERSHIP_ANNUAL_FEE), rawJson.optULong(KEY_MEMBERSHIP_LIFETIME_FEE)) }
}
data class AccountTransferOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ACCOUNT_TRANSFER_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AccountTransferOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class AssetCreateOperationFeeParameters(val symbol3: ULong, val symbol4: ULong, val longSymbol: ULong, val pricePerKByte: UInt): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_CREATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetCreateOperationFeeParameters(rawJson.optULong(KEY_SYMBOL3), rawJson.optULong(KEY_SYMBOL4), rawJson.optULong(KEY_LONG_SYMBOL), rawJson.optUInt(KEY_PRICE_PER_KBYTE)) }
}
data class AssetUpdateOperationFeeParameters(val fee: ULong, val pricePerKByte: UInt): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_UPDATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetUpdateOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optUInt(KEY_PRICE_PER_KBYTE)) }
}
data class AssetUpdateBitassetOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_UPDATE_BITASSET_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetUpdateBitassetOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class AssetUpdateFeedProducersOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_UPDATE_FEED_PRODUCERS_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetUpdateFeedProducersOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class AssetIssueOperationFeeParameters(val fee: ULong, val pricePerKByte: UInt): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_ISSUE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetIssueOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optUInt(KEY_PRICE_PER_KBYTE)) }
}
data class AssetReserveOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_RESERVE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetReserveOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class AssetFundFeePoolOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_FUND_FEE_POOL_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetFundFeePoolOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class AssetSettleOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_SETTLE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetSettleOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class AssetGlobalSettleOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_GLOBAL_SETTLE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetGlobalSettleOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class AssetPublishFeedOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_PUBLISH_FEED_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetPublishFeedOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class WitnessCreateOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.WITNESS_CREATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = WitnessCreateOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class WitnessUpdateOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.WITNESS_UPDATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = WitnessUpdateOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class ProposalCreateOperationFeeParameters(val fee: ULong, val pricePerKByte: UInt): FeeParameters() {
    override val type: FeeParams = FeeParams.PROPOSAL_CREATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = ProposalCreateOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optUInt(KEY_PRICE_PER_KBYTE)) }
}
data class ProposalUpdateOperationFeeParameters(val fee: ULong, val pricePerKByte: UInt): FeeParameters() {
    override val type: FeeParams = FeeParams.PROPOSAL_UPDATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = ProposalUpdateOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optUInt(KEY_PRICE_PER_KBYTE)) }
}
data class ProposalDeleteOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.PROPOSAL_DELETE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = ProposalDeleteOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class WithdrawPermissionCreateOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.WITHDRAW_PERMISSION_CREATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = WithdrawPermissionCreateOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class WithdrawPermissionUpdateOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.WITHDRAW_PERMISSION_UPDATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = WithdrawPermissionUpdateOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class WithdrawPermissionClaimOperationFeeParameters(val fee: ULong, val pricePerKByte: UInt): FeeParameters() {
    override val type: FeeParams = FeeParams.WITHDRAW_PERMISSION_CLAIM_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = WithdrawPermissionClaimOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optUInt(KEY_PRICE_PER_KBYTE)) }
}
data class WithdrawPermissionDeleteOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.WITHDRAW_PERMISSION_DELETE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = WithdrawPermissionDeleteOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class CommitteeMemberCreateOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.COMMITTEE_MEMBER_CREATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = CommitteeMemberCreateOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class CommitteeMemberUpdateOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.COMMITTEE_MEMBER_UPDATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = CommitteeMemberUpdateOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class CommitteeMemberUpdateGlobalParametersOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.COMMITTEE_MEMBER_UPDATE_GLOBAL_PARAMETERS_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = CommitteeMemberUpdateGlobalParametersOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class VestingBalanceCreateOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.VESTING_BALANCE_CREATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = VestingBalanceCreateOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class VestingBalanceWithdrawOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.VESTING_BALANCE_WITHDRAW_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = VestingBalanceWithdrawOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class WorkerCreateOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.WORKER_CREATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = WorkerCreateOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class CustomOperationFeeParameters(val fee: ULong, val pricePerKByte: UInt): FeeParameters() {
    override val type: FeeParams = FeeParams.CUSTOM_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = CustomOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optUInt(KEY_PRICE_PER_KBYTE)) }
}
data class AssertOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSERT_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssertOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
class BalanceClaimOperationFeeParameters: FeeParameters() {
    override val type: FeeParams = FeeParams.BALANCE_CLAIM_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = BalanceClaimOperationFeeParameters() }
}
data class OverrideTransferOperationFeeParameters(val fee: ULong, val pricePerKByte: UInt): FeeParameters() {
    override val type: FeeParams = FeeParams.OVERRIDE_TRANSFER_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = OverrideTransferOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optUInt(KEY_PRICE_PER_KBYTE)) }
}
data class TransferToBlindOperationFeeParameters(val fee: ULong, val pricePerOutput: UInt): FeeParameters() {
    override val type: FeeParams = FeeParams.TRANSFER_TO_BLIND_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = TransferToBlindOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optUInt(KEY_PRICE_PER_OUTPUT)) }
}
data class BlindTransferOperationFeeParameters(val fee: ULong, val pricePerOutput: UInt): FeeParameters() {
    override val type: FeeParams = FeeParams.BLIND_TRANSFER_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = BlindTransferOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optUInt(KEY_PRICE_PER_OUTPUT)) }
}
data class TransferFromBlindOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.TRANSFER_FROM_BLIND_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = TransferFromBlindOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
class AssetSettleCancelOperationFeeParameters: FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_SETTLE_CANCEL_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetSettleCancelOperationFeeParameters() }
}
data class AssetClaimFeesOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_CLAIM_FEES_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetClaimFeesOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
class FbaDistributeOperationFeeParameters: FeeParameters() {
    override val type: FeeParams = FeeParams.FBA_DISTRIBUTE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = FbaDistributeOperationFeeParameters() }
}
data class BidCollateralOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.BID_COLLATERAL_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = BidCollateralOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
class ExecuteBidOperationFeeParameters: FeeParameters() {
    override val type: FeeParams = FeeParams.EXECUTE_BID_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = ExecuteBidOperationFeeParameters() }
}
data class AssetClaimPoolOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_CLAIM_POOL_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetClaimPoolOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class AssetUpdateIssuerOperationFeeParameters(val fee: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.ASSET_UPDATE_ISSUER_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = AssetUpdateIssuerOperationFeeParameters(rawJson.optULong(KEY_FEE)) }
}
data class HtlcCreateOperationFeeParameters(val fee: ULong, val feePerDay: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.HTLC_CREATE_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = HtlcCreateOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optULong(KEY_FEE_PER_DAY)) }
}
data class HtlcRedeemOperationFeeParameters(val fee: ULong, val feePerKB: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.HTLC_REDEEM_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = HtlcRedeemOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optULong(KEY_FEE_PER_KB)) }
}
class HtlcRedeemedOperationFeeParameters: FeeParameters() {
    override val type: FeeParams = FeeParams.HTLC_REDEEMED_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = HtlcRedeemedOperationFeeParameters() }
}
data class HtlcExtendOperationFeeParameters(val fee: ULong, val feePerDay: ULong): FeeParameters() {
    override val type: FeeParams = FeeParams.HTLC_EXTEND_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = HtlcExtendOperationFeeParameters(rawJson.optULong(KEY_FEE), rawJson.optULong(KEY_FEE_PER_DAY)) }
}
class HtlcRefundOperationFeeParameters() : FeeParameters() {
    override val type: FeeParams = FeeParams.HTLC_REFUND_OPERATION_FEE_PARAMETERS
    companion object { fun fromJson(rawJson: JSONObject) = HtlcRefundOperationFeeParameters() }
}

//data class FeeSchedule(
//    val transferOperationFeeParameters: TransferOperationFeeParameters,
//    val limitOrderCreateOperationFeeParameters: LimitOrderCreateOperationFeeParameters,
//    val limitOrderCancelOperationFeeParameters: LimitOrderCancelOperationFeeParameters,
//    val callOrderUpdateOperationFeeParameters: CallOrderUpdateOperationFeeParameters,
//    val fillOrderOperationFeeParameters: FillOrderOperationFeeParameters,
//    val accountCreateOperationFeeParameters: AccountCreateOperationFeeParameters,
//    val accountUpdateOperationFeeParameters: AccountUpdateOperationFeeParameters,
//    val accountWhitelistOperationFeeParameters: AccountWhitelistOperationFeeParameters,
//    val accountUpgradeOperationFeeParameters: AccountUpgradeOperationFeeParameters,
//    val accountTransferOperationFeeParameters: AccountTransferOperationFeeParameters,
//    val assetCreateOperationFeeParameters: AssetCreateOperationFeeParameters,
//    val assetUpdateOperationFeeParameters: AssetUpdateOperationFeeParameters,
//    val assetUpdateBitassetOperationFeeParameters: AssetUpdateBitassetOperationFeeParameters,
//    val assetUpdateFeedProducersOperationFeeParameters: AssetUpdateFeedProducersOperationFeeParameters,
//    val assetIssueOperationFeeParameters: AssetIssueOperationFeeParameters,
//    val assetReserveOperationFeeParameters: AssetReserveOperationFeeParameters,
//    val assetFundFeePoolOperationFeeParameters: AssetFundFeePoolOperationFeeParameters,
//    val assetSettleOperationFeeParameters: AssetSettleOperationFeeParameters,
//    val assetGlobalSettleOperationFeeParameters: AssetGlobalSettleOperationFeeParameters,
//    val assetPublishFeedOperationFeeParameters: AssetPublishFeedOperationFeeParameters,
//    val witnessCreateOperationFeeParameters: WitnessCreateOperationFeeParameters,
//    val witnessUpdateOperationFeeParameters: WitnessUpdateOperationFeeParameters,
//    val proposalCreateOperationFeeParameters: ProposalCreateOperationFeeParameters,
//    val proposalUpdateOperationFeeParameters: ProposalUpdateOperationFeeParameters,
//    val proposalDeleteOperationFeeParameters: ProposalDeleteOperationFeeParameters,
//    val withdrawPermissionCreateOperationFeeParameters: WithdrawPermissionCreateOperationFeeParameters,
//    val withdrawPermissionUpdateOperationFeeParameters: WithdrawPermissionUpdateOperationFeeParameters,
//    val withdrawPermissionClaimOperationFeeParameters: WithdrawPermissionClaimOperationFeeParameters,
//    val withdrawPermissionDeleteOperationFeeParameters: WithdrawPermissionDeleteOperationFeeParameters,
//    val committeeMemberCreateOperationFeeParameters: CommitteeMemberCreateOperationFeeParameters,
//    val committeeMemberUpdateOperationFeeParameters: CommitteeMemberUpdateOperationFeeParameters,
//    val committeeMemberUpdateGlobalParametersOperationFeeParameters: CommitteeMemberUpdateGlobalParametersOperationFeeParameters,
//    val vestingBalanceCreateOperationFeeParameters: VestingBalanceCreateOperationFeeParameters,
//    val vestingBalanceWithdrawOperationFeeParameters: VestingBalanceWithdrawOperationFeeParameters,
//    val workerCreateOperationFeeParameters: WorkerCreateOperationFeeParameters,
//    val customOperationFeeParameters: CustomOperationFeeParameters,
//    val assertOperationFeeParameters: AssertOperationFeeParameters,
//    val balanceClaimOperationFeeParameters: BalanceClaimOperationFeeParameters,
//    val overrideTransferOperationFeeParameters: OverrideTransferOperationFeeParameters,
//    val transferToBlindOperationFeeParameters: TransferToBlindOperationFeeParameters,
//    val blindTransferOperationFeeParameters: BlindTransferOperationFeeParameters,
//    val transferFromBlindOperationFeeParameters: TransferFromBlindOperationFeeParameters,
//    val assetSettleCancelOperationFeeParameters: AssetSettleCancelOperationFeeParameters,
//    val assetClaimFeesOperationFeeParameters: AssetClaimFeesOperationFeeParameters,
//    val fbaDistributeOperationFeeParameters: FbaDistributeOperationFeeParameters,
//    val bidCollateralOperationFeeParameters: BidCollateralOperationFeeParameters,
//    val executeBidOperationFeeParameters: ExecuteBidOperationFeeParameters,
//    val assetClaimPoolOperationFeeParameters: AssetClaimPoolOperationFeeParameters,
//    val assetUpdateIssuerOperationFeeParameters: AssetUpdateIssuerOperationFeeParameters,
//    val htlcCreateOperationFeeParameters: HtlcCreateOperationFeeParameters,
//    val htlcRedeemOperationFeeParameters: HtlcRedeemOperationFeeParameters,
//    val htlcRedeemedOperationFeeParameters: HtlcRedeemedOperationFeeParameters,
//    val htlcExtendOperationFeeParameters: HtlcExtendOperationFeeParameters,
//    val htlcRefundOperationFeeParameters: HtlcRefundOperationFeeParameters
//) {
//
//    companion object {
//
//        fun fromJson(rawJson: JSONArray): FeeSchedule {
//            val feeParams = FeeParams.values().toList()
//            val feeMap = rawJson.asIterable<JSONArray>().map { feeParams[it.optInt(0)] to (it.optJSONObject(1) ?: JSONObject()) }.toMap().withDefault { JSONObject() }
//            return FeeSchedule(
//                TransferOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.TRANSFER_OPERATION_FEE_PARAMETERS)),
//                LimitOrderCreateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.LIMIT_ORDER_CREATE_OPERATION_FEE_PARAMETERS)),
//                LimitOrderCancelOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.LIMIT_ORDER_CANCEL_OPERATION_FEE_PARAMETERS)),
//                CallOrderUpdateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.CALL_ORDER_UPDATE_OPERATION_FEE_PARAMETERS)),
//                FillOrderOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.FILL_ORDER_OPERATION_FEE_PARAMETERS)),
//                AccountCreateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ACCOUNT_CREATE_OPERATION_FEE_PARAMETERS)),
//                AccountUpdateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ACCOUNT_UPDATE_OPERATION_FEE_PARAMETERS)),
//                AccountWhitelistOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ACCOUNT_WHITELIST_OPERATION_FEE_PARAMETERS)),
//                AccountUpgradeOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ACCOUNT_UPGRADE_OPERATION_FEE_PARAMETERS)),
//                AccountTransferOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ACCOUNT_TRANSFER_OPERATION_FEE_PARAMETERS)),
//                AssetCreateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_CREATE_OPERATION_FEE_PARAMETERS)),
//                AssetUpdateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_UPDATE_OPERATION_FEE_PARAMETERS)),
//                AssetUpdateBitassetOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_UPDATE_BITASSET_OPERATION_FEE_PARAMETERS)),
//                AssetUpdateFeedProducersOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_UPDATE_FEED_PRODUCERS_OPERATION_FEE_PARAMETERS)),
//                AssetIssueOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_ISSUE_OPERATION_FEE_PARAMETERS)),
//                AssetReserveOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_RESERVE_OPERATION_FEE_PARAMETERS)),
//                AssetFundFeePoolOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_FUND_FEE_POOL_OPERATION_FEE_PARAMETERS)),
//                AssetSettleOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_SETTLE_OPERATION_FEE_PARAMETERS)),
//                AssetGlobalSettleOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_GLOBAL_SETTLE_OPERATION_FEE_PARAMETERS)),
//                AssetPublishFeedOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_PUBLISH_FEED_OPERATION_FEE_PARAMETERS)),
//                WitnessCreateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.WITNESS_CREATE_OPERATION_FEE_PARAMETERS)),
//                WitnessUpdateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.WITNESS_UPDATE_OPERATION_FEE_PARAMETERS)),
//                ProposalCreateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.PROPOSAL_CREATE_OPERATION_FEE_PARAMETERS)),
//                ProposalUpdateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.PROPOSAL_UPDATE_OPERATION_FEE_PARAMETERS)),
//                ProposalDeleteOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.PROPOSAL_DELETE_OPERATION_FEE_PARAMETERS)),
//                WithdrawPermissionCreateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.WITHDRAW_PERMISSION_CREATE_OPERATION_FEE_PARAMETERS)),
//                WithdrawPermissionUpdateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.WITHDRAW_PERMISSION_UPDATE_OPERATION_FEE_PARAMETERS)),
//                WithdrawPermissionClaimOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.WITHDRAW_PERMISSION_CLAIM_OPERATION_FEE_PARAMETERS)),
//                WithdrawPermissionDeleteOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.WITHDRAW_PERMISSION_DELETE_OPERATION_FEE_PARAMETERS)),
//                CommitteeMemberCreateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.COMMITTEE_MEMBER_CREATE_OPERATION_FEE_PARAMETERS)),
//                CommitteeMemberUpdateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.COMMITTEE_MEMBER_UPDATE_OPERATION_FEE_PARAMETERS)),
//                CommitteeMemberUpdateGlobalParametersOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.COMMITTEE_MEMBER_UPDATE_GLOBAL_PARAMETERS_OPERATION_FEE_PARAMETERS)),
//                VestingBalanceCreateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.VESTING_BALANCE_CREATE_OPERATION_FEE_PARAMETERS)),
//                VestingBalanceWithdrawOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.VESTING_BALANCE_WITHDRAW_OPERATION_FEE_PARAMETERS)),
//                WorkerCreateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.WORKER_CREATE_OPERATION_FEE_PARAMETERS)),
//                CustomOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.CUSTOM_OPERATION_FEE_PARAMETERS)),
//                AssertOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSERT_OPERATION_FEE_PARAMETERS)),
//                BalanceClaimOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.BALANCE_CLAIM_OPERATION_FEE_PARAMETERS)),
//                OverrideTransferOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.OVERRIDE_TRANSFER_OPERATION_FEE_PARAMETERS)),
//                TransferToBlindOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.TRANSFER_TO_BLIND_OPERATION_FEE_PARAMETERS)),
//                BlindTransferOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.BLIND_TRANSFER_OPERATION_FEE_PARAMETERS)),
//                TransferFromBlindOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.TRANSFER_FROM_BLIND_OPERATION_FEE_PARAMETERS)),
//                AssetSettleCancelOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_SETTLE_CANCEL_OPERATION_FEE_PARAMETERS)),
//                AssetClaimFeesOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_CLAIM_FEES_OPERATION_FEE_PARAMETERS)),
//                FbaDistributeOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.FBA_DISTRIBUTE_OPERATION_FEE_PARAMETERS)),
//                BidCollateralOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.BID_COLLATERAL_OPERATION_FEE_PARAMETERS)),
//                ExecuteBidOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.EXECUTE_BID_OPERATION_FEE_PARAMETERS)),
//                AssetClaimPoolOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_CLAIM_POOL_OPERATION_FEE_PARAMETERS)),
//                AssetUpdateIssuerOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.ASSET_UPDATE_ISSUER_OPERATION_FEE_PARAMETERS)),
//                HtlcCreateOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.HTLC_CREATE_OPERATION_FEE_PARAMETERS)),
//                HtlcRedeemOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.HTLC_REDEEM_OPERATION_FEE_PARAMETERS)),
//                HtlcRedeemedOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.HTLC_REDEEMED_OPERATION_FEE_PARAMETERS)),
//                HtlcExtendOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.HTLC_EXTEND_OPERATION_FEE_PARAMETERS)),
//                HtlcRefundOperationFeeParameters.fromJson(feeMap.getValue(FeeParams.HTLC_REFUND_OPERATION_FEE_PARAMETERS))
//            )
//        }
//    }
//}






















































