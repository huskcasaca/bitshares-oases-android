package com.bitshares.android.ui.blank

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bitshares.android.ui.base.ContainerFragment
import com.bitshares.android.ui.base.*
import modulon.layout.recycler.section

class BlankFragment : ContainerFragment() {

    private val viewModel: BlankViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            titleConnectionState("CONTAINER_TITLE")
            networkStateMenu()
            walletStateMenu()
        }
        setupRecycler {
            section {

            }
        }
    }

}