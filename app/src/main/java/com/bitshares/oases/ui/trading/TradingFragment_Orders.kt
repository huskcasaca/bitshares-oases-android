package com.bitshares.oases.ui.trading

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.entities.LimitOrder
import com.bitshares.oases.extensions.viewbinder.bindLimitOrderTable
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.transaction.showLimitOrderCancelDialog
import modulon.component.cell.ComponentCell
import modulon.dialog.section
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.viewbinder.cell
import modulon.layout.lazy.data
import modulon.layout.lazy.distinctItemsBy
import modulon.layout.lazy.list
import modulon.layout.lazy.section

class TradingFragment_Orders : ContainerFragment() {

    private val viewModel: TradingViewModel by activityViewModels()

    override fun onCreateView() {
        setupRecycler {
            section {
                list<ComponentCell, LimitOrder> {
                    data {
                        bindLimitOrderTable(it)
                        doOnLongClick { showDetailedOrderDialog(it) }
                    }
                    distinctItemsBy { it.order.id }
                    viewModel.currentMarketLimitOrders.observe(viewLifecycleOwner) { submitList(it) }
                }
                isVisible = false
                viewModel.currentMarketLimitOrders.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
        }
    }
    private fun showDetailedOrderDialog(order: LimitOrder) = showBottomDialog {
        title = "Limit Order"
        subtitle = order.order.uid.toString()
        section {
            cell {
                text = "Order Detail"
                doOnClick { dismiss() }
            }
            cell {
                text = "Cancel Order"
                doOnClick {
                    showLimitOrderCancelDialog(viewModel.createCancelOperation(order.order), order.market)
                    dismiss()
                }
            }
        }
    }

}