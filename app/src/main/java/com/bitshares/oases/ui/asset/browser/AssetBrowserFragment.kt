package com.bitshares.oases.ui.asset.browser

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.AssetObjectType
import com.bitshares.oases.R
import com.bitshares.oases.ui.asset.AssetViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.raw_data.JsonRawDataFragment
import com.bitshares.oases.ui.raw_data.JsonRawDataViewModel
import modulon.extensions.view.*
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout
import modulon.layout.actionbar.subtitle
import java.util.*

class AssetBrowserFragment : ContainerFragment() {

    enum class Tabs(override val stringRes: Int) : StringResTabs {
        INFO(R.string.asset_browser_tab_basic_info),
        BITASSET(R.string.asset_browser_tab_bitasset),
        FEED(R.string.asset_browser_tab_feeds),
        RAW(R.string.tab_raw_data),
    }

    private val viewModel: AssetViewModel by activityViewModels()
    private val rawViewModel: JsonRawDataViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            titleConnectionState(context.getString(R.string.asset_browser_title))
            networkStateMenu()
            walletStateMenu()
            viewModel.assetSymbol.observe(viewLifecycleOwner) { subtitle(it.toUpperCase(Locale.ROOT)) }
            viewModel.assetNonNull.observe(viewLifecycleOwner) { rawViewModel.setContent(it) }
        }
        setupVertical {
            tabLayout {
                viewModel.assetType.observe(viewLifecycleOwner) {
                    val list = when (it) {
                        AssetObjectType.UNDEFINED, AssetObjectType.CORE, AssetObjectType.UIA -> listOf(Tabs.INFO, Tabs.RAW)
                        AssetObjectType.MPA, AssetObjectType.PREDICTION -> Tabs.values().toList()
                    }
                    attachTabs(list)
                    post { attachViewPager2(nextView()) }
                }
            }
            pagerLayout {
                attachEmptyAdapter()
                viewModel.assetType.observe {
                    val list = when (it) {
                        AssetObjectType.UNDEFINED, AssetObjectType.CORE, AssetObjectType.UIA -> listOf(Tabs.INFO, Tabs.RAW)
                        AssetObjectType.MPA, AssetObjectType.PREDICTION -> Tabs.values().toList()
                    }
                    attachFragmentListAdapter(list) {
                        when (it) {
                            Tabs.INFO -> AssetBrowserFragment_Info()
                            Tabs.BITASSET -> AssetBrowserFragment_Bitasset()
                            Tabs.FEED -> AssetBrowserFragment_Feeds()
                            Tabs.RAW -> JsonRawDataFragment()
                        }
                    }
                }
            }
        }

    }

}