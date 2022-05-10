package com.bitshares.oases.ui.blank

import androidx.fragment.app.activityViewModels
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.layout.lazy.section

class BlankFragment : ContainerFragment() {

    private val viewModel: BlankViewModel by activityViewModels()

    override fun onCreateView() {
        setupAction {
            titleConnectionState("CONTAINER_TITLE")
            websocketStateMenu()
            walletStateMenu()
        }
        setupRecycler {
            section {

            }
        }
    }

}