package com.bitshares.oases.ui.settings.network

import android.app.Application
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.bitshares.oases.chain.globalDatabaseScope
import com.bitshares.oases.database.entities.BitsharesNode
import com.bitshares.oases.database.entities.nodeConfigAreEquivalent
import com.bitshares.oases.globalPreferenceManager
import com.bitshares.oases.netowrk.java_websocket.StabledMovingAverage
import com.bitshares.oases.provider.local_repo.BitsharesNodeRepository
import com.bitshares.oases.ui.base.BaseViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.util.network.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.Clock
import modulon.extensions.stdlib.logcat
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

//data class WebsocketState(
//    val state: GrapheneClient.State,
//    val selected: Long,
//    val config: GrapheneClientConfig
//)

//class NodeListViewModel(
//    application: Application
//) : BaseViewModel(application) {
//
//    private val repo = BitsharesNodeRepository
//
//    val websocketState = combine(globalWebsocketManager.state, globalPreferenceManager.NODE_ID.asFlow(), globalWebsocketManager.config) { state, id, config ->
//        WebsocketState(state, id, config)
//    }.distinctUntilChanged()
//
//    val isAutoSelect = globalPreferenceManager.AUTO_SELECT_NODE
//    val nodes = BitsharesNodeRepository.getListAsync()
//
//    fun setAutoSelect(newValue: Boolean = !isAutoSelect.value) {
//        isAutoSelect.value = newValue
//    }
//
//    fun switch(node: BitsharesNode) {
//        globalPreferenceManager.NODE_ID.value = node.id
//    }
//
//    private val httpClient = HttpClient(CIO.create()) {
//        install(WebSockets)
//        install(ContentNegotiation)
//    }
//
//    private var pingerTask: Job = Job()
//    fun startPinger() {
//        val temp = pingerTask
//        pingerTask = viewModelScope.launch(Dispatchers.IO + SupervisorJob()) {
//            temp.cancelAndJoin()
//            repo.getListAsync().distinctUntilChanged { old, new ->
//                old.size == new.size && old.indices.all { nodeConfigAreEquivalent(old[it], new[it]) }
//            }.collectLatest {
//                it.forEach {
//                    repo.updateLatency(it.id, BitsharesNode.LATENCY_CONNECTING)
//                    launch {
//                        val average = StabledMovingAverage()
//                        try {
//                            httpClient.wss(it.url) {
//                                while (isActive) {
//                                    val send = Clock.System.now()
//                                    send(byteArrayOf())
//                                    incoming.receive()
//                                    val receive = Clock.System.now()
//                                    average.update((receive - send).toDouble(DurationUnit.MILLISECONDS))
//                                    globalDatabaseScope.launch {
//                                        repo.updateLatency(it.id, average.value.roundToLong())
//                                    }
//                                    "Node Latency #${it.id} ${it.name.padEnd(12).take(12)} Latency ${average.value}ms".logcat()
//                                    delay(10.seconds)
//                                }
//                                ensureActive()
//                            }
//                        } catch (e: Throwable) {
//                            cancel("", e)
//                        }
//                    }.invokeOnCompletion { e ->
//                        "Node Latency #${it.id} ${it.name.padEnd(12).take(12)} Reason $e ${e?.cause}".logcat()
//                        globalDatabaseScope.launch {
//                            when (e?.cause) {
//                                is ConnectTimeoutException -> repo.updateLatency(it.id, BitsharesNode.LATENCY_TIMEOUT)
//                                is ClosedReceiveChannelException -> repo.updateLatency(it.id, BitsharesNode.LATENCY_TIMEOUT)
//                                is UnresolvedAddressException -> repo.updateLatency(it.id, BitsharesNode.LATENCY_UNRESOLVED)
//                                else -> repo.updateLatency(it.id, BitsharesNode.LATENCY_UNKNOWN)
//                            }
//                        }
//
//                    }
//                }
//            }
//        }
//    }
//
//    init {
//        startPinger()
//    }
//
//}
