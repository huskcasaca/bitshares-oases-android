package com.bitshares.oases.ui.account.browser

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.chain.AccountBalance
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.showAccountBalanceBrowserDialog
import com.bitshares.oases.extensions.compat.startAssetBrowser
import com.bitshares.oases.extensions.viewbinder.bindAccountBalanceTable
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.putJson
import modulon.component.ComponentCell
import modulon.extensions.compat.finish
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.layout.recycler.*

class AccountBrowserFragment_Balance : ContainerFragment() {

    private val viewModel: AccountViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler {
//            addExpandable<GrapheneComponentCell> {
//                initView {
//                    roundCorner()
//                    titleView.textSize = 22f
//                    title = context.getString(R.string.account_balance_total_balance)
//                    viewModel.totalAmount.observe(viewLifecycleOwner) { text = it.toString() }
//                }
//                isExpanded = false
//                viewModel.totalAmount.observe(viewLifecycleOwner) { isExpanded = !viewModel.isPicker }
//
//            }
            section {
                list<ComponentCell, AccountBalance> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        bindAccountBalanceTable(it)
                        subtitle = it.value.toStringOrEmpty()
                        doOnClick {
                            if (viewModel.isPicker) {
                                finish {
                                    putJson(IntentParameters.Account.KEY_BALANCE, it)
                                }
                            } else startAssetBrowser(it.balance.assetUid)
                        }
                        doOnLongClick { showAccountBalanceBrowserDialog(it.balance) }
                    }
                    distinctItemsBy { it.balance.assetUid }
                    viewModel.balanceSorted.observe(viewLifecycleOwner) { adapter.submitList(it) }
                    viewModel.accountUid.observe(viewLifecycleOwner) { postDelayed(500) { smoothScrollToPosition(0) } }
                }
                viewModel.balanceSorted.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            logo()
        }
    }

}