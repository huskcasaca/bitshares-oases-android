package com.bitshares.oases.ui.account.browser

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.AccountObject
import com.bitshares.oases.R
import com.bitshares.oases.extensions.compat.showAccountBrowserDialog
import com.bitshares.oases.extensions.compat.startAccountBrowser
import com.bitshares.oases.extensions.viewbinder.bindAccountV3
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account.AccountViewModel
import com.bitshares.oases.ui.account.whitelist.WhitelistFragment.Tabs
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.component.ComponentCell
import modulon.component.IconSize
import modulon.extensions.view.doOnClick
import modulon.extensions.view.doOnLongClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.layout.recycler.*

class AccountBrowserFragment_Whitelist : ContainerFragment() {

    private val viewModel: AccountViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecycler {
            Tabs.values().forEach { type ->
                val source = when (type) {
                    Tabs.BLACKLISTED -> viewModel.blacklisted
                    Tabs.WHITELISTED -> viewModel.whitelisted
                    Tabs.BLACKLISTING -> viewModel.blacklisting
                    Tabs.WHITELISTING -> viewModel.whitelisting
                }
                section {
                    header = when (type) {
                        Tabs.BLACKLISTED -> context.getString(R.string.account_whitelist_tab_blacklisted)
                        Tabs.WHITELISTED -> context.getString(R.string.account_whitelist_tab_whitelisted)
                        Tabs.BLACKLISTING -> context.getString(R.string.account_whitelist_tab_blacklisting)
                        Tabs.WHITELISTING -> context.getString(R.string.account_whitelist_tab_whitelisting)
                    }
                    list<ComponentCell, AccountObject> {
                        construct {
                            updatePaddingVerticalHalf()
                        }
                        data { account ->
                            bindAccountV3(account, true, IconSize.COMPONENT_0)
                            doOnClick { startAccountBrowser(account.uid) }
                            doOnLongClick { showAccountBrowserDialog(account) }
                        }
                        distinctItemsBy { it.uid }
                        source.observe(viewLifecycleOwner) { adapter.submitList(it) }
                    }
                    source.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                }
            }
            logo()
        }
    }

}
