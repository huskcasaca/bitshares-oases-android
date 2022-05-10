package com.bitshares.oases.ui.main.search

import androidx.fragment.app.activityViewModels
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.layout.lazy.section

class GlobalSearchFragment : ContainerFragment() {

    private val viewModel: GlobalSearchViewModel by activityViewModels()

    override fun onCreateView() {
        setupAction {
            titleConnectionState("Global Search")
            websocketStateMenu()
            walletStateMenu()
        }
        setupRecycler {
            section {

            }
        }
    }

}