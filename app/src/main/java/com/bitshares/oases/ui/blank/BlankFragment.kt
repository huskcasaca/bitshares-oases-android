package com.bitshares.oases.ui.blank

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.ui.base.ContainerFragment
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