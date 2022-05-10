package com.bitshares.oases.ui.account.picker

import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.AccountObject
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.chain.accountNameFilter
import com.bitshares.oases.database.entities.User
import com.bitshares.oases.database.entities.toAccount
import com.bitshares.oases.extensions.viewbinder.bindAccountV3
import com.bitshares.oases.extensions.viewbinder.bindUserV3
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.putJson
import modulon.component.cell.ComponentCell
import modulon.component.cell.IconSize
import modulon.component.cell.buttonStyle
import modulon.extensions.compat.finishActivity
import modulon.extensions.view.*
import modulon.extensions.viewbinder.*
import modulon.component.appbar.SearchView
import modulon.component.appbar.menu
import modulon.layout.lazy.*
import modulon.component.tab.tab

class AccountPickerFragment : ContainerFragment() {

    enum class Tabs(override val stringRes: Int) : StringResTabs {
        HISTORY(R.string.account_picker_search_history),
        LOCAL(R.string.account_picker_local_users),
        WHITELIST(R.string.account_picker_whitelist),
    }

    private val viewModel: AccountPickerViewModel by activityViewModels()

    override fun onCreateView() {
        setupAction {
            titleConnectionState(getString(R.string.account_picker_title))
            actionView = create<SearchView> {
                queryHint = context.getString(R.string.account_picker_search)
                fieldtextView.apply {
                    doAfterTextChanged {
                        viewModel.lookup(it.toString())
                    }
                    inputType = EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    filters = arrayOf(accountNameFilter)
                }
            }
            expandActionView()
            menu {
                text = context.getString(R.string.account_picker_search_account)
                icon = R.drawable.ic_menu_add.contextDrawable()
                doOnClick {
                    expandActionView()
                    (actionView as SearchView).fieldtextView.requestFocus()
                }
            }
        }
        setupCoordinator {
            verticalLayout {
                layoutWidth = MATCH_PARENT
                layoutHeight = MATCH_PARENT
                tabLayout {
                    viewModel.availableTabs.observe {
                        removeAllTabs()
                        it.forEach {
                            tab { text = context.getString(it.stringRes) }
                        }
                        attachViewPager2(nextView())
                    }
                }
                pagerLayout {
                    pageList<LazyListView, Tabs> {
                        construct {
                            layoutWidth = MATCH_PARENT
                            layoutHeight = MATCH_PARENT
                        }
                        // FIXME: 8/12/2021 potential memory leak
                        data {
                            removeAllViews()
                            when (it) {
                                Tabs.HISTORY -> {
                                    section {
                                        list<ComponentCell, AccountObject> {
                                            construct { updatePaddingVerticalHalf() }
                                            data {
                                                bindAccountV3(it, false, IconSize.COMPONENT_0)
                                                doOnClick {
                                                    finishActivity {
                                                        putJson(IntentParameters.Account.KEY_ACCOUNT, it)
                                                        putJson(IntentParameters.Account.KEY_UID, it.uid)
                                                    }
                                                }
                                            }
                                            viewModel.historyAccounts.observe(viewLifecycleOwner) { submitList(it) }
                                        }
                                        viewModel.historyAccounts.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                                    }
                                    section {
                                        cell {
                                            buttonStyle()
                                            title = context.getString(R.string.account_picker_clear_history)
                                            doOnClick { viewModel.clearSearchHistory() }
                                        }
                                    }
                                }
                                Tabs.LOCAL -> {
                                    section {
                                        list<ComponentCell, User> {
                                            construct { updatePaddingVerticalHalf() }
                                            data {
                                                bindUserV3(it, IconSize.COMPONENT_0)
                                                doOnClick {
                                                    finishActivity {
                                                        putJson(IntentParameters.Account.KEY_ACCOUNT, it.toAccount())
                                                        putJson(IntentParameters.Account.KEY_UID, it.uid)
                                                    }
                                                }
                                            }
                                            viewModel.localAccounts.observe(viewLifecycleOwner) { submitList(it) }
                                        }
                                        viewModel.localAccounts.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                                    }
                                }
                                Tabs.WHITELIST -> {
                                    section {
                                        list<ComponentCell, AccountObject> {
                                            construct { updatePaddingVerticalHalf() }
                                            data {
                                                bindAccountV3(it, false, IconSize.COMPONENT_0)
                                                doOnClick {
                                                    finishActivity {
                                                        putJson(IntentParameters.Account.KEY_ACCOUNT, it)
                                                        putJson(IntentParameters.Account.KEY_UID, it.uid)
                                                    }
                                                }
                                            }
                                            viewModel.whitelistAccounts.observe(viewLifecycleOwner) { submitList(it) }
                                        }
                                        viewModel.whitelistAccounts.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                                    }
                                }
                            }
                        }
                        viewModel.availableTabs.observe { submitList(it) }
                    }
                }
                viewModel.searchState.observe { isVisible = it == AccountPickerViewModel.STATE_HISTORY_SHOWN }
            }
            recyclerLayout {
                layoutWidth = MATCH_PARENT
                layoutHeight = MATCH_PARENT
                isVisible = false
                viewModel.searchState.observe { isVisible = it != AccountPickerViewModel.STATE_HISTORY_SHOWN }
                section {
                    list<ComponentCell, AccountObject> {
                        construct { updatePaddingVerticalHalf() }
                        data {
                            bindAccountV3(it, false, IconSize.COMPONENT_0)
                            doOnClick {
                                viewModel.addSearchHistory(it)
                                finishActivity {
                                    putJson(IntentParameters.Account.KEY_ACCOUNT, it)
                                    putJson(IntentParameters.Account.KEY_UID, it.uid)
                                }
                            }
                        }
                        viewModel.searchResult.observe(viewLifecycleOwner) { submitList(it) }
                    }
                    viewModel.searchResult.observe(viewLifecycleOwner) { isVisible = it.isNotEmpty() }
                }

                section {
                    cell {
                        text = context.getString(R.string.account_picker_searching)
                        subtext = context.getString(R.string.account_picker_searching_info)
                    }
                    isVisible = false
                    viewModel.searchState.observe(viewLifecycleOwner) {
                        isVisible = it == AccountPickerViewModel.STATE_LOOKING_UP
                    }
                }
                section {
                    cell {
                        text = context.getString(R.string.account_picker_not_found)
                        subtext = context.getString(R.string.account_picker_not_found_info)
                    }
                    isVisible = false
                    viewModel.searchState.observe(viewLifecycleOwner) {
                        isVisible = it == AccountPickerViewModel.STATE_NOT_FOUND
                    }
                }
                section {
                    cell {
                        text = context.getString(R.string.account_picker_no_connection)
                        subtext = context.getString(R.string.connection_state_no_connection_info)
                    }
                    isVisible = false
                    viewModel.searchState.observe(viewLifecycleOwner) {
                        isVisible = it == AccountPickerViewModel.STATE_NO_CONNECTION
                    }
                }
            }
        }
    }

}