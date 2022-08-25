package com.bitshares.oases.ui.account.voting

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.AccountObject
import bitshareskit.objects.WitnessObject
import bitshareskit.objects.WorkerObject
import com.bitshares.oases.R
import com.bitshares.oases.chain.CommitteeMember
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.showCommitteeBrowserDialog
import com.bitshares.oases.extensions.compat.showWitnessBrowserDialog
import com.bitshares.oases.extensions.compat.showWorkerBrowserDialog
import com.bitshares.oases.extensions.viewbinder.bindCommitteeV3
import com.bitshares.oases.extensions.viewbinder.bindWitnessV3
import com.bitshares.oases.extensions.viewbinder.bindWorkerV3
import com.bitshares.oases.preference.AppConfig
import com.bitshares.oases.ui.account.voting.VotingFragment.Tabs
import com.bitshares.oases.ui.account.voting.VotingFragment.Tabs.*
import com.bitshares.oases.ui.base.ContainerFragment
import kotlinx.coroutines.launch
import modulon.component.cell.ComponentCell
import modulon.component.cell.IconSize
import modulon.dialog.button
import modulon.dialog.dismissWith
import modulon.dialog.doOnDismiss
import modulon.dialog.resumeWith
import modulon.extensions.compat.showBooleanSuspendedBottomDialog
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.layout.lazy.*

class VotingFragment_Tabs : ContainerFragment() {

    private val tab by lazy { requireArguments().getSerializable(IntentParameters.KEY_TAB_TYPE) as Tabs }
    private val viewModel: VotingViewModel by activityViewModels()

    override fun onCreateView() {

        setupRecycler {
            when (tab) {
                COMMITTEE_MEMBER -> {
                    section {
                        header = context.getString(R.string.voting_active_committee_member)
                        list<ComponentCell, CommitteeMember> {
                            construct { updatePaddingVerticalHalf() }
                            data {
                                iconSize = IconSize.COMPONENT_0
                                bindCommitteeV3(it)
                                doOnClick {
                                    lifecycleScope.launch {
                                        if (showRemoveProxyDialog()) viewModel.changeVote(it.committee.vote, !isChecked)
                                        adapter.notifyPayloadChanged(it)
                                    }
                                }
                                doOnLongClick { showCommitteeBrowserDialog(it.committee) }
                            }
                            payload { data, payload -> isChecked = (payload as Set<*>).contains(data.committee.vote) }
                            distinctItemsBy { it.committee.uid }
                            viewModel.activeCommitteeMembers.observe(viewLifecycleOwner) { submitList(it) }
                            viewModel.voting.observe(viewLifecycleOwner) { adapter.submitPayload(it, false) }
                        }
                        viewModel.activeCommitteeMembers.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                    }
                    section {
                        header = context.getString(R.string.voting_standby_committee_member)
                        list<ComponentCell, CommitteeMember> {
                            construct { updatePaddingVerticalHalf() }
                            data {
                                iconSize = IconSize.COMPONENT_0
                                bindCommitteeV3(it)
                                doOnClick {
                                    lifecycleScope.launch {
                                        if (showRemoveProxyDialog()) viewModel.changeVote(it.committee.vote, !isChecked)
                                        adapter.notifyPayloadChanged(it)
                                    }
                                }
                                doOnLongClick { showCommitteeBrowserDialog(it.committee) }
                            }
                            payload { data, payload -> isChecked = (payload as Set<*>).contains(data.committee.vote) }
                            distinctItemsBy { it.committee.uid }
                            viewModel.standbyCommitteeMembers.observe(viewLifecycleOwner) { submitList(it) }
                            viewModel.voting.observe(viewLifecycleOwner) { adapter.submitPayload(it, false) }
                        }
                        viewModel.standbyCommitteeMembers.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                    }
                }
                WITNESS -> {
                    section {
                        header = context.getString(R.string.voting_active_witness)
                        list<ComponentCell, WitnessObject> {
                            construct { updatePaddingVerticalHalf() }
                            data {
                                iconSize = IconSize.COMPONENT_0
                                bindWitnessV3(it)
                                doOnClick {
                                    lifecycleScope.launch {
                                        if (showRemoveProxyDialog()) viewModel.changeVote(it.vote, !isChecked)
                                        adapter.notifyPayloadChanged(it)
                                    }
                                }
                                doOnLongClick { showWitnessBrowserDialog(it) }
                            }
                            payload { data, payload -> isChecked = (payload as Set<*>).contains(data.vote) }
                            distinctItemsBy { it.uid }
                            viewModel.activeWitnesses.observe(viewLifecycleOwner) { submitList(it) }
                            viewModel.voting.observe(viewLifecycleOwner) { adapter.submitPayload(it, false) }
                        }
                        viewModel.activeWitnesses.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                    }
                    section {
                        header = context.getString(R.string.voting_standby_witness)
                        list<ComponentCell, WitnessObject> {
                            construct { updatePaddingVerticalHalf() }
                            data {
                                iconSize = IconSize.COMPONENT_0
                                bindWitnessV3(it)
                                doOnClick {
                                    lifecycleScope.launch {
                                        if (showRemoveProxyDialog()) viewModel.changeVote(it.vote, !isChecked)
                                        adapter.notifyPayloadChanged(it)
                                    }
                                }
                                doOnLongClick { showWitnessBrowserDialog(it) }
                            }
                            payload { data, payload -> isChecked = (payload as Set<*>).contains(data.vote) }
                            distinctItemsBy { it.uid }
                            viewModel.standbyWitnesses.observe(viewLifecycleOwner) { submitList(it) }
                            viewModel.voting.observe(viewLifecycleOwner) { adapter.submitPayload(it, false) }
                        }
                        viewModel.standbyWitnesses.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                    }
                }
                WORKER_PROPOSAL -> {
                    section {
                        header = context.getString(R.string.voting_active_workers)
                        list<ComponentCell, WorkerObject> {
                            construct {
                                updatePaddingVerticalHalf()
                            }
                            data {
                                bindWorkerV3(it, true)
                                doOnClick {
                                    lifecycleScope.launch {
                                        if (showRemoveProxyDialog()) viewModel.changeVote(it.voteFor, !isChecked)
                                        adapter.notifyPayloadChanged(it)
                                    }
                                }
                                doOnLongClick {
                                    showWorkerBrowserDialog(it)
                                }
                            }
                            payload { data, payload -> isChecked = (payload as Set<*>).contains(data.voteFor) }
                            distinctItemsBy { it.uid }
                            viewModel.activeWorkersFiltered.observe(viewLifecycleOwner) { submitList(it) }
                            viewModel.voting.observe(viewLifecycleOwner) { adapter.submitPayload(it, false) }
                        }
                        viewModel.activeWorkersFiltered.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                    }
                    section {
                        header = context.getString(R.string.voting_standby_workers)
                        list<ComponentCell, WorkerObject> {
                            construct { updatePaddingVerticalHalf() }
                            data {
                                bindWorkerV3(it, false)
                                doOnClick {
                                    lifecycleScope.launch {
                                        if (showRemoveProxyDialog()) viewModel.changeVote(it.voteFor, !isChecked)
                                        adapter.notifyPayloadChanged(it)
                                    }
                                }
                                doOnLongClick { showWorkerBrowserDialog(it) }
                            }
                            payload { data, payload -> isChecked = (payload as Set<*>).contains(data.voteFor) }
                            distinctItemsBy { it.uid }
                            viewModel.standbyWorkerFiltered.observe(viewLifecycleOwner) { submitList(it) }
                            viewModel.voting.observe(viewLifecycleOwner) { adapter.submitPayload(it, false) }
                        }
                        viewModel.standbyWorkerFiltered.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                    }
                }
                else -> {}
            }
        }
    }

    private suspend fun showRemoveProxyDialog(): Boolean {
        if (viewModel.proxyEnabled.value != true) {
            return true
        }
        if (!AppConfig.ENABLE_REMOVE_PROXY_CONFIRM) {
            viewModel.changeProxy(AccountObject.PROXY_TO_SELF)
            return true
        }
        return showRemoveProxyDialogInternal()
    }

    private suspend fun showRemoveProxyDialogInternal() =
        showBooleanSuspendedBottomDialog {
        title = context.getString(R.string.voting_proxy_title)
        message = context.getString(R.string.voting_proxy_change_message)
        isCancelableByButtons = true
        button {
            text = context.getString(R.string.button_confirm)
            doOnClick {
                viewModel.changeProxy(AccountObject.PROXY_TO_SELF)
                dismissWith(true)
            }
        }
        button { text = context.getString(R.string.button_cancel) }
        doOnDismiss { resumeWith(false) }
    }

}
