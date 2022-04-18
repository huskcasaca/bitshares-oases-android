package com.bitshares.oases.netowrk.socket

import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.bitshares.oases.MainApplication
import com.bitshares.oases.database.entities.BitsharesNode
import com.bitshares.oases.netowrk.java_websocket.WebSocketState
import com.bitshares.oases.provider.local_repo.BitsharesNodeRepository
import graphene.app.API
import graphene.extension.info
import graphene.rpc.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.json.JsonArray
import modulon.extensions.livedata.*
import modulon.extensions.stdlib.logcat
import kotlin.time.Duration.Companion.seconds

class WebsocketManager(
    private val application: MainApplication
) : WebsocketManagerInternal() {

    override var stateInternal = state.value ?: WebSocketState.CLOSED
        set(value) {
            value.logcat()
            field = value
            (state as NonNullMutableLiveData<WebSocketState>).postValue(value)
        }

    init {
        application.connectivityManager?.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    startClient(clientInternal)
                }

            }
        )
        application.connectivityManager?.addDefaultNetworkActiveListener {
            "onNetworkActive".logcat()
        }
        application.preferenceManager.NODE_ID.switchMap(socketScope) {
            BitsharesNodeRepository.getLiveNode(it)
        }.filterNotNull().distinctUntilChangedBy {
            listOf(it.id, it.url, it.username, it.password)
        }.observeForever {
            switch(it)
        }
    }

}

open class WebsocketManagerInternal : AllBroadcaster {

    protected val socketScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    override val broadcastScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val fallbackChannel = Channel<BroadcastStruct>(Channel.UNLIMITED)
    private var lastSession: Job = Job()
    protected var clientInternal = GrapheneClient { }
        set(value) {
            field = value
            client.postValue(value)
        }

    private val waiting = Channel<Any?>()

    // TODO: 2022/4/16 remove livedatas
    val client = mutableLiveDataOf(clientInternal)
    val state: LiveData<WebSocketState> = mutableLiveDataOf(WebSocketState.CLOSED)
    val config = client.map { it.configuration }

    protected open var stateInternal = state.value ?: WebSocketState.CLOSED
        set(value) {
            value.info()
            field = value
        }

    private suspend fun waitForOpen() = waiting.send(null)

    fun switch(node: BitsharesNode) {
        GrapheneClient {
            id = node.id
            name = node.name
            url = node.url
            enableFallback = false
            debug = true
        }.also { startClient(it) }
    }


    private fun GrapheneClient.state(state: WebSocketState) {
        if (clientInternal == this) stateInternal = state
    }

    fun startClient(client: GrapheneClient, lazy: Boolean = false) {
        val tempSession = lastSession
        if (clientInternal == client) {
            if (stateInternal == WebSocketState.CONNECTING || stateInternal == WebSocketState.CONNECTED) {
                return
            }
        } else {
            clientInternal.stop()
            clientInternal = client
        }
        with(client) {
            state(WebSocketState.CONNECTING)
            lastSession = socketScope.launch {
                tempSession.cancelAndJoin()
                launch {
                    try {
                        launchSocket()
                    } catch (e: Throwable) {
                        // UnresolvedAddressException
                        // ClosedReceiveChannelException
                        // ConnectException
//                        throw e
                        e.logcat()
                    }
                    state(WebSocketState.CLOSED)
                }
                withTimeout(10_000) { waitForOpen() }
                state(WebSocketState.CONNECTED)
                while (isActive) { waiting.receive() }
            }.apply {
                invokeOnCompletion {
                    "Session Complete".info()
                    state(WebSocketState.CLOSED)
                }
            }
        }

    }

    private val TIMEOUT = 10.seconds

    override suspend fun broadcast(method: API, params: JsonArray): SocketResult {
        if (stateInternal == WebSocketState.CLOSED) {
            startClient(clientInternal)
        }
        waitForOpen()
        return withTimeout(TIMEOUT) {
            clientInternal.broadcast(method, params)
        }
    }

}