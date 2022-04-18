package com.bitshares.oases.ui.settings.network

import android.app.Application
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.bitshares.oases.database.entities.BitsharesNode
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.globalWebsocketManager
import com.bitshares.oases.provider.local_repo.BitsharesNodeRepository
import com.bitshares.oases.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import modulon.extensions.livedata.*

class NetworkSettingsViewModel(application: Application) : BaseViewModel(application) {

    init {
        startPinging()
    }

    val selectedNode = globalPreferenceManager.NODE_ID.map(viewModelScope) { BitsharesNodeRepository.getNode(it) }
    val activeNodeConfig = globalWebsocketManager.config

    val selectedNodeId = globalPreferenceManager.NODE_ID
    val activeNodeId = globalWebsocketManager.config.map { it.id }.distinctUntilChanged()

    val websocketState = globalWebsocketManager.state.filterNotNull()

    val isAutoSelect = globalPreferenceManager.AUTO_SELECT_NODE
    val nodes = BitsharesNodeRepository.getLiveList()

    fun setAutoSelect(newValue: Boolean = !isAutoSelect.value) {
        isAutoSelect.value = newValue
    }

    fun switchNode(node: BitsharesNode) {
        globalPreferenceManager.NODE_ID.value = node.id
    }

    fun startPinging() {
        viewModelScope.launch(Dispatchers.IO) {


        }
    }


}
