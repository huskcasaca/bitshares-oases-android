package com.bitshares.oases.ui.asset.picker

import android.app.Application
import androidx.lifecycle.viewModelScope
import bitshareskit.objects.AssetObject
import com.bitshares.oases.preference.AppConfig
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.provider.chain_repo.AssetRepository
import com.bitshares.oases.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.coroutine.debounce
import modulon.extensions.livedata.NonNullMutableLiveData
import modulon.extensions.livedata.filterChildNotNull
import modulon.extensions.livedata.mapChildParallel

class AssetPickerViewModel(application: Application) : BaseViewModel(application) {

    enum class State {
        EMPTY, SEARCHING, COMPLETE, NO_CONNECTION
    }

    val searchState = NonNullMutableLiveData(State.EMPTY)

    val searchHistorySettings = (if (AppConfig.ENABLE_ASSET_HISTORY) Settings.KEY_ASSET_SEARCH_HISTORY else Settings.KEY_ASSET_SEARCH_HISTORY.default)
    val searchHistoryInternal = searchHistorySettings.mapChildParallel(viewModelScope) { AssetRepository.getAsset(it.uid) }.filterChildNotNull()

    val searchResult = NonNullMutableLiveData(emptyList<AssetObject>())
    private var searchField = EMPTY_SPACE
    private val changeSearchField = debounce(viewModelScope) { nameOrId: String ->
        if (nameOrId != EMPTY_SPACE) {
            searchState.value = State.SEARCHING
            viewModelScope.launch {
                val result = AssetRepository.lookupAssetSymbols(nameOrId)
                if (nameOrId == searchField) withContext(Dispatchers.Main) {
                    searchResult.value = result
//                        .filter { it.isSmartcoin() || it.isPrediction() }
                    searchState.value = State.COMPLETE
                }
            }
        } else {
            searchState.value = State.EMPTY
            searchResult.value = emptyList()
        }
    }

    fun changeSearchField(text: String) {
        searchField = text
        changeSearchField.invoke(text)
    }

    var searchFieldText: String
        get() = searchField
        set(text) {
            searchField = text
            changeSearchField.invoke(text)
        }

    fun resetField() {
        searchField = EMPTY_SPACE
        searchState.value = State.EMPTY
        searchResult.value = emptyList()
    }

    fun addAssetHistory(asset: AssetObject) {
        if (asset.isExist) Settings.KEY_ASSET_SEARCH_HISTORY.value = Settings.KEY_ASSET_SEARCH_HISTORY.value + asset
    }

}