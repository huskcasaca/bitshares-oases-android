package com.bitshares.oases.ui.main.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.layout.recycler.section

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