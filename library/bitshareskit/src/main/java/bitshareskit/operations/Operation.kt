package bitshareskit.operations

import bitshareskit.ks_chain.Authority
import bitshareskit.chain.ChainConfig
import bitshareskit.models.AssetAmount
import bitshareskit.objects.AssetObject
import bitshareskit.objects.GrapheneSortSerializable
import bitshareskit.serializer.grapheneGlobalComparator
import org.java_json.JSONArray
import org.java_json.JSONObject
import java.io.Serializable
import java.time.Instant
import java.util.*


abstract class Operation : GrapheneSortSerializable, Serializable {

    companion object {
        const val KEY_FEE = "fee"
        const val KEY_EXTENSIONS = "extensions"

        val values = OperationType.values()

        fun fromJsonPair(rawJson: JSONArray, rawJsonResult: JSONArray): Operation{
            return fromJson(getOperationType(rawJson.optInt(0)), rawJson.optJSONObject(1), rawJsonResult)
        }

        fun fromJson(type: OperationType, rawJson: JSONObject, rawJsonResult: JSONArray): Operation {
            return when (type) {
                OperationType.TRANSFER_OPERATION                                    -> TransferOperation.fromJson(rawJson, rawJsonResult)
                OperationType.LIMIT_ORDER_CREATE_OPERATION                          -> LimitOrderCreateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.LIMIT_ORDER_CANCEL_OPERATION                          -> LimitOrderCancelOperation.fromJson(rawJson, rawJsonResult)
                OperationType.CALL_ORDER_UPDATE_OPERATION                           -> CallOrderUpdateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.FILL_ORDER_OPERATION                                  -> FillOrderOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ACCOUNT_CREATE_OPERATION                              -> AccountCreateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ACCOUNT_UPDATE_OPERATION                              -> AccountUpdateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ACCOUNT_WHITELIST_OPERATION                           -> AccountWhitelistOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ACCOUNT_UPGRADE_OPERATION                             -> AccountUpgradeOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ACCOUNT_TRANSFER_OPERATION                            -> AccountTransferOperation.fromJson(rawJson, rawJsonResult) // stops here
                OperationType.ASSET_CREATE_OPERATION                                -> AssetCreateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ASSET_UPDATE_OPERATION                                -> AssetUpdateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ASSET_UPDATE_BITASSET_OPERATION                       -> AssetUpdateBitassetOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ASSET_UPDATE_FEED_PRODUCERS_OPERATION                 -> AssetUpdateFeedProducersOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ASSET_ISSUE_OPERATION                                 -> AssetIssueOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ASSET_RESERVE_OPERATION                               -> AssetReserveOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ASSET_FUND_FEE_POOL_OPERATION                         -> AssetFundFeePoolOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ASSET_SETTLE_OPERATION                                -> AssetSettleOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ASSET_GLOBAL_SETTLE_OPERATION                         -> AssetGlobalSettleOperation.fromJson(rawJson, rawJsonResult) // START NEW OPT METHOD HERE
                OperationType.ASSET_PUBLISH_FEED_OPERATION                          -> AssetPublishFeedOperation.fromJson(rawJson, rawJsonResult)
                OperationType.WITNESS_CREATE_OPERATION                              -> WitnessCreateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.WITNESS_UPDATE_OPERATION                              -> WitnessUpdateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.PROPOSAL_CREATE_OPERATION                             -> ProposalCreateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.PROPOSAL_UPDATE_OPERATION                             -> ProposalUpdateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.PROPOSAL_DELETE_OPERATION                             -> ProposalDeleteOperation.fromJson(rawJson, rawJsonResult)
                OperationType.WITHDRAW_PERMISSION_CREATE_OPERATION                  -> WithdrawPermissionCreateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.WITHDRAW_PERMISSION_UPDATE_OPERATION                  -> WithdrawPermissionUpdateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.WITHDRAW_PERMISSION_CLAIM_OPERATION                   -> WithdrawPermissionClaimOperation.fromJson(rawJson, rawJsonResult)
                OperationType.WITHDRAW_PERMISSION_DELETE_OPERATION                  -> WithdrawPermissionDeleteOperation.fromJson(rawJson, rawJsonResult)
                OperationType.COMMITTEE_MEMBER_CREATE_OPERATION                     -> CommitteeMemberCreateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.COMMITTEE_MEMBER_UPDATE_OPERATION                     -> CommitteeMemberUpdateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.COMMITTEE_MEMBER_UPDATE_GLOBAL_PARAMETERS_OPERATION   -> CommitteeMemberUpdateGlobalParametersOperation.fromJson(rawJson, rawJsonResult)
                // resume here
                OperationType.VESTING_BALANCE_CREATE_OPERATION                      -> VestingBalanceCreateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.VESTING_BALANCE_WITHDRAW_OPERATION                    -> VestingBalanceWithdrawOperation.fromJson(rawJson, rawJsonResult)
                OperationType.WORKER_CREATE_OPERATION                               -> WorkerCreateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.CUSTOM_OPERATION                                      -> CustomOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ASSERT_OPERATION                                      -> AssertOperation.fromJson(rawJson, rawJsonResult)
                OperationType.BALANCE_CLAIM_OPERATION                               -> BalanceClaimOperation.fromJson(rawJson, rawJsonResult)
                OperationType.OVERRIDE_TRANSFER_OPERATION                           -> OverrideTransferOperation.fromJson(rawJson, rawJsonResult)
                OperationType.TRANSFER_TO_BLIND_OPERATION                           -> TransferToBlindOperation.fromJson(rawJson, rawJsonResult)
                OperationType.BLIND_TRANSFER_OPERATION                              -> BlindTransferOperation.fromJson(rawJson, rawJsonResult)
                OperationType.TRANSFER_FROM_BLIND_OPERATION                         -> TransferFromBlindOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ASSET_SETTLE_CANCEL_OPERATION                         -> AssetSettleCancelOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ASSET_CLAIM_FEES_OPERATION                            -> AssetClaimFeesOperation.fromJson(rawJson, rawJsonResult)
                OperationType.FBA_DISTRIBUTE_OPERATION                              -> FbaDistributeOperation.fromJson(rawJson, rawJsonResult)
                OperationType.BID_COLLATERAL_OPERATION                              -> BidCollateralOperation.fromJson(rawJson, rawJsonResult)
                OperationType.EXECUTE_BID_OPERATION                                 -> ExecuteBidOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ASSET_CLAIM_POOL_OPERATION                            -> AssetClaimPoolOperation.fromJson(rawJson, rawJsonResult)
                OperationType.ASSET_UPDATE_ISSUER_OPERATION                         -> AssetUpdateIssuerOperation.fromJson(rawJson, rawJsonResult)
                OperationType.HTLC_CREATE_OPERATION                                 -> HtlcCreateOperation.fromJson(rawJson, rawJsonResult)
                OperationType.HTLC_REDEEM_OPERATION                                 -> HtlcRedeemOperation.fromJson(rawJson, rawJsonResult)
                OperationType.HTLC_REDEEMED_OPERATION                               -> HtlcRedeemedOperation.fromJson(rawJson, rawJsonResult)
                OperationType.HTLC_EXTEND_OPERATION                                 -> HtlcExtendOperation.fromJson(rawJson, rawJsonResult)
                OperationType.HTLC_REFUND_OPERATION                                 -> HtlcRefundOperation.fromJson(rawJson, rawJsonResult)
                OperationType.UNDEFINED_OPERATION                                   -> EmptyOperation.fromJson(rawJson, rawJsonResult)
            }.apply {
                rawJsonTemp = rawJson
            }
        }
        // FIXME: 2021/9/30      java.lang.ArrayIndexOutOfBoundsException: length=54; index=57
        private fun getOperationType(type: Int): OperationType {
            return values.getOrElse(type) { OperationType.UNDEFINED_OPERATION }
        }

        val EMPTY get() = object : Operation() {
            override val operationType: OperationType = OperationType.UNDEFINED_OPERATION
            override fun toJsonElement(): Any? = null
            override fun toByteArray(): ByteArray = byteArrayOf()
        }

    }

    var rawJsonTemp = JSONObject()

    abstract val operationType: OperationType

    override val ordinal: Int get() = operationType.ordinal

    open val authority: Authority = Authority.OWNER

    var fee: AssetAmount = AssetAmount(0L, AssetObject.CORE_ASSET)
//    val extensions = emptySet<Extensions>()
    protected val extensions: TreeSet<Any> = sortedSetOf(grapheneGlobalComparator)

    var createTime: Date = Date.from(Instant.EPOCH)
    var isVirtual: Boolean = false
    var blockHeight = ChainConfig.EMPTY_INSTANCE

}

