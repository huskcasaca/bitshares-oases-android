package com.bitshares.oases.netowrk.java_websocket

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import bitshareskit.chain.ChainConfig
import bitshareskit.extensions.asOrNull
import bitshareskit.extensions.formatIdentifier
import bitshareskit.extensions.logcat
import bitshareskit.objects.AssetObject
import com.bitshares.oases.MainApplication
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.chain.blockchainNetworkScope
import com.bitshares.oases.database.BlockchainDatabase
import com.bitshares.oases.database.entities.Node
import com.bitshares.oases.provider.local_repo.LocalUserRepository
import com.bitshares.oases.provider.local_repo.NodeRepository
import bitshareskit.chain.CallMethod
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import modulon.extensions.coroutine.resumeSafe
import modulon.extensions.coroutine.throttleFirst
import modulon.extensions.coroutine.throttleLatest
import modulon.extensions.livedata.distinctUntilChangedBy
import modulon.extensions.livedata.withDefault
import org.java_json.JSONArray
import org.java_json.JSONObject
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import kotlin.concurrent.schedule
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

class SocketConnectionManager(private val application: MainApplication) : ActivityLifecycleCallbacks, NetworkCallback() {

    private val socketConnectionJob: CompletableJob = Job()

    val databaseScope get() = CoroutineScope(Dispatchers.IO + MainApplication.applicationJob + socketConnectionJob)
    val socketScope get() = CoroutineScope(Dispatchers.IO + MainApplication.applicationJob)

    // TODO: 13/9/2021 breaking changes for ConcurrentHashMap
    private val pingMap = ConcurrentHashMap<Int, GrapheneSocket>()
    private var pendingConnectionList = ConcurrentLinkedDeque<Continuation<GrapheneSocket>>()
    private var lastConnection: GrapheneSocket? = null
        set(value) {
            field = value
            if (value != null) {
                pendingConnectionList.forEach {
                    it.resumeSafe(value)
                }
                pendingConnectionList.clear()
                connectionLive.postValue(value)
            }
        }
    private val debounceNodeSwitcher = throttleFirst<Int>(blockchainDatabaseScope) {
        blockchainDatabaseScope.launch {
            val node = NodeRepository.getNode(it)
            CoroutineScope(Dispatchers.IO).launch { switch(node, "debounce") }
        }
    }
    private val onNodeChange = throttleLatest<Node>(CoroutineScope(Dispatchers.IO), 1000) {
        currentNodeId.value = it.id
    }

    private val currentNodeId = application.preferenceManager.CURRENT_NODE_ID
    private val autoSelectNode = application.preferenceManager.AUTO_SELECT_NODE
    private val nodeList = NodeRepository.getLiveList()

    val connectionLive = MutableLiveData<GrapheneSocket>()
    val connectionState = connectionLive.switchMap { it.stateLive }.withDefault { WebSocketState.CONNECTING }
    val isConnectedLive = connectionState.distinctUntilChangedBy { it == WebSocketState.CONNECTED || it == WebSocketState.MESSAGING }.map { it == WebSocketState.CONNECTED || it == WebSocketState.MESSAGING }.withDefault { false }
    val isConnected get() = isConnectedLive.value

    fun start() {
        isConnectedLive.observeForever { }
        currentNodeId.distinctUntilChanged().observeForever { debounceNodeSwitcher.invoke(it) }
        nodeList.observeForever { socketScope.launch { select(it) } }
        pingTask.run()
    }

    private val autoSelect get() = application.preferenceManager.AUTO_SELECT_NODE.valueSafe

    private val socketEmpty = GrapheneSocket(Node.EMPTY)

    private var isPending = false
    private var isPreSwitching = false

    @Synchronized
    private suspend fun switch(node: Node?, from: String = ""): GrapheneSocket {
        logcat("Synchronized switch from ${from} ${node?.url}")
        try {
            if (node == null) {
                lastConnection?.disconnect()
                lastConnection = null
            } else {
                isPending = true
                val socket = GrapheneSocket(node)
                socket.connect()
                val chainId = sendOrNull(socket, CallMethod.GET_CHAIN_ID) { it.asOrNull<String>() }
                val coreAsset = sendOrNull(socket, CallMethod.LOOKUP_ASSET_SYMBOLS, listOf(listOf(formatIdentifier<AssetObject>(ChainConfig.GLOBAL_INSTANCE)))) {
                    runCatching { AssetObject((it as JSONArray)[0] as JSONObject) }.onFailure { it.printStackTrace() }.getOrNull()
                }
                if (chainId != null && coreAsset != null) {
                    node.chainId = chainId
                    node.coreSymbol = coreAsset.symbol
                    blockchainDatabaseScope.launch {
                        NodeRepository.updateChainInfo(node)
                    }
                    if (!autoSelect || (autoSelect && chainId == ChainConfig.Chain.CHAIN_ID_MAIN_NET)) {
                        if (currentNodeId.value == node.id && !isPreSwitching) {
                            isPreSwitching = true
                            if (application.chainPreferenceManager.KEY_CHAIN_ID.value != chainId) {
                                BlockchainDatabase.INSTANCE.clearAllTables()
                            }
                            application.chainPreferenceManager.KEY_CHAIN_ID.value = chainId
                            LocalUserRepository.switchChain(chainId)
                            application.chainPreferenceManager.KEY_SYMBOL.value = coreAsset.symbol
                            application.chainPreferenceManager.KEY_CORE_ASSET.value = coreAsset
                            lastConnection = socket
                            isPending = false
                            isPreSwitching = false
                            return socket
                        }

                    }
                }
                isPending = false
            }
        } catch (e: Exception) {
            isPending = false
        }
        return socketEmpty
    }
    private suspend fun select(list: List<Node>) {
        coroutineScope {
            if (autoSelect) {
                var nodeToSwitch: Node? = null
                list.forEach { node ->
                    val latency = list.find { it.id == lastConnection?.node?.id }?.latency
                    if (node.latency in 1..3000 && (latency == null || (latency - node.latency > 100 || latency > node.latency * 0.8))) nodeToSwitch = node
                }
                nodeToSwitch?.let { onNodeChange(it) }
            }
        }
    }
    private suspend fun requireConnection(): GrapheneSocket {
        return lastConnection ?: suspendCoroutine {
            pendingConnectionList.add(it)
            if (!isPending) debounceNodeSwitcher.invoke(currentNodeId.value)
        }
    }

    suspend fun sendSubscribe(method: CallMethod, data: List<Any> = emptyList()): Flow<Any?> = requireConnection().sendSubscribe(method, data)
    suspend fun sendSuspend(method: CallMethod, data: List<Any> = emptyList()): Any? = requireConnection().sendOrNull(method, data)
    suspend fun sendOrThrow(method: CallMethod, data: List<Any> = emptyList()): Any? = requireConnection().sendOrThrow(method, data)

    suspend fun <T : Any?> sendOrThrow(method: CallMethod, data: List<Any> = listOf(), transform: suspend (Any?) -> T): T = transform.invoke(requireConnection().sendOrThrow(method, data))
    suspend fun <T : Any?> sendOrNull(method: CallMethod, data: List<Any> = listOf(), transform: suspend (Any?) -> T): T = transform.invoke(requireConnection().sendOrNull(method, data))
    suspend fun <T : Any?> sendSubscribe(method: CallMethod, data: List<Any> = listOf(), transform: suspend (Any?) -> T?): Flow<T?> = requireConnection().sendSubscribe(method, data).map { transform.invoke(it) }

    private suspend fun <T : Any?> sendOrThrow(socket: GrapheneSocket, method: CallMethod, data: List<Any> = listOf(), transform: suspend (Any?) -> T): T = transform.invoke(socket.sendOrThrow(method, data))
    private suspend fun <T : Any?> sendOrNull(socket: GrapheneSocket, method: CallMethod, data: List<Any> = listOf(), transform: suspend (Any?) -> T): T = transform.invoke(socket.sendOrNull(method, data))
    private suspend fun <T : Any?> sendSubscribe(socket: GrapheneSocket, method: CallMethod, data: List<Any> = listOf(), transform: suspend (Any?) -> T?): Flow<T?> = socket.sendSubscribe(method, data).map { transform.invoke(it) }

    fun <T : Any?> sendLive(method: CallMethod, data: List<Any> = listOf(), period: Long? = null, transform: (Any) -> T): GrapheneSocketLiveData<T> = GrapheneSocketLiveData(method, data, period, transform)

    private val pingJob = CoroutineScope(Dispatchers.IO + Job())

    fun startPinging() {
        pingJob.launch {
            stopPinging()
            NodeRepository.getList().forEach { node ->
                runCatching { GrapheneSocket(node, false).apply { connect() } }.onSuccess {
                    pingMap[node.id] = it
                }
            }

        }
    }

    private fun stopPinging() {
        pingMap.values.forEach { runCatching { it.disconnect() } }
        pingMap.clear()
    }

    private val pingTask = Timer().schedule(60000L) {
        stopPinging()
        startPinging()
    }

    private val reconnect = throttleLatest(blockchainNetworkScope, 1000) {
        runCatching {
            val last = lastConnection
            if (last != null && last.isClosed) last.reconnect()
        }
    }

    override fun onAvailable(network: Network) {
        reconnect()
        pingTask.run()
    }

    override fun onLost(network: Network) {
        pingTask.cancel()
        stopPinging()
    }

    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        reconnect()
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}

}

