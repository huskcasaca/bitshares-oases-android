package com.bitshares.oases.ui.main.liquid

import androidx.fragment.app.activityViewModels
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.main.MainViewModel
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.recyclerLayout
import modulon.extensions.viewbinder.tabLayout
import modulon.layout.lazy.section
import modulon.component.tab.tab

class LiquidPoolFragment : ContainerFragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView() {
        setupVertical {
            tabLayout {
                tab { text = "Pool" }
            }
            recyclerLayout {
                section {
                    cell {
                        title = "COMING SOON"
                    }
                }
                logo()
            }
        }
    }


}