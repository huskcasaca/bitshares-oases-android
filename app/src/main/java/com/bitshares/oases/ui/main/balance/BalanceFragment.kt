package com.bitshares.oases.ui.main.balance

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.main.MainViewModel
import modulon.extensions.compat.arguments
import modulon.extensions.view.*
import modulon.extensions.viewbinder.nestedScrollableHost
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout

class BalanceFragment : ContainerFragment() {

    enum class Tabs(override val stringRes: Int): StringResTabs {
        BALANCES(R.string.balance_tab_balances),
        LIMIT_ORDERS(R.string.balance_tab_limit_orders),
        MARGIN_POSITIONS(R.string.balance_tab_margin_positions),
    }

    private val mainViewModel: MainViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()

    override fun onCreateView() {

        setupVertical {
            tabLayout {
                attachTabs(Tabs.values())
                post { attachViewPager2(nextView<ViewGroup>().getFirstChild()) }
            }
            nestedScrollableHost {
                layoutWidth = MATCH_PARENT
                layoutHeight = MATCH_PARENT
                pagerLayout {
                    offscreenPageLimit = 2
                    mainViewModel.selectedBalanceTab.observe { setCurrentItem(it.ordinal, false) }
                    attachFragmentListAdapter(Tabs.values()) {
                        BalanceFragment_Internal().arguments { putSerializable(IntentParameters.KEY_TAB_TYPE, it) }
                    }
                    doOnPageSelected { mainViewModel.balanceTab = Tabs.values()[it] }
                }
            }

        }
    }

}