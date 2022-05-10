package com.bitshares.oases.ui.main.explore

import androidx.fragment.app.activityViewModels
import bitshareskit.models.*
import com.bitshares.oases.chain.formatCoreAssetAmount
import com.bitshares.oases.chain.formatCoreAssetBalance
import com.bitshares.oases.extensions.text.appendAssetAmountSpan
import com.bitshares.oases.extensions.text.appendOperationNameSpan
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account.picker.AccountPickerViewModel
import com.bitshares.oases.ui.account.voting.VotingViewModel
import com.bitshares.oases.ui.asset.picker.AssetPickerViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.main.MainViewModel
import modulon.component.cell.ComponentCell
import modulon.extensions.charset.BLANK_SPACE
import modulon.extensions.text.buildContextSpannedString
import modulon.extensions.view.doOnClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.layout.lazy.*

class ExploreFragment_FeeSchedule : ContainerFragment() {

    private val exploreViewModel: ExploreViewModel by activityViewModels()
    private val votingViewModel: VotingViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val accountSearchingViewModel: AccountPickerViewModel by activityViewModels()
    private val assetSearchingViewModel: AssetPickerViewModel by activityViewModels()

    override fun onCreateView() {
        setupRecycler {
            section {
                header = "Fee Paramaters"
                list<ComponentCell, FeeParameters> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        title = buildContextSpannedString {
                            doOnClick {  }
                            appendOperationNameSpan(it.type)
                        }
                    }
                    payload { it, payload ->
                        fun appendAmount(amount: ULong) = buildContextSpannedString {
                            payload as Boolean
                            append("Regular Transaction", BLANK_SPACE)
                            appendAssetAmountSpan(formatCoreAssetAmount(amount))
//                                                        appendAssetAmountSpan(context, formatCoreAssetAmount(amount), context.getColor(R.color.component))
                        }
                        subtext = when (it) {
                            is TransferOperationFeeParameters -> appendAmount(it.fee)
                            is LimitOrderCreateOperationFeeParameters -> appendAmount(it.fee)
                            is LimitOrderCancelOperationFeeParameters -> appendAmount(it.fee)
                            is CallOrderUpdateOperationFeeParameters -> appendAmount(it.fee)
                            is FillOrderOperationFeeParameters -> "Free of Charge"
                            is AccountCreateOperationFeeParameters -> formatCoreAssetBalance(it.basicFee) // TODO: 2021/1/15
                            is AccountUpdateOperationFeeParameters -> appendAmount(it.fee)
                            is AccountWhitelistOperationFeeParameters -> appendAmount(it.fee)
                            is AccountUpgradeOperationFeeParameters -> formatCoreAssetBalance(it.membershipLifetimeFee) // TODO: 2021/1/15
                            is AccountTransferOperationFeeParameters -> appendAmount(it.fee)
                            is AssetCreateOperationFeeParameters -> formatCoreAssetBalance(it.longSymbol) // TODO: 2021/1/15
                            is AssetUpdateOperationFeeParameters -> appendAmount(it.fee)
                            is AssetUpdateBitassetOperationFeeParameters -> appendAmount(it.fee)
                            is AssetUpdateFeedProducersOperationFeeParameters -> appendAmount(it.fee)
                            is AssetIssueOperationFeeParameters -> appendAmount(it.fee)
                            is AssetReserveOperationFeeParameters -> appendAmount(it.fee)
                            is AssetFundFeePoolOperationFeeParameters -> appendAmount(it.fee)
                            is AssetSettleOperationFeeParameters -> appendAmount(it.fee)
                            is AssetGlobalSettleOperationFeeParameters -> appendAmount(it.fee)
                            is AssetPublishFeedOperationFeeParameters -> appendAmount(it.fee)
                            is WitnessCreateOperationFeeParameters -> appendAmount(it.fee)
                            is WitnessUpdateOperationFeeParameters -> appendAmount(it.fee)
                            is ProposalCreateOperationFeeParameters -> appendAmount(it.fee)
                            is ProposalUpdateOperationFeeParameters -> appendAmount(it.fee)
                            is ProposalDeleteOperationFeeParameters -> appendAmount(it.fee)
                            is WithdrawPermissionCreateOperationFeeParameters -> appendAmount(it.fee)
                            is WithdrawPermissionUpdateOperationFeeParameters -> appendAmount(it.fee)
                            is WithdrawPermissionClaimOperationFeeParameters -> appendAmount(it.fee)
                            is WithdrawPermissionDeleteOperationFeeParameters -> appendAmount(it.fee)
                            is CommitteeMemberCreateOperationFeeParameters -> appendAmount(it.fee)
                            is CommitteeMemberUpdateOperationFeeParameters -> appendAmount(it.fee)
                            is CommitteeMemberUpdateGlobalParametersOperationFeeParameters -> appendAmount(it.fee)
                            is VestingBalanceCreateOperationFeeParameters -> appendAmount(it.fee)
                            is VestingBalanceWithdrawOperationFeeParameters -> appendAmount(it.fee)
                            is WorkerCreateOperationFeeParameters -> appendAmount(it.fee)
                            is CustomOperationFeeParameters -> appendAmount(it.fee)
                            is AssertOperationFeeParameters -> appendAmount(it.fee)
                            is BalanceClaimOperationFeeParameters -> ""  // TODO: 2021/1/15
                            is OverrideTransferOperationFeeParameters -> appendAmount(it.fee)
                            is TransferToBlindOperationFeeParameters -> appendAmount(it.fee)
                            is BlindTransferOperationFeeParameters -> appendAmount(it.fee)
                            is TransferFromBlindOperationFeeParameters -> appendAmount(it.fee)
                            is AssetSettleCancelOperationFeeParameters -> ""  // TODO: 2021/1/15
                            is AssetClaimFeesOperationFeeParameters -> appendAmount(it.fee)
                            is FbaDistributeOperationFeeParameters -> ""  // TODO: 2021/1/15
                            is BidCollateralOperationFeeParameters -> appendAmount(it.fee)
                            is ExecuteBidOperationFeeParameters -> ""  // TODO: 2021/1/15
                            is AssetClaimPoolOperationFeeParameters -> appendAmount(it.fee)
                            is AssetUpdateIssuerOperationFeeParameters -> appendAmount(it.fee)
                            is HtlcCreateOperationFeeParameters -> appendAmount(it.fee)
                            is HtlcRedeemOperationFeeParameters -> appendAmount(it.fee)
                            is HtlcRedeemedOperationFeeParameters -> ""  // TODO: 2021/1/15
                            is HtlcExtendOperationFeeParameters -> appendAmount(it.fee)
                            is HtlcRefundOperationFeeParameters -> ""  // TODO: 2021/1/15
                            else -> ""
                        }
                    }
                    exploreViewModel.feeParameters.observe { submitList(it) }
                }
            }
            logo()
        }
    }
}