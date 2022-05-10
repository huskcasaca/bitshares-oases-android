package com.bitshares.oases.ui.account.browser

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.entities.LimitOrder
import com.bitshares.oases.extensions.viewbinder.bindLimitOrderTable
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.component.cell.ComponentCell
import modulon.extensions.view.doOnClick
import modulon.layout.lazy.*

class AccountBrowserFragment_LimitOrders : ContainerFragment() {

    private val viewModel: AccountViewModel by activityViewModels()

    override fun onCreateView() {

        setupRecycler {
            section {
                list<ComponentCell, LimitOrder> {
                    data {
                        bindLimitOrderTable(it)
                        doOnClick {
                            bindLimitOrderTable(it.copy(market = it.market.inverted))
                        }
                    }
                    distinctItemsBy { it.order.uid }
                    distinctContentBy { it }
                    viewModel.limitOrdersDetailed.observe(viewLifecycleOwner) { submitList(it) }
                }
                viewModel.limitOrdersDetailed.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            logo()
        }
    }

}