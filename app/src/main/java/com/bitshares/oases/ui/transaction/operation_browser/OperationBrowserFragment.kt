package com.bitshares.oases.ui.transaction.operation_browser

import android.os.Bundle
import android.view.View
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

class OperationBrowserFragment : ContainerFragment() {

    enum class Tabs(override val stringRes: Int): StringResTabs {
        INFO(R.string.operation_browser_tab_basic_info),
        RAW(R.string.tab_raw_data),
    }

    private val viewModel: OperationViewModel by activityViewModels()
    private val rawViewModel: JsonRawDataViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            titleConnectionState(context.getString(R.string.operation_browser_title))
            networkStateMenu()
            walletStateMenu()
        }
        setupVertical {
            tabLayout {
                post { attachEnumsViewPager2<Tabs>(nextView()) }
            }
            pagerLayout {
                attachFragmentEnumsAdapter<Tabs> {
                    when (it) {
                        Tabs.INFO -> OperationInfoFragment()
                        Tabs.RAW -> JsonRawDataFragment()
                    }
                }
            }
        }
        viewModel.operation.observe(viewLifecycleOwner) {
            rawViewModel.setContent(it)
        }
    }

}