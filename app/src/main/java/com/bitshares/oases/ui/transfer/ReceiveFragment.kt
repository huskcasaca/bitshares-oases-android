package com.bitshares.oases.ui.transfer

import com.bitshares.oases.ui.base.ContainerFragment
import modulon.extensions.viewbinder.cell
import modulon.layout.lazy.section

class ReceiveFragment : ContainerFragment() {

    override fun onCreateView() {
        setupAction {
            titleConnectionState("Receive")
            websocketStateMenu()
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