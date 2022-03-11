package com.bitshares.oases.ui.asset.picker

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import bitshareskit.objects.AssetObject
import com.bitshares.oases.R
import com.bitshares.oases.chain.IntentParameters
import com.bitshares.oases.chain.assetSymbolFilter
import com.bitshares.oases.extensions.viewbinder.bindAssetV3
import com.bitshares.oases.ui.base.ContainerFragment
import com.bitshares.oases.ui.base.putJson
import modulon.component.ComponentCell
import modulon.extensions.compat.finish
import modulon.extensions.compat.showSoftKeyboard
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.text.toStringOrEmpty
import modulon.extensions.view.create
import modulon.extensions.view.doOnClick
import modulon.layout.actionbar.SearchLayout
import modulon.layout.actionbar.menu
import modulon.layout.recycler.data
import modulon.layout.recycler.distinctItemsBy
import modulon.layout.recycler.list
import modulon.layout.recycler.section

class AssetPickerFragment : ContainerFragment() {

    private val viewModel: AssetPickerViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction {
            titleConnectionState("Asset Picker")
            actionView = create<SearchLayout> {
                queryHint = context.getString(R.string.account_picker_search)
                fieldtextView.apply {
                    inputType = EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or EditorInfo.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    filters = arrayOf(assetSymbolFilter)
                    doAfterTextChanged { viewModel.changeSearchField(it.toStringOrEmpty()) }
                }
            }
            expandActionView()
            menu {
                text = "SEARCH_ASSET"
                icon = R.drawable.ic_menu_add.contextDrawable()
                doOnClick {
                    expandActionView()
                    showSoftKeyboard()
                }
            }
        }
        setupRecycler {
            section {
                header = "History"
                list<ComponentCell, AssetObject> {
                    data {
                        bindAssetV3(it, true)
                        doOnClick {
                            finish {
                                putJson(IntentParameters.Asset.KEY_ASSET, it)
                                putJson(IntentParameters.Asset.KEY_UID, it.uid)
                            }
                        }
                    }
                    viewModel.searchHistoryInternal.observe(viewLifecycleOwner) { adapter.submitList(it) }
                    // TODO: 3/11/2021 add visibility?
                    viewModel.searchState.observe(viewLifecycleOwner) {
//                    isVisible = it == AssetPickerViewModel.State.EMPTY
                    }
                }
                list<ComponentCell, AssetObject> {
                    data {
                        bindAssetV3(it, true)
                        doOnClick {
                            viewModel.addAssetHistory(it)
                            finish {
                                putJson(IntentParameters.Asset.KEY_ASSET, it)
                                putJson(IntentParameters.Asset.KEY_UID, it.uid)
                            }
                        }
                    }
                    distinctItemsBy { it.uid }
                    viewModel.searchResult.observe(viewLifecycleOwner) { adapter.submitList(it) }
                }
                combineNonNull(viewModel.searchHistorySettings, viewModel.searchState).observe(viewLifecycleOwner) { (history, state) ->
                    isVisible = history.isNotEmpty() && state == AssetPickerViewModel.State.EMPTY
                }
            }
        }
    }


}