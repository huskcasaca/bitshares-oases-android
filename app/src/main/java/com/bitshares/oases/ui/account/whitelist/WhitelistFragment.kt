package com.bitshares.oases.ui.account.whitelist

import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.map
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.extensions.compat.showChangesDiscardDialog
import com.bitshares.oases.extensions.text.createAccountSpan
import com.bitshares.oases.extensions.viewbinder.bindAccountV1
import com.bitshares.oases.extensions.viewbinder.feeCell
import com.bitshares.oases.ui.base.ContainerFragment
import modulon.component.appbar.subtitle
import com.bitshares.oases.ui.transaction.bindTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modulon.component.cell.IconSize
import modulon.dialog.section
import modulon.extensions.compat.arguments
import modulon.extensions.compat.finishActivity
import modulon.extensions.compat.showBottomDialog
import modulon.extensions.livedata.observeNonNull
import modulon.extensions.text.appendItem
import modulon.extensions.text.appendTag
import modulon.extensions.text.buildContextSpannedString
import modulon.extensions.view.*
import modulon.extensions.viewbinder.cell
import modulon.extensions.viewbinder.pagerLayout
import modulon.extensions.viewbinder.tabLayout
import modulon.extensions.viewbinder.verticalLayout
import java.util.*

class WhitelistFragment : ContainerFragment() {

    enum class Tabs(override val stringRes: Int): StringResTabs {
        BLACKLISTED(R.string.whitelist_settings_tab_blacklisted),
        WHITELISTED(R.string.whitelist_settings_tab_whitelisted),
        BLACKLISTING(R.string.whitelist_settings_tab_blacklisting),
        WHITELISTING(R.string.whitelist_settings_tab_whitelisting)
    }

    private val viewModel: WhitelistViewModel by activityViewModels()

    override fun onCreateView() {
        setupAction {
            titleConnectionState(context.getString(R.string.whitelist_settings_title))
            websocketStateMenu()
            walletStateMenu()
            broadcastMenu {
                text = context.getString(R.string.account_observe)
                contentDescription = ""
                doOnClick {
                    showChangeBlacklistDialog()
                }
                viewModel.isModified.observe(viewLifecycleOwner) {
                    isVisible = it
                    isClickable = it
                }
            }
            viewModel.accountName.observe(viewLifecycleOwner) { subtitle(it.toUpperCase(Locale.ROOT)) }
        }
        setupVertical {
            tabLayout {
                post { attachEnumsViewPager2<Tabs>(nextView()) }
            }
            pagerLayout {
                attachFragmentEnumsAdapter<Tabs> {
                    WhitelistTabFragment().arguments { putSerializable(IntentParameters.KEY_TAB_TYPE, it) }
                }
            }
        }
        doOnBackPressed {
            lifecycleScope.launch(Dispatchers.Main.immediate) {
                if (viewModel.isModified() && showChangesDiscardDialog(getString(R.string.whitelist_settings_changes_discard_message))) showChangeBlacklistDialog() else finishActivity()
            }
            false
        }
    }

    private fun showChangeBlacklistDialog() = showBottomDialog {
        bindTransaction(viewModel.buildTransaction(), viewModel)
        subtitle = context.getString(R.string.operation_account_whitelist_change)
        section {
            // TODO: 2022/2/22  remove verticalLayout
            verticalLayout {
                cell {
                    title = context.getString(R.string.transaction_creator)
                    viewModel.operation.map { it.account }.observe(viewLifecycleOwner) {
                        subtitle = createAccountSpan(it)
                    }
                }
                viewModel.transaction.observeNonNull(viewLifecycleOwner) {
                    removeAllViews()
                    val blacklistedToRemove = viewModel.blacklistedToRemove.value
                    val whitelistedToAppend = viewModel.whitelistedToAppend.value
                    val whitelistedToRemove = viewModel.whitelistedToRemove.value
                    val blacklistedToAppend = viewModel.blacklistedToAppend.value
                    cell {
                        updatePaddingVerticalV6()
                        title = context.getString(R.string.whitelist_settings_tab_blacklisted)
                        isVisible = (blacklistedToRemove + blacklistedToAppend).isNotEmpty()
                    }
                    blacklistedToAppend.forEach {
                        cell {
                            updatePaddingVerticalV6()
                            bindAccountV1(it, false, IconSize.COMPONENT_0)
                            title = buildContextSpannedString {
                                appendItem(title)
                                appendTag(getString(R.string.tag_new).toUpperCase(), context.getColor(R.color.tag_component), context.getColor(R.color.text_primary_inverted))
                            }
                        }
                    }
                    blacklistedToRemove.forEach {
                        cell {
                            updatePaddingVerticalV6()
                            bindAccountV1(it, false, IconSize.COMPONENT_0)
                            title = buildContextSpannedString {
                                appendItem(title)
                                appendTag(getString(R.string.tag_remove).toUpperCase(), context.getColor(R.color.tag_red), context.getColor(R.color.text_primary_inverted))
                            }
                        }
                    }
                    cell {
                        updatePaddingVerticalV6()
                        title = context.getString(R.string.whitelist_settings_tab_whitelisted)
                        isVisible = (whitelistedToRemove + whitelistedToAppend).isNotEmpty()
                    }
                    whitelistedToAppend.forEach {
                        cell {
                            updatePaddingVerticalV6()
                            bindAccountV1(it, false, IconSize.COMPONENT_0)
                            title = buildContextSpannedString {
                                appendItem(title)
                                appendTag(getString(R.string.tag_new).toUpperCase(), context.getColor(R.color.tag_component), context.getColor(R.color.text_primary_inverted))
                            }
                        }
                    }
                    whitelistedToRemove.forEach {
                        cell {
                            updatePaddingVerticalV6()
                            bindAccountV1(it, false, IconSize.COMPONENT_0)
                            title = buildContextSpannedString {
                                appendItem(title)
                                appendTag(getString(R.string.tag_remove).toUpperCase(), context.getColor(R.color.tag_red), context.getColor(R.color.text_primary_inverted))
                            }
                        }
                    }
                }
            }
            feeCell(union, viewModel.transactionBuilder)
        }
    }

}