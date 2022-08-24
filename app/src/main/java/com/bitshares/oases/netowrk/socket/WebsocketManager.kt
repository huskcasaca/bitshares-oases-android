package com.bitshares.oases.netowrk.socket

import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.asFlow
import com.bitshares.oases.MainApplication
import com.bitshares.oases.database.entities.nodeConfigAreEquivalent
import com.bitshares.oases.provider.local_repo.BitsharesNodeRepository
import graphene.app.API
import graphene.rpc.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.serialization.json.JsonArray
import modulon.extensions.stdlib.logcat
import kotlin.time.Duration.Companion.seconds

//class WebsocketManager(
//    private val application: MainApplication
//) : AllBroadcaster {
//
//    private val repo = BitsharesNodeRepository
//    private val websocketScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
//    override val broadcastScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
////    private val fallbackChannel = Channel<BroadcastStruct>(Channel.UNLIMITED)
//
//    private val client = MutableStateFlow(GrapheneClient { })
//    val state = client.flatMapLatest { it.state }.distinctUntilChanged()
//    val config = client.map { it.configuration }
//
//    private val waiting = Channel<Any?>()
//    private suspend fun awaitConnection() = waiting.send(null)
//
//    private var lastSession: Job = Job()
//    private suspend fun startClient(newClient: GrapheneClient = client.value, lazyStart: Boolean = true) {
//        var lazy = lazyStart
//        "startClient $newClient LazyStart $lazy".logcat()
//        val tempSession = lastSession
//            if (client.value.state.value == GrapheneClient.State.Closed) {
//                lazy = false
//            } else {
//                if (client.value == newClient && newClient.state.last().let { it == GrapheneClient.State.Connected || it == GrapheneClient.State.Connecting }) {
//                    return
//                }
//            }
//        if (!lazy) {
//            tempSession.cancelAndJoin()
//            client.emit(newClient)
//        }
//        lastSession = websocketScope.launch {
//            launch { newClient.start() }
//            delay(3.seconds)
//            withTimeout(10.seconds) { newClient.awaitConnection() }
//            if (lazy) {
//                tempSession.cancelAndJoin()
//                client.emit(newClient)
//            }
//            while (isActive) { waiting.receive() }
//        }.apply { invokeOnCompletion { websocketScope.launch { tempSession.cancelAndJoin() } } }
//    }
//
//    private suspend fun collectLatestConfig() {
//        application.preferenceManager.NODE_ID.asFlow().flatMapLatest {
//            repo.getAsync(it)
//        }.filterNotNull(
//
//        ).distinctUntilChanged(nodeConfigAreEquivalent).collectLatest {
//            startClient(it.toClient(), true)
//        }
//    }
//
//    override suspend fun broadcast(method: API, params: JsonArray): SocketResult {
//        if (client.value.state.value == GrapheneClient.State.Closed) {
//            startClient()
//        }
//        awaitConnection()
//        return withTimeout(10.seconds) {
//            client.value.broadcast(method, params)
//        }
//    }
//
//    init {
//        application.connectivityManager?.registerDefaultNetworkCallback(
//            object : ConnectivityManager.NetworkCallback() {
//                override fun onAvailable(network: Network) {
//                    websocketScope.launch { startClient() }
//                }
//            }
//        )
//        websocketScope.launch { collectLatestConfig() }
//    }
//
//}