package com.bitshares.oases.ui.account.importer

import android.os.Bundle
import android.view.View
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.startRegister
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.extensions.compat.arguments
import modulon.extensions.compat.finish
import modulon.extensions.compat.secureWindow
import modulon.extensions.view.*
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout
import modulon.layout.actionbar.menu

class ImportFragment : ContainerFragment() {

    enum class Tabs(override val stringRes: Int, override val iconRes: Int): StringResTabs, DrawableResTabs {
        CLOUD(R.string.import_cloud_mode, R.drawable.ic_tab_cloud_mode),
        WIF(R.string.import_private_key_mode, R.drawable.ic_tab_private_key_mode),
        BRAIN(R.string.import_brain_key_mode, R.drawable.ic_tab_brain_key_mode),
        BIN(R.string.import_restore_mode, R.drawable.ic_tab_restore_mode),
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        secureWindow()
        setupAction {
            titleConnectionState(context.getString(R.string.import_title))
            networkStateMenu()
            walletStateMenu()
            menu {
                icon = R.drawable.ic_menu_add.contextDrawable()
                doOnClick {
                    startRegister()
                    finish()
                }
            }
        }
        setupVertical {
            tabLayout {
                post { attachEnumsViewPager2<Tabs>(nextView()) }
            }
            pagerLayout {
                attachFragmentEnumsAdapter<Tabs> {
                    ImportFragment_LoginTabs().arguments { putSerializable(IntentParameters.KEY_TAB_TYPE, it) }
                }
            }
        }
    }

}

