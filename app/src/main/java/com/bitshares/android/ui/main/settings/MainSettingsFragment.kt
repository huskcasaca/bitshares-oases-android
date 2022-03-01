package com.bitshares.android.ui.main.settings

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import bitshareskit.extensions.nameOrEmpty
import com.bitshares.android.R
import com.bitshares.android.database.entities.User
import com.bitshares.android.extensions.compat.*
import com.bitshares.android.extensions.viewbinder.bindAccountV3
import com.bitshares.android.extensions.viewbinder.bindUserDrawer
import com.bitshares.android.extensions.viewbinder.logo
import com.bitshares.android.extensions.viewbinder.setDrawerItemStyle
import com.bitshares.android.preference.AppConfig
import com.bitshares.android.preference.old.Settings
import com.bitshares.android.ui.about.AboutFragment
import com.bitshares.android.ui.account.importer.ImportFragment
import com.bitshares.android.ui.base.ContainerFragment
import com.bitshares.android.ui.base.*
import com.bitshares.android.ui.intro.IntroFragment
import com.bitshares.android.ui.main.MainViewModel
import com.bitshares.android.ui.main.drawer.DrawerFragment
import com.bitshares.android.ui.settings.SettingsViewModel
import com.bitshares.android.ui.settings.appearance.AppearanceSettingsFragment
import com.bitshares.android.ui.settings.node.NodeSettingsFragment
import com.bitshares.android.ui.settings.showLanguageSettingDialog
import com.bitshares.android.ui.settings.storage.StorageSettingsFragment
import com.bitshares.android.ui.testlab.TestLabFragment
import com.bitshares.android.ui.wallet.WalletSettingsFragment
import com.bitshares.android.ui.wallet.showUserOptionDialog
import com.bitshares.android.ui.wallet.showUserSwitchDialog
import modulon.component.ComponentCell
import modulon.component.IconSize
import modulon.component.buttonStyle
import modulon.extensions.compat.startUriBrowser
import modulon.extensions.livedata.distinctUntilChangedBy
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.layout.recycler.*
import modulon.layout.recycler.containers.submitList

class MainSettingsFragment : ContainerFragment() {

    private val mainViewModel: MainViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler {
            section {
                cell {
                    mainViewModel.userAccount.distinctUntilChangedBy { it.nameOrEmpty }.observe(viewLifecycleOwner) {
                        if (it != null) {
                            bindAccountV3(it, true, IconSize.COMPONENT_1)
                            doOnThrottledClick { startAccountBrowser(it.uid) }
                        }
                    }
                }
                mainViewModel.userAccount.distinctUntilChangedBy { it.nameOrEmpty }.observe(viewLifecycleOwner) {
                    isVisible = it != null
                }
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
                isVisible = false
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
                isVisible = false
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
                    title = context.getString(R.string.language_settings_title)
//                    subtext = "Languages and locals"
                    icon = R.drawable.ic_cell_language.contextDrawable()
                    doOnThrottledClick { showLanguageSettingDialog() }
                }
            }
            section {
                header = "Help"
                cell {
                    title = context.getString(R.string.drawer_help)
                    icon = R.drawable.ic_cell_help.contextDrawable()
                    doOnThrottledClick {
                        startUriBrowser(AppConfig.HELP_URL.toUri())
                        mainViewModel.closeDrawer()
                    }
                }
                cell {
                    title = context.getString(R.string.drawer_about)
                    icon = R.drawable.ic_cell_about.contextDrawable()
                    doOnThrottledClick {
                        startFragment<AboutFragment>()
                        mainViewModel.closeDrawer()
                    }
                }
                cell {
                    title = context.getString(R.string.drawer_test_lab)
                    icon = R.drawable.ic_cell_test_lab.contextDrawable()
                    doOnThrottledClick {
                        startFragment<TestLabFragment>()
                        mainViewModel.closeDrawer()
                    }
                }
            }
            logo()
        }
    }

}