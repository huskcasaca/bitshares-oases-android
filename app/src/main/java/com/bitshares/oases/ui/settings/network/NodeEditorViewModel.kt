package com.bitshares.oases.ui.settings.network

import android.app.Application
import com.bitshares.oases.chain.globalDatabaseScope
import com.bitshares.oases.database.entities.BitsharesNode
import com.bitshares.oases.provider.local_repo.BitsharesNodeRepository
import com.bitshares.oases.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import modulon.extensions.livedata.mutableLiveDataOf

class NodeEditorViewModel(application: Application) : BaseViewModel(application) {

    private val repo = BitsharesNodeRepository

    // node dialog
    // TODO: 2022/4/16
    val nodeInEdit = mutableLiveDataOf(BitsharesNode())
    val isUriInvalid = mutableLiveDataOf(false)

    var nameField: String = ""
    var uriField: String = ""
        set(value) {
            field = value
            isUriInvalid.value = false
        }
    var usernameField: String = ""
    var passwordField: String = ""


    fun edit(node: BitsharesNode?) {
        nodeInEdit.value = node ?: BitsharesNode()
    }

    fun save(): Boolean {
        uriField = uriField.replace(Regex("\\s+"), "")
        if (!uriField.matches(WSS_ADDRESS_PATTERN)) {
            isUriInvalid.value = true
            return false
        }
        val node = nodeInEdit.value.copy(
            name = nameField,
            url = uriField,
            username = usernameField,
            password = passwordField,
            latency = Long.MAX_VALUE,
            lastUpdate = Long.MAX_VALUE
        )
        globalDatabaseScope.launch {
            if (repo.get(node.id) != null) {
                repo.update(node)
            } else {
                repo.add(node)
//                withContext(Dispatchers.Main) { Settings.KEY_CURRENT_NODE_ID.value =  }
            }
        }
        isUriInvalid.value = false
        return true
    }

    fun remove() {
        val node = nodeInEdit.value
        if (node.id == 0L) return
        globalDatabaseScope.launch {
            repo.remove(node)
//            if (Settings.KEY_CURRENT_NODE_ID.value == node.id) {
//                Settings.KEY_CURRENT_NODE_ID.value = NodeRepository.getList().find { it.chainId == ChainConfig.Chain.CHAIN_ID_MAIN_NET }?.id ?: ChainConfig.EMPTY_INSTANCE.toInt()
//            }
        }
    }

}
