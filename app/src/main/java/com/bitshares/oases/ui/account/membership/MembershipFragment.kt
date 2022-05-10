package com.bitshares.oases.ui.account.membership

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.map
import bitshareskit.extensions.formatGraphenePercentage
import bitshareskit.extensions.nameOrId
import com.bitshares.oases.R
import com.bitshares.oases.chain.formatCoreAssetBalance
import com.bitshares.oases.extensions.compat.showAccountBrowserDialog
import com.bitshares.oases.extensions.compat.startAccountBrowser
import com.bitshares.oases.extensions.text.createAccountSpan
import com.bitshares.oases.extensions.viewbinder.bindAccountV3
import com.bitshares.oases.extensions.viewbinder.feeCell
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.transaction.bindTransaction
import modulon.component.cell.IconSize
import modulon.component.cell.buttonStyle
import modulon.dialog.section
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.view.updatePaddingVerticalV6
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.hint
import modulon.layout.lazy.section

class MembershipFragment : ContainerFragment() {

    private val viewModel: MembershipViewModel by activityViewModels()

    override fun onCreateView() {
        setupAction {
            titleConnectionState("Membership")
        }
        setupRecycler {
            section {
                cell {
                    viewModel.accountNonNull.observe(viewLifecycleOwner) {
                        bindAccountV3(it, true, IconSize.COMPONENT_1)
                        doOnLongClick {
                            showAccountBrowserDialog(it)
                        }
                    }
                }
                cell {
                    buttonStyle()
                    // FIXME: 1/2/2022 no auto update
                    title = "Buy Lifetime Subscription"
                    isVisible = false
        //                        viewModel.accountStaticById.observe(viewLifecycleOwner) {
                    viewModel.accountNonNull.observe(viewLifecycleOwner) {
                        isVisible = !it.isLifetimeMember
                    }
                    doOnClick { showLifetimeSubscriptionDialog() }
                }
            }
            hint {
                isVisible = false
                viewModel.accountStaticById.observe(viewLifecycleOwner) {
                    isVisible = true
                    text = if (it.isLifetimeMember) "As a lifetime member (LTM) you are eligable for a 60% cashback on all your fees. " +
                            "You will also recieve cashback from markets with a referral reward. The referral reward is based on your own and your referred members market orders."
                    else "Every transaction on the BitShares network is divided between the network and referrers. " +
                            "By registering to a Lifetime Membership the account will receive 60% cashback on every transaction fee paid. " +
                            "As a bonus it will also qualify to earn referral income from users registered with or refered to the network. \n" +
                            "A Lifetime Membership price will change over time, right now it is only [LIFETIME_MEMBERSHIP_PRICE] BTS."
                }
            }
            section {
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.account_info_lifetime_fee_paid)
                    // TODO: 18/1/2022 extract "-"
                    subtitle = "-"
                    viewModel.accountStatistics.observe(viewLifecycleOwner) {
                        subtitle = formatCoreAssetBalance(it.lifetimeFeesPaid)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.account_info_pending_fee)
                    // TODO: 18/1/2022 extract "-"
                    subtitle = "-"
                    viewModel.accountStatistics.observe(viewLifecycleOwner) {
                        subtitle = formatCoreAssetBalance(it.pendingFees)
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.account_info_pending_vested_fee)
                    // TODO: 18/1/2022 extract "-"
                    subtitle = "-"
                    viewModel.accountStatistics.observe(viewLifecycleOwner) {
                        subtitle = formatCoreAssetBalance(it.pendingVestingFees)
                    }
                }
            }
            hint {
                text = "Fees paid by [ACCOUNT_NAME] are divided among the network, referrers, and registrars once every maintenance interval ([INTERVAL_SECONDS] seconds)." +
                        " The next maintenance time is [NEXT_MAINTENANCE_TIME]."
            }
            hint {
                text = "Most fees are made available immediately, but fees over 1 [CORE_ASSET_SYMBOL] (such as those paid to upgrade your membership or register a premium account name)" +
                        " must vest for a total of 1 days."
            }
            section {
                header = context.getString(R.string.account_info_fee_allocation)
                cell {
                    updatePaddingVerticalHalf()
                    isVisible = false
                    title = context.getString(R.string.tag_registrar)
                    combineNonNull(viewModel.registrar, viewModel.registerFeePercentage).observe(viewLifecycleOwner) { (account,percentage) ->
                        isVisible = true
                        subtitle = "${account.nameOrId} (${formatGraphenePercentage(percentage, 100)})"
                        doOnClick { startAccountBrowser(account.uid) }
                        doOnLongClick { showAccountBrowserDialog(account) }
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    isVisible = false
                    title = context.getString(R.string.tag_referrer)
                    combineNonNull(viewModel.referrer, viewModel.referrerFeePercentage).observe(viewLifecycleOwner) { (account,percentage) ->
                        isVisible = true
                        subtitle = "${account.nameOrId} (${formatGraphenePercentage(percentage, 100)})"
                        doOnClick { startAccountBrowser(account.uid) }
                        doOnLongClick { showAccountBrowserDialog(account) }
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    isVisible = false
                    title = context.getString(R.string.tag_lifetime_referrer)
                    combineNonNull(viewModel.lifetimeReferrer, viewModel.lifetimeReferrerFeePercentage).observe(viewLifecycleOwner) { (account,percentage) ->
                        isVisible = true
                        subtitle = "${account.nameOrId} (${formatGraphenePercentage(percentage, 100)})"
                        doOnClick { startAccountBrowser(account.uid) }
                        doOnLongClick { showAccountBrowserDialog(account) }
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    isVisible = false
                    title = context.getString(R.string.tag_network)
                    combineNonNull(viewModel.network, viewModel.networkFeePercentage).observe(viewLifecycleOwner) { (account,percentage) ->
                        isVisible = true
                        subtitle = "${account.nameOrId} (${formatGraphenePercentage(percentage, 100)})"
                        doOnClick { startAccountBrowser(account.uid) }
                        doOnLongClick { showAccountBrowserDialog(account) }
                    }
                }
            }
            logo()

        }
    }

    private fun showLifetimeSubscriptionDialog() = showBottomDialog {
        bindTransaction(viewModel.buildTransaction(), viewModel)
        section {
            cell {
                updatePaddingVerticalV6()
                title = context.getString(R.string.transaction_creator)
                viewModel.operation.map { it.account }.observe(viewLifecycleOwner) {
                    subtitle = createAccountSpan(it)
                }
            }
            cell {
                updatePaddingVerticalV6()
                title = "Upgrade to Lifetime Member"
                viewModel.operation.map { it.isLifetime }.observe(viewLifecycleOwner) {
                    subtitle = if (it) "Yes" else "No"
                }
            }
            feeCell(union, viewModel.transactionBuilder)
        }
    }

}