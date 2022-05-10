package com.bitshares.oases.ui.main.explore

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.WorkerObject
import com.bitshares.oases.R
import com.bitshares.oases.extensions.compat.showWorkerBrowserDialog
import com.bitshares.oases.extensions.viewbinder.bindWorkerV3
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

class ExploreFragment_Worker : ContainerFragment() {

    private val exploreViewModel: ExploreViewModel by activityViewModels()
    private val votingViewModel: VotingViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val accountSearchingViewModel: AccountPickerViewModel by activityViewModels()
    private val assetSearchingViewModel: AssetPickerViewModel by activityViewModels()

    override fun onCreateView() {
        setupRecycler {
            section {
                header = context.getString(R.string.voting_active_workers)
                list<ComponentCell, WorkerObject> {
                    construct {
                        updatePaddingVerticalHalf()
                    }
                    data {
                        bindWorkerV3(it, true)
                        doOnClick {}
                        doOnLongClick {
                            showWorkerBrowserDialog(it)
                        }
                    }
                    payload { data, payload -> isChecked = (payload as Set<*>).contains(data.voteFor) }
                    distinctItemsBy { it.uid }
                    votingViewModel.activeWorkersFiltered.observe(viewLifecycleOwner) { submitList(it) }
                }
                votingViewModel.activeWorkersFiltered.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            section {
                header = context.getString(R.string.voting_standby_workers)
                list<ComponentCell, WorkerObject> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        bindWorkerV3(it, false)
                        doOnClick {}
                        doOnLongClick { showWorkerBrowserDialog(it) }
                    }
                    payload { data, payload -> isChecked = (payload as Set<*>).contains(data.voteFor) }
                    distinctItemsBy { it.uid }
                    votingViewModel.workerList.observe(viewLifecycleOwner) { submitList(it) }
                }
                votingViewModel.workerList.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            logo()
        }
    }
}