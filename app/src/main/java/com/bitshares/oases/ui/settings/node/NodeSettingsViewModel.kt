package com.bitshares.oases.ui.settings.node

import android.app.Application
import androidx.lifecycle.distinctUntilChanged
import bitshareskit.chain.ChainConfig
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.database.entities.Node
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.netowrk.java_websocket.NetworkService
import com.bitshares.oases.preference.old.Settings
import com.bitshares.oases.provider.local_repo.NodeRepository
import com.bitshares.oases.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import modulon.extensions.charset.EMPTY_SPACE
import modulon.extensions.livedata.NonNullMutableLiveData
import modulon.extensions.livedata.combineNonNull
import modulon.extensions.livedata.filterNotNull

class NodeSettingsViewModel(application: Application) : BaseViewModel(application) {

    val nodeList = NodeRepository.getLiveList()

    val nodeStateList = combineNonNull(NodeRepository.currentSelectedNodeId, NodeRepository.currentNodeId)

    val connectionState = NetworkService.connectionState.distinctUntilChanged().filterNotNull()

    val autoSelect = globalPreferenceManager.AUTO_SELECT_NODE

    fun addNode(node: Node) {
        blockchainDatabaseScope.launch {
            if (NodeRepository.getNode(node.id) != null) {
                NodeRepository.add(node)
            } else {
                withContext(Dispatchers.Main) { Settings.KEY_CURRENT_NODE_ID.value = NodeRepository.add(node).toInt() }
            }
        }
    }

    fun removeNode(node: Node) {
        blockchainDatabaseScope.launch {
            NodeRepository.remove(node)
            if (Settings.KEY_CURRENT_NODE_ID.value == node.id) {
                Settings.KEY_CURRENT_NODE_ID.value = NodeRepository.getList().find { it.chainId == ChainConfig.Chain.CHAIN_ID_MAIN_NET }?.id ?: ChainConfig.EMPTY_INSTANCE.toInt()
            }
        }
    }

    fun switchNode(node: Node) {
        Settings.KEY_CURRENT_NODE_ID.value = node.id
    }

    fun setAutoSelect(enabled: Boolean = !globalPreferenceManager.AUTO_SELECT_NODE.value) {
        globalPreferenceManager.AUTO_SELECT_NODE.value = enabled
    }

    val isUriFieldError = NonNullMutableLiveData(false)

    // node dialog
    val currentNodeInEdit = NonNullMutableLiveData(Node.EMPTY)

    var nodeNameField = EMPTY_SPACE
    var nodeUriField = EMPTY_SPACE
        set(value) {
            field = value
            isUriFieldError.value = false
        }
    var nodeUsernameField = EMPTY_SPACE
    var nodePasswordField = EMPTY_SPACE

    fun setCurrentEditNode(node: Node?) {
        currentNodeInEdit.value = node ?: Node.EMPTY
    }

    fun saveCurrentNode(): Boolean {
        nodeUriField = nodeUriField.replace(Regex("\\s+"), "")
        return if (nodeUriField.matches(WEBSOCKET_ADDRESS_PATTERN)) {
            currentNodeInEdit.value.apply {
                name = nodeNameField
                url = nodeUriField
                username = nodeUsernameField
                password = nodePasswordField
                latency = Node.LATENCY_CONNECTING
                addNode(this)
            }
            isUriFieldError.value = false
            true
        } else {
            isUriFieldError.value = true
            false
        }

    }


}
