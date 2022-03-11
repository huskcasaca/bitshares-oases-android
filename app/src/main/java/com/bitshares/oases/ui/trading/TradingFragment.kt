package com.bitshares.oases.ui.trading

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.chain.Constants
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.component.toggleEnd
import modulon.dialog.section
import modulon.extensions.compat.arguments
import modulon.extensions.compat.recreate
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout
import modulon.layout.actionbar.menu
import modulon.layout.actionbar.subtitle
import modulon.layout.actionbar.title
import modulon.widget.doOnStepChanged

class TradingFragment : ContainerFragment() {

    enum class Tabs(override val stringRes: Int): StringResTabs {
        BUY(R.string.market_tab_buy),
        SELL(R.string.market_tab_sell),
        ORDERS(R.string.market_tab_orders),
    }

    private val viewModel: TradingViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            title(context.getString(R.string.market_title))
            networkStateMenu()
            walletStateMenu()
            menu {
                icon = R.drawable.ic_cell_settings.contextDrawable()
                doOnClick { showMarketSettingsDialog() }
            }
            viewModel.marketInternal.observe(viewLifecycleOwner) { subtitle(it.toString()) }
        }
        setupVertical {
            tabLayout {
                post { attachEnumsViewPager2<Tabs>(nextView()) }
            }
            pagerLayout {
                attachFragmentEnumsAdapter<Tabs> {
                    when (it) {
                        Tabs.BUY, Tabs.SELL -> TradingFragment_AskBid().arguments { putSerializable(IntentParameters.KEY_TAB_TYPE, it) }
                        Tabs.ORDERS -> TradingFragment_Orders()
                    }
                }
            }
        }
    }

    private fun showMarketSettingsDialog() = showBottomDialog {
        title = "Market Setting"
        section {
            cell {
                updatePaddingVerticalV6()
                title = "Price Precision"
                slider {
                    snapToStep = true
                    maxStep = Constants.Market.MAX_PRECISION
                    progressActiveColor = context.getColor(R.color.component)
                    thumbColor = context.getColor(R.color.component)
                    doOnStepChanged { step, fromUser ->
                        subtitle = step.toString()
                        if (fromUser) viewModel.precision.value = step
                    }
                    viewModel.precision.observe(viewLifecycleOwner) {
                        if (!isOnTouch) setStep(it, true)
                        subtitle = step.toString()
                    }
                }
            }
            cell {
                text = "Vertical Layout"
                toggleEnd {
                    setChecked(Settings.KEY_IS_VERTICAL_LAYOUT.value, false)
                    viewModel.isVerticalLayoutEnabled.observe(viewLifecycleOwner) {
                        setChecked(it, true)
                    }
                }
                doOnClick {
                    Settings.KEY_IS_VERTICAL_LAYOUT.value = !Settings.KEY_IS_VERTICAL_LAYOUT.value
                    dismissNow()
                    recreate()
                }
            }
        }

    }
}