package com.bitshares.oases.ui.main.dashboard

import android.content.Context
import android.view.Gravity
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.entities.LimitOrder
import bitshareskit.objects.CallOrder
import com.bitshares.oases.R
import com.bitshares.oases.chain.AccountBalance
import com.bitshares.oases.extensions.compat.showAccountBalanceBrowserDialog
import com.bitshares.oases.extensions.compat.startAssetBrowser
import com.bitshares.oases.extensions.compat.startCollateral
import com.bitshares.oases.extensions.viewbinder.bindAccountBalanceTable
import com.bitshares.oases.extensions.viewbinder.bindCallOrderTable
import com.bitshares.oases.extensions.viewbinder.bindLimitOrderTable
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.startFragment
import com.bitshares.oases.ui.main.MainFragment
import com.bitshares.oases.ui.main.MainViewModel
import com.bitshares.oases.ui.main.balance.BalanceFragment
import com.bitshares.oases.ui.transfer.ReceiveFragment
import com.bitshares.oases.ui.transfer.ScannerFragment
import com.bitshares.oases.ui.transfer.TransferFragment
import modulon.UI
import modulon.component.*
import modulon.component.cell.*
import modulon.extensions.font.typefaceBold
import modulon.extensions.graphics.createSelectorDrawable
import modulon.extensions.temp.drawShaders
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.horizontalLayout
import modulon.extensions.viewbinder.noClipping
import modulon.extensions.viewbinder.verticalLayout
import modulon.layout.lazy.*
import modulon.layout.linear.HorizontalView

class DashboardFragment : ContainerFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by activityViewModels()

    private class Item(context: Context) : ComponentPaddingCell(context) {
        init {
            verticalLayout {
                view(iconView) {
                    layoutWidth = resources.getDimensionPixelSize(IconSize.SIZE_5.size)
                    layoutHeight = resources.getDimensionPixelSize(IconSize.SIZE_5.size)
                    layoutGravityLinear = Gravity.CENTER_HORIZONTAL
                }
                view(titleView) {
                    isAllCaps = true
                    typeface = typefaceBold
                    textSize = 15f
                    layoutGravityLinear = Gravity.CENTER_HORIZONTAL
                }
            }
            background = createSelectorDrawable(context.getColor(R.color.background), UI.CORNER_RADIUS.dpf)
        }
    }

    override fun onCreateView() {
        setupRecycler {
            section {
                isolated<HorizontalView> {
                    layoutWidth = MATCH_PARENT
                    noClipping()
                    view<Item> {
                        drawShaders()
                        icon = R.drawable.ic_test_round_qr_code_scanner_24.contextDrawable()
                        title = "Scan"
                        layoutWeightLinear = 1f
                        doOnClick { startFragment<ScannerFragment>() }
                    }
                    view<ComponentSpacerCell> {
                        layoutWidth = context.resources.getDimensionPixelSize(R.dimen.global_spacer_size)
                        layoutHeight = 0
                    }
                    view<Item> {
                        drawShaders()
                        icon = R.drawable.ic_test_outline_assignment_returned_24.contextDrawable()
                        title = "Recvive"
                        layoutWeightLinear = 1f
                        doOnClick { startFragment<ReceiveFragment>() }
                    }
                    view<ComponentSpacerCell> {
                        layoutWidth = context.resources.getDimensionPixelSize(R.dimen.global_spacer_size)
                        layoutHeight = 0
                    }
                    view<Item> {
                        drawShaders()
                        icon = R.drawable.ic_test_round_send_24.contextDrawable()
                        title = "Transfer"
                        layoutWeightLinear = 1f
                        doOnClick { startFragment<TransferFragment>() }
                    }
                }
            }
            section {
                header = "Balances"
                list<ComponentCell, AccountBalance> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        bindAccountBalanceTable(it)
                        subtitle = it.value.toStringOrEmpty()
                        doOnClick {
                            startAssetBrowser(it.balance.assetUid)
                        }
                        doOnLongClick { showAccountBalanceBrowserDialog(it.balance) }
                    }
                    distinctItemsBy { it.balance.assetUid }
                    accountViewModel.balanceSorted.observe { adapter.submitList(it.take(5)) }
                }
                cell {
                    buttonStyle()
                    title = "Show More"
                    accountViewModel.balanceSorted.observe { isVisible = it.size > 5 }
                    doOnClick { mainViewModel.select(MainFragment.Tabs.BALANCE, BalanceFragment.Tabs.BALANCES) }
                }
                isVisible = false
                accountViewModel.balanceSorted.observe { isVisible = it.isNotEmpty() }
            }
            section {
                header = "Limit Orders"
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
                    accountViewModel.limitOrdersDetailed.observe { adapter.submitList(it.take(5)) }
                }
                cell {
                    buttonStyle()
                    title = "Show More"
                    accountViewModel.limitOrdersDetailed.observe { isVisible = it.size > 5 }
                    doOnClick { mainViewModel.select(MainFragment.Tabs.BALANCE, BalanceFragment.Tabs.LIMIT_ORDERS) }
                }
                isVisible = false
                accountViewModel.limitOrdersDetailed.observe { isVisible = it.isNotEmpty() }
            }
            section {
                header = "Margin Positions"
                list<ComponentCell, CallOrder> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        bindCallOrderTable(it)
                        doOnClick { startCollateral(it.borrower.uid, it.debt.asset.uid) }
                    }
                    distinctContentBy { }
                    accountViewModel.callOrdersExtended.observe { adapter.submitList(it.take(5)) }
                }
                cell {
                    buttonStyle()
                    title = "Show More"
                    accountViewModel.callOrdersExtended.observe { isVisible = it.size > 5 }
                    doOnClick { mainViewModel.select(MainFragment.Tabs.BALANCE, BalanceFragment.Tabs.MARGIN_POSITIONS) }
                }
                isVisible = false
                accountViewModel.callOrders.observe { isVisible = it.isNotEmpty() }
            }
            logo()
        }
    }


}


