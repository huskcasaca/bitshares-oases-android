package com.bitshares.oases.ui.asset.browser

import android.content.Intent
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.AssetObjectType
import com.bitshares.oases.R
import com.bitshares.oases.ui.asset.AssetViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.raw_data.JsonRawDataFragment
import com.bitshares.oases.ui.raw_data.JsonRawDataViewModel
import modulon.extensions.compat.activity
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.component.appbar.AppbarView
import modulon.component.appbar.actionMenu
import modulon.component.appbar.subtitle
import modulon.component.appbar.ActionBarBehavior
import modulon.component.appbar.ContainerScrollingBehavior
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@OptIn(ExperimentalContracts::class)
inline fun ViewGroup.actionBarLayout(block: AppbarView.() -> Unit = {}) {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    val view = AppbarView(context).apply {
        fitsSystemWindows = true
        actionMenu {
            icon = if (activity.intent.data != null && activity.intent.action == Intent.ACTION_VIEW) R.drawable.ic_cell_cross.contextDrawable() else R.drawable.ic_cell_back_arrow.contextDrawable()
        }
        block()
    }
    addView(view)
}

fun actionCoordinatorParams() = coordinatorParams(MATCH_PARENT, WRAP_CONTENT, behavior = ActionBarBehavior(true))
fun bodyCoordinatorParams() = coordinatorParams(MATCH_PARENT, MATCH_PARENT, behavior = ContainerScrollingBehavior())


class AssetBrowserFragment : ContainerFragment() {

    enum class Tabs(override val stringRes: Int) : StringResTabs {
        INFO(R.string.asset_browser_tab_basic_info),
        BITASSET(R.string.asset_browser_tab_bitasset),
        FEED(R.string.asset_browser_tab_feeds),
        RAW(R.string.tab_raw_data),
    }

    private val viewModel: AssetViewModel by activityViewModels()
    private val rawViewModel: JsonRawDataViewModel by activityViewModels()

    override fun ViewGroup.onCreateView() {
        fitsSystemWindows = true
        actionBarLayout {
            layoutParams = actionCoordinatorParams()
            titleConnectionState(context.getString(R.string.asset_browser_title))
            websocketStateMenu()
            walletStateMenu()
            viewModel.assetSymbol.observe(viewLifecycleOwner) { subtitle(it.toUpperCase(Locale.ROOT)) }
            viewModel.assetNonNull.observe(viewLifecycleOwner) { rawViewModel.setContent(it) }
        }
        verticalLayout {
            layoutParams = bodyCoordinatorParams()
            tabLayout {
                viewModel.assetType.observe(viewLifecycleOwner) {
                    val list = when (it) {
                        AssetObjectType.UNDEFINED, AssetObjectType.CORE, AssetObjectType.UIA -> listOf(Tabs.INFO, Tabs.RAW)
                        AssetObjectType.MPA, AssetObjectType.PREDICTION -> Tabs.values().toList()
                    }
                    attachTabs(list)
                    post {
                        attachViewPager2(nextView()) }
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