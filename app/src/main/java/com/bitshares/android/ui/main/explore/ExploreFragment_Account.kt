package com.bitshares.android.ui.main.explore

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.AccountObject
import com.bitshares.android.chain.accountNameFilter
import com.bitshares.android.extensions.compat.startAccountBrowser
import com.bitshares.android.extensions.viewbinder.bindAccountV3
import com.bitshares.android.extensions.viewbinder.logo
import com.bitshares.android.ui.account.picker.AccountPickerViewModel
import com.bitshares.android.ui.account.voting.VotingViewModel
import com.bitshares.android.ui.asset.picker.AssetPickerViewModel
import com.bitshares.android.ui.base.ContainerFragment
import com.bitshares.android.ui.base.*
import com.bitshares.android.ui.main.MainViewModel
import modulon.component.ComponentCell
import modulon.component.IconSize
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.doOnClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.layout.recycler.construct
import modulon.layout.recycler.data
import modulon.layout.recycler.list
import modulon.layout.recycler.section

class ExploreFragment_Account : ContainerFragment() {

    private val exploreViewModel: ExploreViewModel by activityViewModels()
    private val votingViewModel: VotingViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val accountSearchingViewModel: AccountPickerViewModel by activityViewModels()
    private val assetSearchingViewModel: AssetPickerViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler {
            section {
                header = "Search Account"
                cell {
                    field {
                        doAfterTextChanged {
                            accountSearchingViewModel.lookup(it.toStringOrEmpty())
                        }
                        inputType = EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                        filters = arrayOf(accountNameFilter)
                    }
                }
            }
            section {
                header = "Search Results"
                list<ComponentCell, AccountObject> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        bindAccountV3(it, true, IconSize.COMPONENT_0)
                        doOnClick { startAccountBrowser(it.uid) }
                    }
                    accountSearchingViewModel.searchResult.observe{ adapter.submitList(it) }
                }
                isVisible = false
                accountSearchingViewModel.searchResult.observe{ isVisible = it.isNotEmpty() }
            }
            logo()
        }
    }
}