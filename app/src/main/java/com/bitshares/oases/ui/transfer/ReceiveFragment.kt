package com.bitshares.oases.ui.transfer

import android.os.Bundle
import android.view.View
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.extensions.viewbinder.cell
import modulon.layout.recycler.section

class ReceiveFragment : ContainerFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            titleConnectionState("Receive")
            networkStateMenu()
            walletStateMenu()
        }
        setupRecycler {
            section {
                cell {
                    title = "TODO"
                }
            }
        }
    }

}