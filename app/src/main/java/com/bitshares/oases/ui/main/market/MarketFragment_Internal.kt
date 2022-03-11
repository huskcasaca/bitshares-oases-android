package com.bitshares.oases.ui.main.market

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.models.Ticker
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.startMarketTrade
import com.bitshares.oases.extensions.viewbinder.bindTicker
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.extensions.viewbinder.setTickerStyle
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.component.ComponentCell
import modulon.component.buttonStyle
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.viewbinder.cell
import modulon.layout.recycler.*

class MarketFragment_Internal : ContainerFragment() {

    private val viewModel: MarketViewModel by activityViewModels()

    private val assetUid by lazy { requireArguments().get(IntentParameters.KEY_ASSET_UID) as Long }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler {
            val tickersFiltered = viewModel.getFilteredTickers(assetUid)
            section {
                header = "Default Group"
                list<ComponentCell, Ticker> {
                    construct { setTickerStyle() }
                    data {
                        bindTicker(it)
                        doOnClick { startMarketTrade(it.market) }
                        doOnLongClick { showTradePairDialog(it) }
                    }
                    distinctItemsBy { it.base.uid to it.quote.uid }
                    distinctContentBy { it }
                    tickersFiltered.observe(viewLifecycleOwner) { adapter.submitList(it) }
                    viewModel.invertColor.observe(viewLifecycleOwner) { adapter.notifyDataSetChanged() }
                }
                tickersFiltered.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
            }
            section {
                cell {
                    buttonStyle()
                    title = context.getString(R.string.market_add_trade_pair)
                    doOnClick { showAddTradePairDialog() }
                }
            }
            logo()
        }
    }

}

