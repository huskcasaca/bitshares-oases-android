package com.bitshares.oases.netowrk.socket

import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import com.bitshares.oases.MainApplication
import com.bitshares.oases.database.entities.nodeConfigAreEquivalent
import com.bitshares.oases.database.entities.toClient
import com.bitshares.oases.provider.local_repo.BitsharesNodeRepository
import graphene.app.API
import graphene.rpc.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.JsonArray
import modulon.extensions.stdlib.logcat
import kotlin.time.Duration.Companion.seconds


class WebsocketManager(
    private val application: MainApplication
) : AllBroadcaster {

    private val repo = BitsharesNodeRepository
    private val websocketScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    override val broadcastScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
//    private val fallbackChannel = Channel<BroadcastStruct>(Channel.UNLIMITED)

    init {
        application.connectivityManager?.registerDefaultNetworkCallback(
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    websocketScope.launch {
                        startClient()
                    }
                }
            }
        )
        websocketScope.launch {
            application.preferenceManager.NODE_ID.asFlow().flatMapLatest {
                repo.getAsync(it)
            }.distinctUntilChanged(nodeConfigAreEquivalent).collectLatest {
                startClient(it.toClient(), true)
            }
        }
    }

    private val client = MutableStateFlow(GrapheneClient { })
    val state = client.flatMapLatest { it.state }
    val config = client.map { it.configuration }

    // TODO: 2022/4/16 remove livedatas
    @Deprecated("use state") val stateLive = state.asLiveData()
    @Deprecated("use config") val configLive = config.asLiveData()

    private val waiting = Channel<Any?>()
    private suspend fun awaitConnection() = waiting.send(null)

    private var lastSession: Job = Job()
    private suspend fun startClient(newClient: GrapheneClient = client.value, lazy: Boolean = true) {
        "startClient $newClient LazyStart $lazy".logcat()
        val tempSession = lastSession
            if (client.value == newClient) {
                if (newClient.state.last().let { it == GrapheneClient.State.CONNECTED || it == GrapheneClient.State.CONNECTING }) {
                    return
                }
            } else {

            }
        if (!lazy) {
            tempSession.cancelAndJoin()
            client.emit(newClient)
        }
        lastSession = websocketScope.launch {
            launch { newClient.start() }
            delay(3.seconds)
            withTimeout(10.seconds) { newClient.awaitConnection() }
            if (lazy) {
                tempSession.cancelAndJoin()
                client.emit(newClient)
            }
            while (isActive) { waiting.receive() }
            awaitCancellation()
        }.apply { invokeOnCompletion { websocketScope.launch { tempSession.cancelAndJoin() } } }
    }

    override suspend fun broadcast(method: API, params: JsonArray): SocketResult {
        if (client.value.state.value == GrapheneClient.State.CLOSED) {
            startClient()
        }
        awaitConnection()
        return withTimeout(10.seconds) {
            client.value.broadcast(method, params)
        }
    }

}