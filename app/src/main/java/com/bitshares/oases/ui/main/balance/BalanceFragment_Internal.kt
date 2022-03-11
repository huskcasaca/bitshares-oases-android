package com.bitshares.oases.ui.main.balance

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.fragment.app.activityViewModels
import bitshareskit.entities.LimitOrder
import bitshareskit.objects.CallOrder
import com.bitshares.oases.chain.AccountBalance
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.showAccountBalanceBrowserDialog
import com.bitshares.oases.extensions.compat.startAssetBrowser
import com.bitshares.oases.extensions.compat.startCollateral
import com.bitshares.oases.extensions.viewbinder.bindAccountBalanceTable
import com.bitshares.oases.extensions.viewbinder.bindCallOrderTable
import com.bitshares.oases.extensions.viewbinder.bindLimitOrderTable
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.putJson
import com.bitshares.oases.ui.main.MainFragment
import com.bitshares.oases.ui.main.MainViewModel
import modulon.component.ComponentCell
import modulon.extensions.compat.finish
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.layout.recycler.*

class BalanceFragment_Internal : ContainerFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()

    private val tab by lazy { requireArguments().getSerializable(IntentParameters.KEY_TAB_TYPE) as BalanceFragment.Tabs }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler {
            when (tab) {
                BalanceFragment.Tabs.BALANCES -> {
                    section {
                        list<ComponentCell, AccountBalance> {
                            construct { updatePaddingVerticalHalf() }
                            data {
                                bindAccountBalanceTable(it)
                                subtitle = it.value.toStringOrEmpty()
                                doOnClick {
                                    // TODO: 2022/2/19 remove
                                    if (accountViewModel.isPicker) {
                                        finish {
                                            putJson(IntentParameters.AccountBalance.KEY_UID, it.balance.uid)
                                        }
                                    } else startAssetBrowser(it.balance.assetUid)
                                }
                                doOnLongClick { showAccountBalanceBrowserDialog(it.balance) }
                            }
                            distinctItemsBy { it.balance.assetUid }
                            accountViewModel.balanceSorted.observe(viewLifecycleOwner) { adapter.submitList(it) }
                        }
                        isVisible = false
                        accountViewModel.balanceSorted.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                    }
                }
                BalanceFragment.Tabs.LIMIT_ORDERS -> {
                    section {
                        list<ComponentCell, LimitOrder> {
                            construct { updatePaddingVerticalHalf() }
                            data {
                                bindLimitOrderTable(it)
                                doOnClick {
                                    bindLimitOrderTable(it.copy(market = it.market.inverted))
                                }
                            }
                            distinctItemsBy { it.order.uid }
                            distinctContentBy { it }
                            accountViewModel.limitOrdersDetailed.observe { adapter.submitList(it) }
                        }
                        isVisible = false
                        accountViewModel.limitOrdersDetailed.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                    }
                }
                BalanceFragment.Tabs.MARGIN_POSITIONS -> {
                    section {
                        list<ComponentCell, CallOrder> {
                            construct { updatePaddingVerticalHalf() }
                            data {
                                bindCallOrderTable(it)
                                doOnClick { startCollateral(it.borrower.uid, it.debt.asset.uid) }
                            }
                            distinctContentBy { }
                            accountViewModel.callOrdersExtended.observe { adapter.submitList(it) }
                        }
                        isVisible = false
                        accountViewModel.callOrdersExtended.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                    }
                }
            }
            logo()
            accountViewModel.accountUid.observe(viewLifecycleOwner) { postDelayed(500) { smoothScrollToPosition(0) } }
            mainViewModel.doubleClickedPage.observe(viewLifecycleOwner) { if (it == MainFragment.Tabs.BALANCE) smoothScrollToPosition(0) }
        }
    }

}