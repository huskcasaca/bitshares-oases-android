package com.bitshares.oases.ui.asset.browser

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.models.PriceFeed
import com.bitshares.oases.extensions.compat.startAccountBrowser
import com.bitshares.oases.extensions.viewbinder.bindFeed
import com.bitshares.oases.ui.asset.AssetViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.component.ComponentCell
import modulon.extensions.view.doOnClick
import modulon.layout.recycler.*

class AssetBrowserFragment_Feeds : ContainerFragment() {

    private val viewModel: AssetViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler {
            section {
                list<ComponentCell, PriceFeed> {
                    data {
                        // TODO: 3/10/2021 replace with livedata
                        bindFeed(it, viewModel.bitassetFP.value)
                        doOnClick { startAccountBrowser(it.provider.uid) }
                    }
                    distinctItemsBy { it.provider.uid }
                    distinctContentBy { it }
                    viewModel.averageSettlementPrice.observe(viewLifecycleOwner) { }
                    viewModel.bitassetFeeds.observe(viewLifecycleOwner) { adapter.submitList(it.sortedBy { it.settlementPrice.value }) }
                }
                isVisible = false
                viewModel.bitassetFeeds.observe(viewLifecycleOwner) {
                    isVisible = it.isNotEmpty()
                }
            }
        }
    }

}
