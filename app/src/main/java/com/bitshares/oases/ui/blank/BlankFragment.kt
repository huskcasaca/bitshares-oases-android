package com.bitshares.oases.ui.blank

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.layout.recycler.section

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