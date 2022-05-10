package com.bitshares.oases.ui.account.whitelist

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.AccountObject
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.startAccountBrowser
import com.bitshares.oases.extensions.viewbinder.bindAccountV1
import com.bitshares.oases.extensions.viewbinder.bindAccountV3
import com.bitshares.oases.ui.account.whitelist.WhitelistFragment.Tabs
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.startAccountPicker
import modulon.component.cell.ComponentCell
import modulon.component.cell.IconSize
import modulon.component.cell.buttonStyle
import modulon.dialog.button
import modulon.dialog.section
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.view.updatePaddingVerticalV6
import modulon.extensions.viewbinder.cell
import modulon.layout.lazy.*

class WhitelistTabFragment : ContainerFragment() {

    private val tab by lazy { requireArguments().getSerializable(IntentParameters.KEY_TAB_TYPE) as Tabs }

    private val viewModel: WhitelistViewModel by activityViewModels()

    override fun onCreateView() {

        setupRecycler {
            when (tab) {
                Tabs.BLACKLISTED, Tabs.WHITELISTED -> {
                    section {
                        val liveList = if (tab == Tabs.BLACKLISTED) viewModel.blacklistChanged else viewModel.whitelistChanged
                        list<ComponentCell, AccountObject> {
                            construct { updatePaddingVerticalHalf() }
                            data {
                                bindAccountV1(it, false, IconSize.COMPONENT_0)
                                doOnClick { startAccountBrowser(it.uid) }
                                doOnLongClick { showUserRemovalDialog(it) }
                            }
                            distinctItemsBy { it.uid }
                            liveList.observe(viewLifecycleOwner) { adapter.submitList(it.toList()) }
                        }
                        liveList.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                    }
                    section {
                        cell {
                            buttonStyle()
                            title = context.getString(R.string.whitelist_settings_add_account_button)
                            doOnClick {
                                startAccountPicker {
                                    if (it != null) {
                                        if (tab == Tabs.BLACKLISTED) viewModel.addBlacklistedAccount(it) else viewModel.addWhitelistedAccount(it)
                                    }
                                }
                            }
                        }
                    }
                }
                Tabs.BLACKLISTING, Tabs.WHITELISTING -> {
                    section {
                        val liveList = if (tab == Tabs.BLACKLISTING) viewModel.blacklisting else viewModel.whitelisting
                        list<ComponentCell, AccountObject> {
                            construct { updatePaddingVerticalHalf() }
                            data {
                                bindAccountV3(it, false, IconSize.COMPONENT_0)
                                doOnClick { startAccountBrowser(it.uid) }
                            }
                            distinctItemsBy { it.uid }
                            liveList.observe(viewLifecycleOwner) { adapter.submitList(it.toList()) }
                        }
                        liveList.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                    }
                }
            }
        }
    }

    private fun showUserRemovalDialog(account: AccountObject) = showBottomDialog {
        val viewModel: WhitelistViewModel by activityViewModels()
        title = context.getString(R.string.whitelist_settings_remove_account_title)
        isCancelableByButtons = true
        message = context.getString(if (tab == Tabs.BLACKLISTED) R.string.whitelist_settings_remove_whitelist_message else R.string.whitelist_settings_remove_whitelist_message)
        section {
            cell {
                bindAccountV3(account, false, IconSize.COMPONENT_0)
                updatePaddingVerticalV6()
            }
        }
        button {
            text = context.getString(R.string.button_remove)
            textColor = context.getColor(R.color.component_error)
            doOnClick { if (tab == Tabs.BLACKLISTED) viewModel.removeBlacklistedAccount(account) else viewModel.removeWhitelistedAccount(account) }
        }
        button { text = context.getString(R.string.button_cancel) }
    }


}