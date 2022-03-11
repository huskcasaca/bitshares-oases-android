package com.bitshares.oases.ui.main.explore

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.ui.account.picker.AccountPickerViewModel
import com.bitshares.oases.ui.account.voting.VotingViewModel
import com.bitshares.oases.ui.asset.picker.AssetPickerViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.main.MainViewModel
import modulon.extensions.view.*
import modulon.extensions.viewbinder.nestedScrollableHost
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout

class ExploreFragment : ContainerFragment() {

    enum class Tabs(override val stringRes: Int): StringResTabs {
        BLOCKCHAIN(R.string.chain_explore_tab_blockchain),
        WITNESS(R.string.chain_explore_tab_witnesses),
        COMMITTEE(R.string.chain_explore_tab_committee),
        WORKER(R.string.chain_explore_tab_worker),
        ACCOUNT(R.string.chain_explore_tab_account),
        ASSET(R.string.chain_explore_tab_asset),
        MARKET(R.string.chain_explore_tab_market),
        FEE_SCHEDULE(R.string.chain_explore_tab_fee_schedule),
    }

    private val exploreViewModel: ExploreViewModel by activityViewModels()
    private val votingViewModel: VotingViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val accountSearchingViewModel: AccountPickerViewModel by activityViewModels()
    private val assetSearchingViewModel: AssetPickerViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupVertical {
            tabLayout {
                attachTabs(Tabs.values())
                post { attachViewPager2(nextView<ViewGroup>().getFirstChild()) }
            }
            nestedScrollableHost {
                pagerLayout {
                    attachFragmentListAdapter(Tabs.values()) {
                        when (it) {
                            Tabs.BLOCKCHAIN -> ExploreFragment_Blockchain()
                            Tabs.WITNESS -> ExploreFragment_Witness()
                            Tabs.COMMITTEE -> ExploreFragment_Committee()
                            Tabs.WORKER -> ExploreFragment_Worker()
                            Tabs.ACCOUNT -> ExploreFragment_Account()
                            Tabs.ASSET -> ExploreFragment_Asset()
                            Tabs.MARKET -> ExploreFragment_Market()
                            Tabs.FEE_SCHEDULE -> ExploreFragment_FeeSchedule()
                        }
                    }
                    offscreenPageLimit = 2
                    exploreViewModel.notifyTab.observe { currentItem = it.ordinal }
                    doOnPageSelected { mainViewModel.exploreTab = Tabs.values()[it] }
                }
                setParamsFill()
            }
        }
        // FIXME: 22/1/2022     java.lang.NullPointerException: Attempt to invoke virtual method 'long java.lang.Number.longValue()' on a null object reference
        votingViewModel.refresh()
    }




}