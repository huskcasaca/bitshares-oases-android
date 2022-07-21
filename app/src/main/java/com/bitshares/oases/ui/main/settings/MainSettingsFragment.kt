package com.bitshares.oases.ui.main.settings

import android.view.Gravity
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.extensions.nameOrEmpty
import bitshareskit.objects.AccountObject
import com.bitshares.oases.R
import com.bitshares.oases.database.entities.User
import com.bitshares.oases.extensions.compat.*
import com.bitshares.oases.extensions.viewbinder.bindAccountV3
import com.bitshares.oases.extensions.viewbinder.bindUserDrawer
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.preference.AppConfig
import com.bitshares.oases.ui.about.AboutFragment
import com.bitshares.oases.ui.account.importer.ImportFragment
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.*
import com.bitshares.oases.ui.intro.IntroFragment
import com.bitshares.oases.ui.main.MainViewModel
import com.bitshares.oases.ui.settings.appearance.AppearanceSettingsFragment
import com.bitshares.oases.ui.settings.network.NetworkSettingsFragment
import com.bitshares.oases.ui.settings.node.NodeSettingsFragment
import com.bitshares.oases.ui.settings.storage.StorageSettingsFragment
import com.bitshares.oases.ui.testlab.TestLabFragment
import com.bitshares.oases.ui.testlab.testSettings
import com.bitshares.oases.ui.wallet.WalletSettingsFragment
import com.bitshares.oases.ui.wallet.showUserOptionDialog
import com.bitshares.oases.ui.wallet.showUserSwitchDialog
import modulon.component.cell.ComponentCell
import modulon.component.cell.IconSize
import modulon.component.cell.buttonStyle
import modulon.component.cell.toggleEnd
import modulon.extensions.compat.recreateActivity
import modulon.extensions.compat.startUriBrowser
import modulon.extensions.livedata.distinctUntilChangedBy
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.lazy.*

class MainSettingsFragment : ContainerFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun ViewGroup.onCreateView() {
        recyclerLayout {
            testSettings()
            section {
                cell {
                    bindAccountV3(AccountObject.EMPTY, true, IconSize.COMPONENT_1)
                    mainViewModel.userAccount.distinctUntilChangedBy { it.nameOrEmpty }.observe(viewLifecycleOwner) {
                        if (it != null) {
                            bindAccountV3(it, true, IconSize.COMPONENT_1)
                            doOnThrottledClick { startAccountBrowser(it.uid) }
                        }
                    }
                }
                // FIXME: 2022/5/3
//                mainViewModel.userAccount.distinctUntilChangedBy { it.nameOrEmpty }.observe(viewLifecycleOwner) {
//                    isVisible = it != null
//                }
            }
            section {
                cells {
                    construct {
                        iconView.apply {
                            layoutMarginStart = (-4).dp
                            layoutMarginTop = (-4).dp
                            layoutMarginEnd = (-4).dp + componentOffset
                            layoutMarginBottom = (-4).dp
                            layoutGravity = Gravity.START or Gravity.CENTER_VERTICAL
                        }
                    }
                    data {
                        val user = it as User
                        bindUserDrawer(user, IconSize.SMALL)
                        doOnClick {
                            showUserSwitchDialog(user)
                        }
                        doOnLongClick { showUserOptionDialog(user) }
                    }
                    mainViewModel.users.observe(viewLifecycleOwner) {
                        submitList(it.toList())
                    }
                }
                cell {
                    buttonStyle()
                    title = "Add Account"
//                    icon = R.drawable.ic_cell_add_account.contextDrawable()
                    doOnClick { startFragment<ImportFragment>() }
                }
            }
            section {
                header = "Menu"
                cell {
                    title = context.getString(R.string.drawer_transfer)
                    icon = R.drawable.ic_cell_transfer.contextDrawable()
//                    doOnThrottledClick
                    mainViewModel.userCurrent.observe {
                        doOnClick { if (it != null) startTransferFrom(it.uid) else startImport() }
                    }
                }
                cell {
                    title = context.getString(R.string.drawer_collateral)
                    icon = R.drawable.ic_cell_collateral.contextDrawable()
                    mainViewModel.userCurrent.observe {
                        doOnClick { if (it != null) startMarginPosition(it.uid) else startImport() }
                    }
                }
                cell {
                    title = context.getString(R.string.drawer_voting)
                    icon = R.drawable.ic_cell_voting.contextDrawable()
                    mainViewModel.userCurrent.observe {
                        doOnClick { if (it != null) startVoting(it.uid) else startImport() }
                    }
                }
                cell {
                    title = context.getString(R.string.drawer_proposed_transactions)
                    icon = R.drawable.ic_cell_proposal_manage.contextDrawable()
                    mainViewModel.userCurrent.observe {
                        doOnClick { if (it != null) startImport() else startImport() }
                    }
                }
                mainViewModel.userCurrent.observe { isVisible = it != null }
            }
            section {
                header = context.getString(R.string.settings_account_title)
                cell {
                    title = context.getString(R.string.whitelist_setting_title)
                    icon = R.drawable.ic_cell_whitelist.contextDrawable()
                    mainViewModel.userCurrent.observe {
                        doOnClick { if (it != null) startWhitelist(it.uid) else startImport() }
                    }
                }
                cell {
                    title = context.getString(R.string.permission_settings_title)
                    icon = R.drawable.ic_cell_authority.contextDrawable()
                    mainViewModel.userCurrent.observe {
                        doOnClick { if (it != null) startPermission(it.uid) else startImport() }
                    }
                }
                cell {
                    title = context.getString(R.string.drawer_membership)
                    icon = R.drawable.ic_cell_membership.contextDrawable()
                    mainViewModel.userCurrent.observe {
                        doOnClick { if (it != null) startMembership(it.uid) else startImport() }
                    }
                }
                mainViewModel.userCurrent.observe { isVisible = it != null }
            }
            section {
                header = context.getString(R.string.settings_default_title)
                cell {
                    title = context.getString(R.string.appearance_settings_title)
//                    subtext = context.getString(R.string.appearance_settings_hint)
                    icon = R.drawable.ic_cell_appearance.contextDrawable()
                    doOnThrottledClick { startFragment<AppearanceSettingsFragment>() }
                }
                cell {
                    title = context.getString(R.string.wallet_settings_title)
//                    subtext = context.getString(R.string.wallet_settings_hint)
                    icon = R.drawable.ic_cell_wallet.contextDrawable()
                    doOnThrottledClick { startFragment<WalletSettingsFragment>() }
                }
                cell {
                    title = context.getString(R.string.storage_settings_title)
//                    subtext = "Local databases and blockchain caches"
                    icon = R.drawable.ic_cell_database.contextDrawable()
                    doOnThrottledClick { startFragment<StorageSettingsFragment>() }
                }
                cell {
                    title = context.getString(R.string.node_settings_title)
//                    subtext = context.getString(R.string.node_settings_hint)
                    icon = R.drawable.ic_cell_nodes.contextDrawable()
                    doOnThrottledClick { startFragment<NodeSettingsFragment>() }
                }
                cell {
                    text = "Ktor" + context.getString(R.string.node_settings_title)
//                    subtext = context.getString(R.string.node_settings_hint)
                    icon = R.drawable.ic_cell_nodes.contextDrawable()
                    doOnThrottledClick { startFragment<NetworkSettingsFragment>() }
                }
                cell {
                    title = context.getString(R.string.language_settings_title)
//                    subtext = "Languages and locals"
                    icon = R.drawable.ic_cell_language.contextDrawable()
                    doOnThrottledClick { startLanguageSettingDialog() }
                }
            }
            section {
                header = "Help"
                cell {
                    title = context.getString(R.string.drawer_help)
                    icon = R.drawable.ic_cell_help.contextDrawable()
                    doOnThrottledClick {
                        startUriBrowser(AppConfig.HELP_URL.toUri())
//                        mainViewModel.closeDrawer()
                    }
                }
                cell {
                    title = context.getString(R.string.drawer_about)
                    icon = R.drawable.ic_cell_about.contextDrawable()
                    doOnThrottledClick {
                        startFragment<AboutFragment>()
//                        mainViewModel.closeDrawer()
                    }
                }
                cell {
                    title = context.getString(R.string.drawer_test_lab)
                    icon = R.drawable.ic_cell_test_lab.contextDrawable()
                    doOnThrottledClick {
                        startFragment<TestLabFragment>()
//                        mainViewModel.closeDrawer()
                    }
                }
            }
            logo()
        }
    }

}