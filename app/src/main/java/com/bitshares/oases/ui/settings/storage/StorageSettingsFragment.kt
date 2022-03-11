package com.bitshares.oases.ui.settings.storage

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.settings.SettingsViewModel
import com.bitshares.oases.ui.settings.showClearBlockchainCachesDialog
import modulon.extensions.view.doOnClick
import modulon.extensions.viewbinder.cell
import modulon.layout.actionbar.title
import modulon.layout.recycler.section
import kotlin.math.roundToInt

class StorageSettingsFragment : ContainerFragment() {

    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            title(getString(R.string.storage_settings_title))
            networkStateMenu()
            walletStateMenu()
        }
        setupRecycler {
            section {
                cell {
                    title = context.getString(R.string.storage_settings_clear_blockchain_caches_button)
                    viewModel.blockchainDBSize.observe(viewLifecycleOwner) {
                        subtitle = "${(it / 1024f).roundToInt()} KB"
                    }
                    doOnClick { showClearBlockchainCachesDialog() }
                }
            }
        }
    }

}