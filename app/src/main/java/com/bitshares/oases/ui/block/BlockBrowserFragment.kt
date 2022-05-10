package com.bitshares.oases.ui.block

import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.raw_data.JsonRawDataFragment
import com.bitshares.oases.ui.raw_data.JsonRawDataViewModel
import modulon.extensions.view.StringResTabs
import modulon.extensions.view.attachEnumsViewPager2
import modulon.extensions.view.nextView
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout
import modulon.component.appbar.subtitle

class BlockBrowserFragment : ContainerFragment() {

    enum class Tabs(override val stringRes: Int): StringResTabs {
        INFO(R.string.block_browser_tab_basic_info),
        TRANSACTIONS(R.string.block_browser_tab_transactions),
        RAW(R.string.tab_raw_data),
    }

    private val viewModel: BlockViewModel by activityViewModels()
    private val rawViewModel: JsonRawDataViewModel by activityViewModels()

    override fun onCreateView() {
        setupAction {
            titleConnectionState(getString(R.string.block_browser_title))
            websocketStateMenu()
            walletStateMenu()
            viewModel.block.observe(viewLifecycleOwner) {
                subtitle(it.blockNum.toString())
                rawViewModel.setContent(it)
            }
        }
        setupVertical {
            tabLayout {
                post { attachEnumsViewPager2<Tabs>(nextView()) }
            }
            pagerLayout {
                attachFragmentEnumsAdapter<Tabs> {
                    when (it) {
                        Tabs.INFO -> BlockBrowserFragment_Info()
                        Tabs.TRANSACTIONS -> BlockBrowserFragment_Transactions()
                        Tabs.RAW -> JsonRawDataFragment()
                    }
                }
            }
        }

    }

}