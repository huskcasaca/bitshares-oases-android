package com.bitshares.oases.ui.account_ktor.browser

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.chain.Clipboard.LABEL_ACCOUNT_NAME
import com.bitshares.oases.chain.Clipboard.LABEL_GRAPHENE_ID
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account_ktor.K_AccountViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.extensions.compat.setClipboardToast
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.layout.recycler.section

class K_AccountBrowserFragment_Info : ContainerFragment() {

    private val viewModel: K_AccountViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler {
            // account container
//            section {
//                cell {
//                    viewModel.accountStaticById.observe(viewLifecycleOwner) {
//                        bindAccountV3(it, true, IconSize.COMPONENT_1)
//                        doOnLongClick {
//                            showAccountBrowserDialog(it)
//                        }
//                    }
//                }
//            }
            // basic
            section {
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.account_info_name)
                    viewModel.account.observe(viewLifecycleOwner) {
                        subtitle = it?.name ?: ""
                        doOnLongClick { setClipboardToast(LABEL_ACCOUNT_NAME, subtitle.toString()) }
                    }
                }
                cell {
                    updatePaddingVerticalHalf()
                    title = context.getString(R.string.account_info_graphene_id)
//                    subtitleView.typeface = typefaceMonoRegular
                    viewModel.account.observe(viewLifecycleOwner) {
                        subtitle = it?.id.toStringOrEmpty()
                        doOnLongClick { setClipboardToast(LABEL_GRAPHENE_ID, subtitle.toString()) }
                    }
                }
//                cell {
//                    updatePaddingVerticalHalf()
//                    title = context.getString(R.string.account_info_member_group)
//                    viewModel.accountStaticById.observe(viewLifecycleOwner) {
//                        subtitle = context.getString(if (it.isLifetimeMember) R.string.tag_lifetime_member else R.string.tag_basic_member)
//                    }
//                }
            }
//            // committee member
//            section {
//                header = context.getString(R.string.account_info_committee_member_info)
//                cell {
//                    updatePaddingVerticalHalf()
//                    title = context.getString(R.string.account_info_total_votes)
//                    viewModel.accountCommitteeInfo.observe(viewLifecycleOwner) {
//                        subtitle = formatAssetBigDecimal(it.totalVotes, Graphene.KEY_CORE_ASSET.value).toLong().toString()
//                    }
//                }
//                cell {
//                    updatePaddingVerticalHalf()
//                    title = context.getString(R.string.account_info_url)
//                    subtitleView.startScrolling()
//                    isVisible = false
//                    viewModel.accountCommitteeInfo.observe(viewLifecycleOwner) {
//                        subtitle = it.url.toString()
//                        doOnClick {
//                            startUriBrowser(it.url)
//                        }
//                        isVisible = text.isNotBlank()
//                    }
//                }
//                isVisible = false
//                viewModel.accountCommitteeInfo.observe(viewLifecycleOwner) {
//                    isVisible = true
//                }
//            }
//            // witness
//            section {
//                header = context.getString(R.string.account_info_witness_info)
//                cell {
//                    updatePaddingVerticalHalf()
//                    title = context.getString(R.string.account_info_total_votes)
//                    viewModel.accountWitnessInfo.observe(viewLifecycleOwner) {
//                        subtitle = formatAssetBigDecimal(it.totalVotes, Graphene.KEY_CORE_ASSET.value).toLong().toString()
//                    }
//                }
//                cell {
//                    updatePaddingVerticalHalf()
//                    title = context.getString(R.string.account_info_last_confirmed_block)
//                    viewModel.accountWitnessInfo.observe(viewLifecycleOwner) {
//                        subtitle = it.lastConfirmedBlockNum.toString()
//                    }
//                }
//                cell {
//                    updatePaddingVerticalHalf()
//                    title = context.getString(R.string.account_info_total_missed)
//                    viewModel.accountWitnessInfo.observe(viewLifecycleOwner) {
//                        subtitle = it.totalMissed.toString()
//                    }
//                }
//                cell {
//                    updatePaddingVerticalHalf()
//                    title = context.getString(R.string.account_info_url)
//                    subtitleView.startScrolling()
//                    isVisible = false
//                    viewModel.accountWitnessInfo.observe(viewLifecycleOwner) {
//                        subtitle = it.url.toString()
//                        isVisible = subtitle.isNotBlank()
//                        doOnClick {
//                            startUriBrowser(it.url)
//                        }
//                    }
//                }
//                isVisible = false
//                viewModel.accountWitnessInfo.observe(viewLifecycleOwner) {
//                    isVisible = true
//                }
//            }
//
//            // statistics
//            section {
//                header = getString(R.string.account_info_statistics)
//                cell {
//                    updatePaddingVerticalHalf()
//                    title = getString(R.string.account_info_lifetime_fee_paid)
//                    viewModel.accountStatistics.observe(viewLifecycleOwner) {
//                        subtitle = formatCoreAssetBalance(it.lifetimeFeesPaid)
//                    }
//                }
//                cell {
//                    updatePaddingVerticalHalf()
//                    title = getString(R.string.account_info_pending_fee)
//                    viewModel.accountStatistics.observe(viewLifecycleOwner) {
//                        subtitle = formatCoreAssetBalance(it.pendingFees)
//                    }
//                }
//                cell {
//                    title = getString(R.string.account_info_pending_vested_fee)
//                    updatePaddingVerticalHalf()
//                    viewModel.accountStatistics.observe(viewLifecycleOwner) {
//                        subtitle = formatCoreAssetBalance(it.pendingVestingFees)
//                    }
//                }
//                cell {
//                    updatePaddingVerticalHalf()
//                    title = getString(R.string.account_info_total_operations)
//                    viewModel.accountStatistics.observe(viewLifecycleOwner) {
//                        subtitle = it.totalOperations.toString()
//                    }
//                }
//                cell {
//                    updatePaddingVerticalHalf()
//                    title = getString(R.string.account_info_removed_operations)
//                    viewModel.accountStatistics.observe(viewLifecycleOwner) {
//                        subtitle = it.removedOperations.toString()
//                    }
//                }
//                cell {
//                    updatePaddingVerticalHalf()
//                    title = getString(R.string.account_info_voting_state)
//                    viewModel.accountStatistics.observe(viewLifecycleOwner) {
//                        subtitle = getString(if (it.isVoting) R.string.account_info_voting else R.string.account_info_not_voting)
//                    }
//                }
//                cell {
//                    updatePaddingVerticalHalf()
//                    title = getString(R.string.account_info_last_vote_time)
//                    isVisible = false
//                    viewModel.accountStatistics.observe(viewLifecycleOwner) {
//                        isVisible = it.isVoting
//                        subtitle = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(it.lastVoteTime)
//                    }
//                }
//            }
//
//            // fee distribution
//            section {
//                header = context.getString(R.string.account_info_fee_allocation)
//                cell {
//                    updatePaddingVerticalHalf()
//                    isVisible = false
//                    title = context.getString(R.string.tag_registrar)
//                    combineNonNull(viewModel.registrar, viewModel.registerFeePercentage).observe(viewLifecycleOwner) { (account,percentage) ->
//                        isVisible = true
//                        subtitle = "${account.nameOrId} (${formatGraphenePercentage(percentage, 100)})"
//                        doOnClick { startAccountBrowser(account.uid) }
//                        doOnLongClick { showAccountBrowserDialog(account) }
//                    }
//                }
//                cell {
//                    updatePaddingVerticalHalf()
//                    isVisible = false
//                    title = context.getString(R.string.tag_referrer)
//                    combineNonNull(viewModel.referrer, viewModel.referrerFeePercentage).observe(viewLifecycleOwner) { (account,percentage) ->
//                        isVisible = true
//                        subtitle = "${account.nameOrId} (${formatGraphenePercentage(percentage, 100)})"
//                        doOnClick { startAccountBrowser(account.uid) }
//                        doOnLongClick { showAccountBrowserDialog(account) }
//                    }
//                }
//                cell {
//                    updatePaddingVerticalHalf()
//                    isVisible = false
//                    title = context.getString(R.string.tag_lifetime_referrer)
//                    combineNonNull(viewModel.lifetimeReferrer, viewModel.lifetimeReferrerFeePercentage).observe(viewLifecycleOwner) { (account,percentage) ->
//                        isVisible = true
//                        subtitle = "${account.nameOrId} (${formatGraphenePercentage(percentage, 100)})"
//                        doOnClick { startAccountBrowser(account.uid) }
//                        doOnLongClick { showAccountBrowserDialog(account) }
//                    }
//                }
//                cell {
//                    updatePaddingVerticalHalf()
//                    isVisible = false
//                    title = context.getString(R.string.tag_network)
//                    combineNonNull(viewModel.network, viewModel.lifetimeReferrerFeePercentage).observe(viewLifecycleOwner) { (account,percentage) ->
//                        isVisible = true
//                        subtitle = "${account.nameOrId} (${formatGraphenePercentage(percentage, 100)})"
//                        doOnClick { startAccountBrowser(account.uid) }
//                        doOnLongClick { showAccountBrowserDialog(account) }
//                    }
//                }
//            }
            logo()
        }
    }

}
