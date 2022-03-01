package com.bitshares.android.ui.main.explore

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.WitnessObject
import com.bitshares.android.R
import com.bitshares.android.extensions.compat.showWitnessBrowserDialog
import com.bitshares.android.extensions.compat.startAccountBrowser
import com.bitshares.android.extensions.viewbinder.bindWitnessV3
import com.bitshares.android.extensions.viewbinder.logo
import com.bitshares.android.ui.account.picker.AccountPickerViewModel
import com.bitshares.android.ui.account.voting.VotingViewModel
import com.bitshares.android.ui.asset.picker.AssetPickerViewModel
import com.bitshares.android.ui.base.ContainerFragment
import com.bitshares.android.ui.base.*
import com.bitshares.android.ui.main.MainViewModel
import modulon.component.ComponentCell
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.layout.recycler.*

class ExploreFragment_Witness : ContainerFragment() {

    private val exploreViewModel: ExploreViewModel by activityViewModels()
    private val votingViewModel: VotingViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val accountSearchingViewModel: AccountPickerViewModel by activityViewModels()
    private val assetSearchingViewModel: AssetPickerViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler {
            section {
                header = context.getString(R.string.voting_active_witness)
                list<ComponentCell, WitnessObject> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        bindWitnessV3(it)
                        doOnClick { startAccountBrowser(it.ownerUid) }
                        doOnLongClick { showWitnessBrowserDialog(it) }
                    }
                    distinctItemsBy { it.uid }
                    votingViewModel.activeWitnessesFiltered.observe(viewLifecycleOwner) { adapter.submitList(it) }
                }
                votingViewModel.activeWitnessesFiltered.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            section {
                header = context.getString(R.string.voting_standby_witness)
                list<ComponentCell, WitnessObject> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        bindWitnessV3(it)
                        doOnClick { startAccountBrowser(it.ownerUid) }
                        doOnLongClick { showWitnessBrowserDialog(it) }
                    }
                    distinctItemsBy { it.uid }
                    votingViewModel.standbyWitnessesFiltered.observe(viewLifecycleOwner) { adapter.submitList(it) }
                }
                votingViewModel.standbyWitnessesFiltered.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            logo()
        }
    }
}