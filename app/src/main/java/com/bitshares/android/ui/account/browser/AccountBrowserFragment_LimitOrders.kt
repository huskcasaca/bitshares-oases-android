package com.bitshares.android.ui.account.browser

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.entities.LimitOrder
import com.bitshares.android.extensions.viewbinder.bindLimitOrderTable
import com.bitshares.android.extensions.viewbinder.logo
import com.bitshares.android.ui.account.AccountViewModel
import com.bitshares.android.ui.base.ContainerFragment
import com.bitshares.android.ui.base.*
import modulon.component.ComponentCell
import modulon.extensions.view.doOnClick
import modulon.layout.recycler.*

class AccountBrowserFragment_LimitOrders : ContainerFragment() {

    private val viewModel: AccountViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                    viewModel.limitOrdersDetailed.observe(viewLifecycleOwner) { adapter.submitList(it) }
                }
                viewModel.limitOrdersDetailed.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            logo()
        }
    }

}