package com.bitshares.oases.ui.main.explore

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.chain.CommitteeMember
import com.bitshares.oases.extensions.compat.showCommitteeBrowserDialog
import com.bitshares.oases.extensions.compat.startAccountBrowser
import com.bitshares.oases.extensions.viewbinder.bindCommitteeV3
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account.picker.AccountPickerViewModel
import com.bitshares.oases.ui.account.voting.VotingViewModel
import com.bitshares.oases.ui.asset.picker.AssetPickerViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.main.MainViewModel
import modulon.component.cell.ComponentCell
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.layout.lazy.*

class ExploreFragment_Committee : ContainerFragment() {

    private val exploreViewModel: ExploreViewModel by activityViewModels()
    private val votingViewModel: VotingViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val accountSearchingViewModel: AccountPickerViewModel by activityViewModels()
    private val assetSearchingViewModel: AssetPickerViewModel by activityViewModels()

    override fun onCreateView() {
        setupRecycler {
            section {
                header = context.getString(R.string.voting_active_committee_member)
                list<ComponentCell, CommitteeMember> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        bindCommitteeV3(it)
                        doOnClick { startAccountBrowser(it.committee.ownerUid) }
                        doOnLongClick { showCommitteeBrowserDialog(it.committee) }
                    }
                    distinctItemsBy { it.committee.uid }
                    votingViewModel.activeCommitteeMembersFiltered.observe(viewLifecycleOwner) { submitList(it) }
                }
                votingViewModel.activeCommitteeMembersFiltered.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            section {
                header = context.getString(R.string.voting_standby_committee_member)
                list<ComponentCell, CommitteeMember> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        bindCommitteeV3(it)
                        doOnClick { startAccountBrowser(it.committee.ownerUid) }
                        doOnLongClick { showCommitteeBrowserDialog(it.committee) }
                    }
                    distinctItemsBy { it.committee.uid }
                    votingViewModel.standbyCommitteeMembersFiltered.observe(viewLifecycleOwner) { submitList(it) }
                }
                votingViewModel.standbyCommitteeMembersFiltered.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            logo()
        }
    }
}