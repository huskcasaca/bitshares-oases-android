package com.bitshares.oases.ui.account.importer

import android.view.ViewGroup
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.startRegister
import com.bitshares.oases.ui.asset.browser.actionBarLayout
import com.bitshares.oases.ui.asset.browser.actionCoordinatorParams
import com.bitshares.oases.ui.asset.browser.bodyCoordinatorParams
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.extensions.compat.arguments
import modulon.extensions.compat.finishActivity
import modulon.extensions.compat.secureWindow
import modulon.extensions.view.*
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout
import modulon.extensions.viewbinder.verticalLayout
import modulon.component.appbar.menu

class ImportFragment : ContainerFragment() {

    enum class Tabs(override val stringRes: Int, override val iconRes: Int): StringResTabs, DrawableResTabs {
        CLOUD(R.string.import_cloud_mode, R.drawable.ic_tab_cloud_mode),
        WIF(R.string.import_private_key_mode, R.drawable.ic_tab_private_key_mode),
        BRAIN(R.string.import_brain_key_mode, R.drawable.ic_tab_brain_key_mode),
        BIN(R.string.import_restore_mode, R.drawable.ic_tab_restore_mode),
    }

    override fun ViewGroup.onCreateView() {
        secureWindow()
        actionBarLayout {
            layoutParams = actionCoordinatorParams()
            titleConnectionState(context.getString(R.string.import_title))
            websocketStateMenu()
            walletStateMenu()
            menu {
                icon = R.drawable.ic_menu_add.contextDrawable()
                doOnClick {
                    startRegister()
                    finishActivity()
                }
            }
        }
        verticalLayout {
            layoutParams = bodyCoordinatorParams()
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

