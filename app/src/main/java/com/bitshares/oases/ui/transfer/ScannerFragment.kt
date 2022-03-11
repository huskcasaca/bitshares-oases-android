package com.bitshares.oases.ui.transfer

import android.os.Bundle
import android.view.View
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.extensions.viewbinder.cell
import modulon.layout.recycler.section

class ScannerFragment : ContainerFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            titleConnectionState("Scan")
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