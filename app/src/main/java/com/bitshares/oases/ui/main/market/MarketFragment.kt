package com.bitshares.oases.ui.main.market

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import bitshareskit.objects.AssetObject
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.text.createAssetName
import com.bitshares.oases.extensions.viewbinder.bindMarketGroup
import com.bitshares.oases.preference.AppConfig
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.dialog.button
import modulon.dialog.doOnDismiss
import modulon.dialog.section
import modulon.extensions.compat.arguments
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.stdlib.logcat
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.nestedScrollableHost
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout
import modulon.component.tab.tab

class MarketFragment : ContainerFragment() {

    private val viewModel: MarketViewModel by activityViewModels()

    override fun onCreateView() {
        setupVertical {
            tabLayout {
                viewModel.marketGroupInternal.observe(viewLifecycleOwner) {
                    removeAllTabs()
                    "removeAllTabs()".logcat()
                    tab { text = context.getString(R.string.market_all_groups) }
                    it.forEach {
                        tab {
                            text = "${createAssetName(it)} Market"
                            doOnLongClick { if (AppConfig.ENABLE_MARKET_GROUP_MODIFICATION) showRemoveMarketDialog(it) }
                        }
                    }
                    post { attachViewPager2(parentViewGroup.getChildAt<ViewGroup>(1).getChildAt<ViewPager2>(0)) }
                    if (AppConfig.ENABLE_MARKET_GROUP_MODIFICATION) {
                        tab {
                            icon = R.drawable.ic_cell_add_account.contextDrawable()
//                            text = context.getString(R.string.market_add_market_group)
                            doOnClick { showAddMarketGroupDialog() }
                        }
                    }
                }
                viewModel.selectedMarket.observe(viewLifecycleOwner) { selectTab(it) }
            }
            nestedScrollableHost {
                layoutWidth = MATCH_PARENT
                layoutHeight = MATCH_PARENT
                pagerLayout {
                    offscreenPageLimit = 2
                    viewModel.selectedMarket.observe { currentItem = it }
                    viewModel.marketGroupInternal.observe(viewLifecycleOwner) {
                        attachFragmentListAdapter(listOf(AssetObject.EMPTY) + it) {
                            MarketFragment_Internal().arguments { putSerializable(IntentParameters.KEY_ASSET_UID, it.uid) }
                        }
                    }
                }
            }
        }
    }

    // TODO: 27/1/2022 extract to union
    //  apply to all
    private fun showRemoveMarketDialog(asset: AssetObject) = showBottomDialog {
        title = context.getString(R.string.market_remove_market_group_dialog_title)
        message = context.getString(R.string.market_remove_market_group_message, createAssetName(asset))
        isCancelableByButtons = true
        section {
            cell {
                updatePaddingVerticalV6()
                bindMarketGroup(asset)
            }
        }
        button {
            text = context.getString(R.string.button_remove)
            textColor = context.getColor(R.color.component_error)
            doOnClick { viewModel.removeMarketGroup(asset) }
        }
        doOnDismiss { viewModel.resetField() }
    }
}

