package com.bitshares.oases.ui.settings.network

import android.app.Application
import androidx.lifecycle.asFlow
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.bitshares.oases.chain.globalDatabaseScope
import com.bitshares.oases.database.entities.BitsharesNode
import com.bitshares.oases.database.entities.nodeConfigAreEquivalent
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.globalWebsocketManager
import com.bitshares.oases.netowrk.java_websocket.StabledMovingAverage
import com.bitshares.oases.netowrk.java_websocket.WebSocketState
import com.bitshares.oases.provider.local_repo.BitsharesNodeRepository
import com.bitshares.oases.ui.base.BaseViewModel
import graphene.rpc.GrapheneClient
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.util.network.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.Clock
import modulon.extensions.livedata.*
import modulon.extensions.stdlib.logcat
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class NodeListViewModel(application: Application) : BaseViewModel(application) {

    private val repo = BitsharesNodeRepository

    val activeNodeConfig = globalWebsocketManager.configLive

    val selectedNodeId = globalPreferenceManager.NODE_ID
    val activeNodeId = combineNonNull(globalWebsocketManager.stateLive, globalWebsocketManager.configLive).map { (state, config) ->
        if (state == GrapheneClient.State.CLOSED) 0L else config.id
    }.distinctUntilChanged()


    val websocketState = globalWebsocketManager.stateLive.filterNotNull()

    val isAutoSelect = globalPreferenceManager.AUTO_SELECT_NODE
    val nodes = BitsharesNodeRepository.getListLive()

    fun setAutoSelect(newValue: Boolean = !isAutoSelect.value) {
        isAutoSelect.value = newValue
    }

    fun switch(node: BitsharesNode) {
        globalPreferenceManager.NODE_ID.value = node.id
    }

    private var lastPingingJob: Job = Job()

    private val client = HttpClient(CIO.create()) {
        install(WebSockets)
        install(ContentNegotiation)
    }

    val BitsharesNode.basicInfo
        get() = BasicNodeInfo(id, url, username, password)

    data class BasicNodeInfo(
        val id: Long,
        val url: String,
        val username: String,
        val password: String,
    )

    private val nodeListAreEquivalent: (List<BitsharesNode>, List<BitsharesNode>) -> Boolean = { old, new ->
        old.size == new.size && old.indices.all { nodeConfigAreEquivalent(old[it], new[it]) }
    }

    private var lastPingerSession: Job = Job()

    fun startPinger() {
        val temp = lastPingerSession
        lastPingerSession = viewModelScope.launch(Dispatchers.IO) {
            temp.cancelAndJoin()
            repo.getListAsync().distinctUntilChanged(nodeListAreEquivalent).collect {
                lastPingingJob.cancelAndJoin()
                lastPingingJob = viewModelScope.launch(Dispatchers.IO + SupervisorJob()) {
                    it.forEach {
                        repo.updateLatency(it.id, BitsharesNode.LATENCY_CONNECTING)
                        launch {
                            val average = StabledMovingAverage()
                            try {
                                client.wss(it.url) {
                                    while (isActive) {
                                        val send = Clock.System.now()
                                        send(byteArrayOf())
                                        incoming.receive()
                                        val receive = Clock.System.now()
                                        average.update((receive - send).toDouble(DurationUnit.MILLISECONDS))
                                        globalDatabaseScope.launch {
                                            repo.updateLatency(it.id, average.value.roundToLong())
                                        }
                                        "Node Latency #${it.id} ${it.name.padEnd(12).take(12)} Latency ${average.value}ms".logcat()
                                        delay(3.seconds)
                                    }
                                    ensureActive()
                                }
                            } catch (e: Throwable) {
                                cancel("", e)
                            }
                        }.invokeOnCompletion { e ->
                            "Node Latency #${it.id} ${it.name.padEnd(12).take(12)} Reason $e ${e?.cause}".logcat()
                            globalDatabaseScope.launch {
                                when (e?.cause) {
                                    is ConnectTimeoutException -> repo.updateLatency(it.id, BitsharesNode.LATENCY_TIMEOUT)
                                    is ClosedReceiveChannelException -> repo.updateLatency(it.id, BitsharesNode.LATENCY_TIMEOUT)
                                    is UnresolvedAddressException -> repo.updateLatency(it.id, BitsharesNode.LATENCY_UNRESOLVED)
                                    else -> repo.updateLatency(it.id, BitsharesNode.LATENCY_UNKNOWN)
                                }
                            }

                        }
                    }
                }
            }
        }


    }

    init {
//        startPinger()
    }

}
