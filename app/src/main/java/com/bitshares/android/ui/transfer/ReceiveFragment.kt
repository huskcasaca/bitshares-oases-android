package com.bitshares.android.ui.transfer

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bitshares.android.ui.base.ContainerFragment
import com.bitshares.android.ui.base.*
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