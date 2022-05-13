package com.bitshares.oases.ui.account.browser

import androidx.core.view.isVisible
import androidx.core.view.postDelayed
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.chain.AccountBalance
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.showAccountBalanceBrowserDialog
import com.bitshares.oases.extensions.compat.startAssetBrowser
import com.bitshares.oases.extensions.viewbinder.bindAccountBalanceTable
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.putJson
import modulon.component.cell.ComponentCell
import modulon.extensions.compat.finishActivity
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.layout.lazy.*

class AccountBrowserFragment_Balance : ContainerFragment() {

    private val viewModel: AccountViewModel by activityViewModels()

    override fun onCreateView() {
        setupRecycler {
//            section {
//                cell {
//                    titleView.textSize = 22f
//                    title = context.getString(R.string.account_balance_total_balance)
//                    viewModel.totalAmount.observe(viewLifecycleOwner) { text = it.toString() }
//                    // expandable
//                    viewModel.totalAmount.observe(viewLifecycleOwner) { isVisible = !viewModel.isPicker }
//
//                }
//            }
            section {
                list<ComponentCell, AccountBalance> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        bindAccountBalanceTable(it)
                        subtitle = it.value.toStringOrEmpty()
                        doOnClick {
                            if (viewModel.isPicker) {
                                finishActivity {
                                    putJson(IntentParameters.Account.KEY_BALANCE, it)
                                }
                            } else startAssetBrowser(it.balance.assetUid)
                        }
                        doOnLongClick { showAccountBalanceBrowserDialog(it.balance) }
                    }
                    distinctItemsBy { it.balance.assetUid }
                    viewModel.balanceSorted.observe(viewLifecycleOwner) { submitList(it) }
                    viewModel.accountUid.observe(viewLifecycleOwner) { postDelayed(500) { smoothScrollToPosition(0) } }
                }
                viewModel.balanceSorted.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            logo()
        }
    }

}