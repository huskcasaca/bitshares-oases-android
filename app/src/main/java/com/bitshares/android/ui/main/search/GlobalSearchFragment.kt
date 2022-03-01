package com.bitshares.android.ui.main.search

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bitshares.android.ui.base.ContainerFragment
import com.bitshares.android.ui.base.*
import modulon.layout.recycler.section

class GlobalSearchFragment : ContainerFragment() {

    private val viewModel: GlobalSearchViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            titleConnectionState("Global Search")
            networkStateMenu()
            walletStateMenu()
        }
        setupRecycler {
            section {

            }
        }
    }

}