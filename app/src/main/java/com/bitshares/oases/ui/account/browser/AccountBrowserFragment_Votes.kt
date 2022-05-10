package com.bitshares.oases.ui.account.browser

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.WitnessObject
import bitshareskit.objects.WorkerObject
import bitshareskit.serializer.grapheneGlobalComparator
import com.bitshares.oases.R
import com.bitshares.oases.chain.CommitteeMember
import com.bitshares.oases.extensions.compat.*
import com.bitshares.oases.extensions.viewbinder.*
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.account.browser.AccountBrowserFragment_Votes.Votes.*
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.component.cell.ComponentCell
import modulon.component.cell.IconSize
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.layout.lazy.*
import java.util.*

class AccountBrowserFragment_Votes : ContainerFragment() {

    enum class Votes {
        WITNESS, COMMITTEE, WORKER
    }

    private val viewModel: AccountViewModel by activityViewModels()

    override fun onCreateView() {

        setupRecycler {
            section {
                header = context.getString(R.string.account_vote_proxy)
                cell {
                    updatePaddingVerticalHalf()
                    viewModel.votingAccount.observe(viewLifecycleOwner) {
                        bindAccountV3(it, true, IconSize.COMPONENT_0)
                        doOnClick { startAccountBrowser(it.uid) }
                        doOnLongClick { showAccountBrowserDialog(it) }
                    }
                }
                isVisible = false
                viewModel.votingAccount.observe(viewLifecycleOwner) { isVisible = it != null }
            }
            values().forEach { vote ->
                section {
                    val source = when (vote) {
                        COMMITTEE -> viewModel.accountCommitteeMemberVotes
                        WITNESS -> viewModel.accountWitnessVotes
                        WORKER -> viewModel.accountWorkerVotes
                    }
                    header = when (vote) {
                        COMMITTEE -> context.getString(R.string.account_vote_committee_member)
                        WITNESS -> context.getString(R.string.account_vote_witness)
                        WORKER -> context.getString(R.string.account_vote_worker_proposal)
                    }
                    when (vote) {
                        COMMITTEE -> list<ComponentCell, CommitteeMember> {
                            construct { updatePaddingVerticalHalf() }
                            data {
                                bindCommitteeSimple(it)
                                doOnLongClick { showCommitteeBrowserDialog(it.committee) }
                            }
                            distinctItemsBy { it.committee.uid }
                            TreeSet(grapheneGlobalComparator).toList()
                            viewModel.accountCommitteeMemberVotes.observe(viewLifecycleOwner) { adapter.submitList(it.sortedByDescending { it.committee.totalVotes }) }
                        }
                        WITNESS -> list<ComponentCell, WitnessObject> {
                            construct { updatePaddingVerticalHalf() }
                            data { witness ->
                                bindWitnessSimple(witness)
                                doOnLongClick { showWitnessBrowserDialog(witness) }
                            }
                            distinctItemsBy { it.uid }
                            viewModel.accountWitnessVotes.observe(viewLifecycleOwner) { adapter.submitList(it.sortedByDescending { it.totalVotes }) }
                        }
                        WORKER -> list<ComponentCell, WorkerObject> {
                            construct { updatePaddingVerticalHalf() }
                            data { worker ->
                                bindWorkerSimple(worker)
                                doOnLongClick { showWorkerBrowserDialog(worker) }
                            }
                            distinctItemsBy { it.uid }
                            viewModel.accountWorkerVotes.observe(viewLifecycleOwner) { adapter.submitList(it.sortedByDescending { it.totalVotesFor }) }
                        }
                    }
                    source.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                }
            }
            logo()
        }
    }

}
