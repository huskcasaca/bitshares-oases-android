package com.bitshares.oases.ui.settings.storage

import androidx.fragment.app.activityViewModels
import com.bitshares.oases.R
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.settings.SettingsViewModel
import com.bitshares.oases.ui.settings.showClearBlockchainCachesDialog
import modulon.extensions.view.doOnClick
import modulon.extensions.viewbinder.cell
import modulon.component.appbar.title
import modulon.layout.lazy.section
import kotlin.math.roundToInt

class StorageSettingsFragment : ContainerFragment() {

    private val viewModel: SettingsViewModel by activityViewModels()

    override fun onCreateView() {
        setupAction {
            title(getString(R.string.storage_settings_title))
            websocketStateMenu()
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