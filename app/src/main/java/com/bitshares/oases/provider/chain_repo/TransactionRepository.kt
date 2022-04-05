package com.bitshares.oases.provider.chain_repo

import bitshareskit.extensions.logcat
import bitshareskit.models.Transaction
import bitshareskit.models.TransactionBlock
import bitshareskit.operations.*
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import bitshareskit.chain.CallMethod
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import org.java_json.JSONArray
import org.java_json.JSONObject

object TransactionRepository {

    suspend fun broadcastTransactionWithCallback(tx: Transaction): Flow<Any?> {
        return NetworkService.sendSubscribe(CallMethod.BROADCAST_TRANSACTION_WITH_CALLBACK, listOf(tx.toJsonElement())) {
            logcat("broadcastTransactionWithCallback $it")
            when (it) {
                is JSONArray -> runCatching { TransactionBlock(it[0] as JSONObject) }.getOrNull()
                else -> it
            }
        }
    }

    @Suppress("IMPLICIT_CAST_TO_ANY")
    suspend fun getOperationDetail(op: Operation): Operation {
        coroutineScope {
            (when (op) {
                is TransferOperation -> listOf(
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) },
                    async { op.from = AccountRepository.getAccountDetail(op.from) },
                    async { op.to = AccountRepository.getAccountDetail(op.to) }
                )
                is LimitOrderCreateOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) },
                    async { op.sells = AssetRepository.getAssetAmountDetail(op.sells) },
                    async { op.receives = AssetRepository.getAssetAmountDetail(op.receives) }
                )
                is LimitOrderCancelOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) }
//                        async { op.order = AssetRepository.getFullAssetAmount(op.amountToSell) },
                )
                is CallOrderUpdateOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) },
                    async { op.deltaDebt = AssetRepository.getAssetAmountDetail(op.deltaDebt) },
                    async { op.deltaCollateral = AssetRepository.getAssetAmountDetail(op.deltaCollateral) }
                )
                is FillOrderOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) },
                    async { op.pays = AssetRepository.getAssetAmountDetail(op.pays) },
                    async { op.receives = AssetRepository.getAssetAmountDetail(op.receives) },
                    async { op.price = AssetRepository.getPriceDetail(op.price) }
                )
                is AccountCreateOperation -> listOf(
                    async { op.registrar = AccountRepository.getAccountDetail(op.registrar) },
                    async { op.result = AccountRepository.getAccountDetail(op.result) }
                )
                is AccountUpdateOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) }
                )
                is AccountWhitelistOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) },
                    async { op.accountToList = AccountRepository.getAccountDetail(op.accountToList) }
                )
                is AccountUpgradeOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) }
                )
                is AccountTransferOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) },
                    async { op.newOwner = AccountRepository.getAccountDetail(op.newOwner) }
                )
                is AssetCreateOperation -> listOf(
                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
                    async { op.result = AssetRepository.getAssetDetail(op.result) }
                )
                is AssetUpdateOperation -> listOf(
                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
                    async { op.asset = AssetRepository.getAssetDetail(op.asset) }
                )
                is AssetUpdateBitassetOperation -> listOf(
                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
                    async { op.asset = AssetRepository.getAssetDetail(op.asset) }
                )
                is AssetUpdateFeedProducersOperation -> listOf(
                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
                    async { op.asset = AssetRepository.getAssetDetail(op.asset) }
                )
                is AssetIssueOperation -> listOf(
                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) },
                    async { op.issueTo = AccountRepository.getAccountDetail(op.issueTo) }
                )
                is AssetReserveOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) },
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) }
                )
                is AssetFundFeePoolOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) },
                    async { op.asset = AssetRepository.getAssetDetail(op.asset) }
                )
                is AssetSettleOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) },
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) }
                )
                is AssetGlobalSettleOperation -> listOf(
                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
                    async { op.asset = AssetRepository.getAssetDetail(op.asset) },
                    async { op.price = AssetRepository.getPriceDetail(op.price) }
                )
                is AssetPublishFeedOperation -> listOf(
                    async { op.publisher = AccountRepository.getAccountDetail(op.publisher) },
                    async { op.asset = AssetRepository.getAssetDetail(op.asset) },
                    async { AssetRepository.getPriceDetail(op.feed.settlementPrice) }
                )
                is WitnessCreateOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) }
                )
                is WitnessUpdateOperation -> listOf(
                    async { op.witness = WitnessRepository.getWitnessDetail(op.witness) },
                    async { op.account = AccountRepository.getAccountDetail(op.account) }
                )
                is ProposalCreateOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) },
                    async { op.operations.onEach { getOperationDetail(it) } }
                )
                is ProposalUpdateOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) }
                )
                is ProposalDeleteOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) }
                )
                is WithdrawPermissionCreateOperation -> listOf(
                    async { op.from = AccountRepository.getAccountDetail(op.from) },
                    async { op.authorized = AccountRepository.getAccountDetail(op.authorized) },
                    async { op.limit = AssetRepository.getAssetAmountDetail(op.limit) }
                )
                is WithdrawPermissionUpdateOperation -> listOf(
                    async { op.from = AccountRepository.getAccountDetail(op.from) },
                    async { op.to = AccountRepository.getAccountDetail(op.to) },
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) }
                )
                is WithdrawPermissionClaimOperation -> listOf(
                    async { op.from = AccountRepository.getAccountDetail(op.from) },
                    async { op.authorized = AccountRepository.getAccountDetail(op.authorized) },
                    async { op.limit = AssetRepository.getAssetAmountDetail(op.limit) }
                )
                is WithdrawPermissionDeleteOperation -> listOf(
                    async { op.from = AccountRepository.getAccountDetail(op.from) },
                    async { op.authorized = AccountRepository.getAccountDetail(op.authorized) }
                )
                is CommitteeMemberCreateOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) }
                )
                is CommitteeMemberUpdateOperation -> listOf(
                    async { op.committee = CommitteeMemberRepository.getCommitteeDetail(op.committee) },
                    async { op.account = AccountRepository.getAccountDetail(op.account) }
                )
                is CommitteeMemberUpdateGlobalParametersOperation -> listOf(
                    async { op.committee = AccountRepository.getAccountDetail(op.committee) }
                )
                is VestingBalanceCreateOperation -> listOf(
                    async { op.creator = AccountRepository.getAccountDetail(op.creator) },
                    async { op.owner = AccountRepository.getAccountDetail(op.owner) },
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) }
                )
                is VestingBalanceWithdrawOperation -> listOf(
                    async { op.owner = AccountRepository.getAccountDetail(op.owner) },
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) }
                )
                is WorkerCreateOperation -> listOf(
                    async { op.owner = AccountRepository.getAccountDetail(op.owner) }
                )
                is CustomOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) }
                )
                is AssertOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) }
                )
                is BalanceClaimOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) }
                )
                is OverrideTransferOperation -> listOf(
                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
                    async { op.from = AccountRepository.getAccountDetail(op.from) },
                    async { op.to = AccountRepository.getAccountDetail(op.to) },
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) }
                )
                is TransferToBlindOperation -> listOf(
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) },
                    async { op.from = AccountRepository.getAccountDetail(op.from) }
                )
                is BlindTransferOperation -> listOf()
                is TransferFromBlindOperation -> listOf(
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) },
                    async { op.to = AccountRepository.getAccountDetail(op.to) }
                )
                is AssetSettleCancelOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) },
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) }
                )
                is AssetClaimFeesOperation -> listOf(
                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) }
                )
                is FbaDistributeOperation -> listOf(
                    async { op.account = AccountRepository.getAccountDetail(op.account) }
                )
                is BidCollateralOperation -> listOf(
                    async { op.bidder = AccountRepository.getAccountDetail(op.bidder) },
                    async { op.additionalCollateral = AssetRepository.getAssetAmountDetail(op.additionalCollateral) },
                    async { op.debtCovered = AssetRepository.getAssetAmountDetail(op.debtCovered) }
                )
                is ExecuteBidOperation -> listOf(
                    async { op.bidder = AccountRepository.getAccountDetail(op.bidder) },
                    async { op.debt = AssetRepository.getAssetAmountDetail(op.debt) },
                    async { op.collateral = AssetRepository.getAssetAmountDetail(op.collateral) })
                is AssetClaimPoolOperation -> listOf(
                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
                    async { op.asset = AssetRepository.getAssetDetail(op.asset) },
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) }
                )
                is AssetUpdateIssuerOperation -> listOf(
                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
                    async { op.asset = AssetRepository.getAssetDetail(op.asset) },
                    async { op.newIssuer = AccountRepository.getAccountDetail(op.newIssuer) }
                )
                is HtlcCreateOperation -> listOf(
                    async { op.from = AccountRepository.getAccountDetail(op.from) },
                    async { op.to = AccountRepository.getAccountDetail(op.to) },
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) }
                )
                is HtlcRedeemOperation -> listOf(
                    async { op.redeemer = AccountRepository.getAccountDetail(op.redeemer) }
                )
                is HtlcRedeemedOperation -> listOf(
                    async { op.from = AccountRepository.getAccountDetail(op.from) },
                    async { op.to = AccountRepository.getAccountDetail(op.to) },
                    async { op.amount = AssetRepository.getAssetAmountDetail(op.amount) }
                )
                is HtlcExtendOperation -> listOf(
                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) }
                )
                is HtlcRefundOperation -> listOf(
                    async { op.to = AccountRepository.getAccountDetail(op.to) }
                )
                else -> listOf()
            } + async { op.fee = AssetRepository.getAssetAmountDetail(op.fee) }).awaitAll()


            // remove here
//            when(op) {
//                is TransferOperation -> {
//                    val from = async { AccountRepository.getAccountDetail(op.from) }
//
//
//                    op.copy(
//                        from.await(),
//                        async { AccountRepository.getAccountDetail(op.to) }.await(),
//                        async { AssetRepository.getAssetAmountDetail(op.buyAmount) }.await()
//                    )
//
//                }
//                is LimitOrderCreateOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) },
//                    async { op.sells = AssetRepository.getAssetAmountDetail(op.sells) },
//                    async { op.totalReceives = AssetRepository.getAssetAmountDetail(op.totalReceives) }
//                )
//                is LimitOrderCancelOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) }
////                        async { op.order = AssetRepository.getFullAssetAmount(op.amountToSell) },
//                )
//                is CallOrderUpdateOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) },
//                    async { op.debt = AssetRepository.getAssetAmountDetail(op.debt) },
//                    async { op.collateral = AssetRepository.getAssetAmountDetail(op.collateral) }
//                )
//                is FillOrderOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) },
//                    async { op.pays = AssetRepository.getAssetAmountDetail(op.pays) },
//                    async { op.totalReceives = AssetRepository.getAssetAmountDetail(op.totalReceives) },
//                    async { op.price = AssetRepository.getPriceDetail(op.price) }
//                )
//                is AccountCreateOperation -> listOf(
//                    async { op.registrar = AccountRepository.getAccountDetail(op.registrar) },
//                    async { op.result = AccountRepository.getAccountDetail(op.result) }
//                )
//                is AccountUpdateOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) }
//                )
//                is AccountWhitelistOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) },
//                    async { op.accountToList = AccountRepository.getAccountDetail(op.accountToList) }
//                )
//                is AccountUpgradeOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) }
//                )
//                is AccountTransferOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) },
//                    async { op.newOwner = AccountRepository.getAccountDetail(op.newOwner) }
//                )
//                is AssetCreateOperation -> listOf(
//                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
//                    async { op.result = AssetRepository.getAssetDetail(op.result) }
//                )
//                is AssetUpdateOperation -> listOf(
//                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
//                    async { op.asset = AssetRepository.getAssetDetail(op.asset) }
//                )
//                is AssetUpdateBitassetOperation -> listOf(
//                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
//                    async { op.asset = AssetRepository.getAssetDetail(op.asset) }
//                )
//                is AssetUpdateFeedProducersOperation -> listOf(
//                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
//                    async { op.asset = AssetRepository.getAssetDetail(op.asset) }
//                )
//                is AssetIssueOperation -> listOf(
//                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) },
//                    async { op.issueTo = AccountRepository.getAccountDetail(op.issueTo) }
//                )
//                is AssetReserveOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) },
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) }
//                )
//                is AssetFundFeePoolOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) },
//                    async { op.asset = AssetRepository.getAssetDetail(op.asset) }
//                )
//                is AssetSettleOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) },
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) }
//                )
//                is AssetGlobalSettleOperation -> listOf(
//                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
//                    async { op.asset = AssetRepository.getAssetDetail(op.asset) },
//                    async { op.price = AssetRepository.getPriceDetail(op.price) }
//                )
//                is AssetPublishFeedOperation -> listOf(
//                    async { op.publisher = AccountRepository.getAccountDetail(op.publisher) },
//                    async { op.asset = AssetRepository.getAssetDetail(op.asset) }
//                    // TODO: getFullFeedPrice
//                )
//                is WitnessCreateOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) }
//                )
//                is WitnessUpdateOperation -> listOf(
//                    async { op.witness = WitnessRepository.getWitnessDetail(op.witness) },
//                    async { op.account = AccountRepository.getAccountDetail(op.account) }
//                )
//                is ProposalCreateOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) },
//                    async { op.operations.onEach { getOperationDetail(it) } }
//                )
//                is ProposalUpdateOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) }
//                )
//                is ProposalDeleteOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) }
//                )
//                is WithdrawPermissionCreateOperation -> listOf(
//                    async { op.from = AccountRepository.getAccountDetail(op.from) },
//                    async { op.authorized = AccountRepository.getAccountDetail(op.authorized) },
//                    async { op.limit = AssetRepository.getAssetAmountDetail(op.limit) }
//                )
//                is WithdrawPermissionUpdateOperation -> listOf(
//                    async { op.from = AccountRepository.getAccountDetail(op.from) },
//                    async { op.to = AccountRepository.getAccountDetail(op.to) },
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) }
//                )
//                is WithdrawPermissionClaimOperation -> listOf(
//                    async { op.from = AccountRepository.getAccountDetail(op.from) },
//                    async { op.authorized = AccountRepository.getAccountDetail(op.authorized) },
//                    async { op.limit = AssetRepository.getAssetAmountDetail(op.limit) }
//                )
//                is WithdrawPermissionDeleteOperation -> listOf(
//                    async { op.from = AccountRepository.getAccountDetail(op.from) },
//                    async { op.authorized = AccountRepository.getAccountDetail(op.authorized) }
//                )
//                is CommitteeMemberCreateOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) }
//                )
//                is CommitteeMemberUpdateOperation -> listOf(
//                    async { op.committee = CommitteeMemberRepository.getCommitteeDetail(op.committee) },
//                    async { op.account = AccountRepository.getAccountDetail(op.account) }
//                )
//                is CommitteeMemberUpdateGlobalParametersOperation -> listOf()
//                is VestingBalanceCreateOperation -> listOf(
//                    async { op.creator = AccountRepository.getAccountDetail(op.creator) },
//                    async { op.owner = AccountRepository.getAccountDetail(op.owner) },
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) }
//                )
//                is VestingBalanceWithdrawOperation -> listOf(
//                    async { op.owner = AccountRepository.getAccountDetail(op.owner) },
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) }
//                )
//                is WorkerCreateOperation -> listOf(
//                    async { op.owner = AccountRepository.getAccountDetail(op.owner) }
//                )
//                is CustomOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) }
//                )
//                is AssertOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) }
//                )
//                is BalanceClaimOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) }
//                )
//                is OverrideTransferOperation -> listOf(
//                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
//                    async { op.from = AccountRepository.getAccountDetail(op.from) },
//                    async { op.to = AccountRepository.getAccountDetail(op.to) },
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) }
//                )
//                is TransferToBlindOperation -> listOf(
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) },
//                    async { op.from = AccountRepository.getAccountDetail(op.from) }
//                )
//                is BlindTransferOperation -> listOf()
//                is TransferFromBlindOperation -> listOf(
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) },
//                    async { op.to = AccountRepository.getAccountDetail(op.to) }
//                )
//                is AssetSettleCancelOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) },
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) }
//                )
//                is AssetClaimFeesOperation -> listOf(
//                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) }
//                )
//                is FbaDistributeOperation -> listOf(
//                    async { op.account = AccountRepository.getAccountDetail(op.account) }
//                )
//                is BidCollateralOperation -> listOf(
//                    async { op.bidder = AccountRepository.getAccountDetail(op.bidder) },
//                    async { op.additionalCollateral = AssetRepository.getAssetAmountDetail(op.additionalCollateral) },
//                    async { op.debtCovered = AssetRepository.getAssetAmountDetail(op.debtCovered) }
//                )
//                is ExecuteBidOperation -> listOf(
//                    async { op.bidder = AccountRepository.getAccountDetail(op.bidder) },
//                    async { op.debt = AssetRepository.getAssetAmountDetail(op.debt) },
//                    async { op.collateral = AssetRepository.getAssetAmountDetail(op.collateral) })
//                is AssetClaimPoolOperation -> listOf(
//                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
//                    async { op.asset = AssetRepository.getAssetDetail(op.asset) },
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) }
//                )
//                is AssetUpdateIssuerOperation -> listOf(
//                    async { op.issuer = AccountRepository.getAccountDetail(op.issuer) },
//                    async { op.asset = AssetRepository.getAssetDetail(op.asset) },
//                    async { op.newIssuer = AccountRepository.getAccountDetail(op.newIssuer) }
//                )
//                is HtlcCreateOperation -> listOf(
//                    async { op.from = AccountRepository.getAccountDetail(op.from) },
//                    async { op.to = AccountRepository.getAccountDetail(op.to) },
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) }
//                )
//                is HtlcRedeemOperation -> listOf(
//                    async { op.redeemer = AccountRepository.getAccountDetail(op.redeemer) }
//                )
//                is HtlcRedeemedOperation -> listOf(
//                    async { op.from = AccountRepository.getAccountDetail(op.from) },
//                    async { op.to = AccountRepository.getAccountDetail(op.to) },
//                    async { op.buyAmount = AssetRepository.getAssetAmountDetail(op.buyAmount) }
//                )
//                is HtlcExtendOperation -> listOf(
//                    async { op.updateIssuer = AccountRepository.getAccountDetail(op.updateIssuer) }
//                )
//                is HtlcRefundOperation -> listOf(
//                    async { op.to = AccountRepository.getAccountDetail(op.to) }
//                )
//                else -> listOf(
//                    async {  }
//                )
//            }

        }


        return op
    }


}
