package com.bitshares.oases.ui.main.explore

import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.AssetObject
import com.bitshares.oases.chain.assetSymbolFilter
import com.bitshares.oases.extensions.compat.startAssetBrowser
import com.bitshares.oases.extensions.viewbinder.bindAssetV3
import com.bitshares.oases.extensions.viewbinder.logo
import com.bitshares.oases.ui.account.picker.AccountPickerViewModel
import com.bitshares.oases.ui.account.voting.VotingViewModel
import com.bitshares.oases.ui.asset.picker.AssetPickerViewModel
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.main.MainViewModel
import modulon.component.cell.ComponentCell
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.doOnClick
import modulon.extensions.view.updatePaddingVerticalHalf
import modulon.extensions.viewbinder.cell
import modulon.layout.lazy.construct
import modulon.layout.lazy.data
import modulon.layout.lazy.list
import modulon.layout.lazy.section

class ExploreFragment_Asset : ContainerFragment() {

    private val exploreViewModel: ExploreViewModel by activityViewModels()
    private val votingViewModel: VotingViewModel by activityViewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val accountSearchingViewModel: AccountPickerViewModel by activityViewModels()
    private val assetSearchingViewModel: AssetPickerViewModel by activityViewModels()

    override fun onCreateView() {
        setupRecycler {
            section {
                header = "Search Asset"
                cell {
                    field {
                        inputType = EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                        filters = arrayOf(assetSymbolFilter)
                        doAfterTextChanged {
                            assetSearchingViewModel.searchFieldText = it.toStringOrEmpty()
                        }
                    }
                }
            }
            section {
                header = "Search Results"
                list<ComponentCell, AssetObject> {
                    construct { updatePaddingVerticalHalf() }
                    data {
                        bindAssetV3(it, true)
                        doOnClick { startAssetBrowser(it.uid) }
                    }
                    assetSearchingViewModel.searchResult.observe{ submitList(it) }
                }
                isVisible = false
                assetSearchingViewModel.searchResult.observe{ isVisible = it.isNotEmpty() }
            }
            logo()
        }
    }
}