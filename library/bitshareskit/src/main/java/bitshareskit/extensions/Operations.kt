package bitshareskit.extensions

import bitshareskit.objects.GrapheneObject
import bitshareskit.operations.*

fun extractOperationComponents(op: Operation): Set<Any> {
    return when(op) {
        is TransferOperation -> setOf(op.from, op.to, op.amount.asset)
        is LimitOrderCreateOperation -> setOf(op.account, op.sells.asset, op.receives.asset)
        is LimitOrderCancelOperation -> setOf(op.account, op.order)
        is CallOrderUpdateOperation -> setOf(op.account, op.deltaDebt.asset, op.deltaCollateral.asset)
        is FillOrderOperation -> setOf(op.account, op.pays.asset, op.receives.asset)
        is AccountCreateOperation -> setOf(op.registrar, op.result)
        is AccountUpdateOperation -> setOf(op.account)
        is AccountWhitelistOperation -> setOf(op.account, op.accountToList)
        is AccountUpgradeOperation -> setOf(op.account)
        is AccountTransferOperation -> setOf(op.account, op.newOwner)
        is AssetCreateOperation -> setOf(op.issuer, op.result)
        is AssetUpdateOperation -> setOf(op.issuer, op.asset)
        is AssetUpdateBitassetOperation -> setOf(op.issuer, op.asset)
        is AssetUpdateFeedProducersOperation -> setOf(op.issuer, op.asset)
        is AssetIssueOperation -> setOf(op.issuer, op.issueTo, op.amount.asset)
        is AssetReserveOperation -> setOf(op.account, op.amount.asset)
        is AssetFundFeePoolOperation -> setOf(op.account, op.asset) // core?
        is AssetSettleOperation -> setOf(op.account, op.amount.asset)
        is AssetGlobalSettleOperation -> setOf(op.issuer, op.asset, op.price.base.asset, op.price.quote.asset)
        is AssetPublishFeedOperation -> setOf(op.publisher, op.asset) //
        is WitnessCreateOperation -> setOf(op.account)
        is WitnessUpdateOperation -> setOf(op.account)
        is ProposalCreateOperation -> setOf(op.account, op.result) // proposal
        is ProposalUpdateOperation -> setOf(op.account, op.proposal) // proposal
        is ProposalDeleteOperation -> setOf(op.account, op.proposal) // proposal
        is WithdrawPermissionCreateOperation -> setOf(op.from, op.authorized)
        is WithdrawPermissionUpdateOperation -> setOf(op.from, op.to)
        is WithdrawPermissionClaimOperation -> setOf(op.from, op.authorized)
        is WithdrawPermissionDeleteOperation -> setOf(op.from, op.authorized)
        is CommitteeMemberCreateOperation -> setOf(op.account)
        is CommitteeMemberUpdateOperation -> setOf(op.committee)
        is CommitteeMemberUpdateGlobalParametersOperation -> setOf(op.committee)
        is VestingBalanceCreateOperation -> setOf(op.creator, op.owner, op.amount.asset)
        is VestingBalanceWithdrawOperation -> setOf(op.owner, op.amount.asset)
        is WorkerCreateOperation -> setOf(op.owner, op.result) // core
        is CustomOperation -> setOf(op.account)
        is AssertOperation -> setOf()
        is BalanceClaimOperation -> setOf(op.account, op.totalClaimed.asset)
        is OverrideTransferOperation -> setOf(op.issuer, op.from, op.to, op.amount.asset)
        is TransferToBlindOperation -> setOf(op.from, op.amount.asset)
        is BlindTransferOperation -> setOf()
        is TransferFromBlindOperation -> setOf(op.to, op.amount.asset)
        is AssetSettleCancelOperation -> setOf(op.account, op.amount.asset)
        is AssetClaimFeesOperation -> setOf(op.issuer, op.amount.asset)
        is FbaDistributeOperation -> setOf()
        is BidCollateralOperation -> setOf(op.bidder, op.additionalCollateral.asset, op.debtCovered.asset)
        is ExecuteBidOperation -> setOf(op.bidder)
        is AssetClaimPoolOperation -> setOf(op.issuer, op.asset)
        is AssetUpdateIssuerOperation -> setOf(op.issuer, op.newIssuer, op.asset)
        is HtlcCreateOperation -> setOf(op.from, op.to, op.amount.asset, op.result)
        is HtlcRedeemOperation -> setOf(op.redeemer, op.htlc) // htlc
        is HtlcRedeemedOperation -> setOf(op.from, op.to, op.amount.asset, op.htlc) // htlc
        is HtlcExtendOperation -> setOf(op.issuer, op.htlc) // htlc
        is HtlcRefundOperation -> setOf(op.to, op.htlc)
        else -> setOf()
    }
}

fun extractOperationComponents1(op: Operation): Set<GrapheneObject> = when(op) {
    is TransferOperation -> setOf(op.from, op.to, op.amount.asset)
    is LimitOrderCreateOperation -> setOf(op.account, op.sells.asset, op.receives.asset)
    is LimitOrderCancelOperation -> setOf(op.account, op.order)
    is CallOrderUpdateOperation -> setOf(op.account, op.deltaDebt.asset, op.deltaCollateral.asset)
    is FillOrderOperation -> setOf(op.account, op.pays.asset, op.receives.asset)
    is AccountCreateOperation -> setOf(op.registrar, op.result)
    is AccountUpdateOperation -> setOf(op.account)
    is AccountWhitelistOperation -> setOf(op.account, op.accountToList)
    is AccountUpgradeOperation -> setOf(op.account)
    is AccountTransferOperation -> setOf(op.account, op.newOwner)
    is AssetCreateOperation -> setOf(op.issuer, op.result)
    is AssetUpdateOperation -> setOf(op.issuer, op.asset)
    is AssetUpdateBitassetOperation -> setOf(op.issuer, op.asset)
    is AssetUpdateFeedProducersOperation -> setOf(op.issuer, op.asset)
    is AssetIssueOperation -> setOf(op.issuer, op.issueTo, op.amount.asset)
    is AssetReserveOperation -> setOf(op.account, op.amount.asset)
    is AssetFundFeePoolOperation -> setOf(op.account, op.asset) // core?
    is AssetSettleOperation -> setOf(op.account, op.amount.asset)
    is AssetGlobalSettleOperation -> setOf(op.issuer, op.asset, op.price.base.asset, op.price.quote.asset)
    is AssetPublishFeedOperation -> setOf(op.publisher, op.asset) //
    is WitnessCreateOperation -> setOf(op.account)
    is WitnessUpdateOperation -> setOf(op.account)
    is ProposalCreateOperation -> setOf(op.account, op.result) // proposal
    is ProposalUpdateOperation -> setOf(op.account, op.proposal) // proposal
    is ProposalDeleteOperation -> setOf(op.account, op.proposal) // proposal
    is WithdrawPermissionCreateOperation -> setOf(op.from, op.authorized)
    is WithdrawPermissionUpdateOperation -> setOf(op.from, op.to)
    is WithdrawPermissionClaimOperation -> setOf(op.from, op.authorized)
    is WithdrawPermissionDeleteOperation -> setOf(op.from, op.authorized)
    is CommitteeMemberCreateOperation -> setOf(op.account)
    is CommitteeMemberUpdateOperation -> setOf(op.committee)
    is CommitteeMemberUpdateGlobalParametersOperation -> setOf(op.committee)
    is VestingBalanceCreateOperation -> setOf(op.creator, op.owner, op.amount.asset)
    is VestingBalanceWithdrawOperation -> setOf(op.owner, op.amount.asset)
    is WorkerCreateOperation -> setOf(op.owner, op.result) // core
    is CustomOperation -> setOf(op.account)
    is AssertOperation -> setOf()
    is BalanceClaimOperation -> setOf(op.account, op.totalClaimed.asset)
    is OverrideTransferOperation -> setOf(op.issuer, op.from, op.to, op.amount.asset)
    is TransferToBlindOperation -> setOf(op.from, op.amount.asset)
    is BlindTransferOperation -> setOf()
    is TransferFromBlindOperation -> setOf(op.to, op.amount.asset)
    is AssetSettleCancelOperation -> setOf(op.account, op.amount.asset)
    is AssetClaimFeesOperation -> setOf(op.issuer, op.amount.asset)
    is FbaDistributeOperation -> setOf()
    is BidCollateralOperation -> setOf(op.bidder, op.additionalCollateral.asset, op.debtCovered.asset)
    is ExecuteBidOperation -> setOf(op.bidder)
    is AssetClaimPoolOperation -> setOf(op.issuer, op.asset)
    is AssetUpdateIssuerOperation -> setOf(op.issuer, op.newIssuer, op.asset)
    is HtlcCreateOperation -> setOf(op.from, op.to, op.amount.asset, op.result)
    is HtlcRedeemOperation -> setOf(op.redeemer, op.htlc) // htlc
    is HtlcRedeemedOperation -> setOf(op.from, op.to, op.amount.asset, op.htlc) // htlc
    is HtlcExtendOperation -> setOf(op.issuer, op.htlc) // htlc
    is HtlcRefundOperation -> setOf(op.to, op.htlc)
    else -> setOf()
}