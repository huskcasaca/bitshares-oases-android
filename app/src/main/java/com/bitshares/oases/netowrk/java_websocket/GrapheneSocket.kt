package com.bitshares.oases.netowrk.java_websocket

import android.util.Log
import bitshareskit.errors.WebSocketClosedException
import bitshareskit.extensions.logcat
import bitshareskit.models.SocketCall
import bitshareskit.models.SocketResponse
import com.bitshares.oases.chain.blockchainDatabaseScope
import com.bitshares.oases.database.entities.Node
import com.bitshares.oases.preference.AppConfig
import com.bitshares.oases.provider.local_repo.NodeRepository
import bitshareskit.chain.BlockchainAPI
import bitshareskit.chain.CallMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch
import modulon.extensions.coroutine.mapParallel
import modulon.extensions.coroutine.resumeSafe
import modulon.extensions.coroutine.resumeWithExceptionSafe
import modulon.extensions.livedata.NonNullMutableLiveData
import modulon.extensions.livedata.throttleLatest
import org.java_json.JSONObject
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.framing.Framedata
import org.java_websocket.framing.PongFrame
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedDeque
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.schedule
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Suppress("UNCHECKED_CAST", "UNUSED", "MemberVisibilityCanBePrivate")
class GrapheneSocket(val node: Node, var login: Boolean = true) {

    companion object {
        const val TAG = "GrapheneSocket"

        private const val TICK_TIMER_START_DELAY = 1000L
        private const val TICK_TIMER_WAIT_PERIOD = 1000L
        private const val MAX_SEND_TICK_COUNT = 5
        private const val MAX_RECEIVE_TICK_COUNT = 10
        private const val WEBSOCKET_TIMEOUT = 10000

        private val apiList = mapOf(
//            CallMethod.LOGIN to ChainInterface.LOGIN,                           // 0x01
            CallMethod.BLOCK to BlockchainAPI.BLOCK,                           // 0x02
            CallMethod.NETWORK_BROADCAST to BlockchainAPI.NETWORK_BROADCAST,   // 0x03
            CallMethod.DATABASE to BlockchainAPI.DATABASE,                     // 0x04
            CallMethod.HISTORY to BlockchainAPI.HISTORY,                       // 0x05
            CallMethod.NETWORK_NODE to BlockchainAPI.NETWORK_NODE,             // 0x06
            CallMethod.CRYPTO to BlockchainAPI.CRYPTO,                         // 0x07
            CallMethod.ASSET to BlockchainAPI.ASSET,                           // 0x08
            CallMethod.ORDERS to BlockchainAPI.ORDERS                          // 0x09
        )

        const val SEQUENCE_BEGIN = -1
    }

    // FIXME: 2021/10/24      javax.net.ssl.SSLHandshakeException: Connection closed by peer
    //        at com.android.org.conscrypt.NativeCrypto.SSL_do_handshake(Native Method)
    //        at com.android.org.conscrypt.SslWrapper.doHandshake(SslWrapper.java:374)
    //        at com.android.org.conscrypt.ConscryptFileDescriptorSocket.startHandshake(ConscryptFileDescriptorSocket.java:217)
    //        at com.android.org.conscrypt.ConscryptFileDescriptorSocket.waitForHandshake(ConscryptFileDescriptorSocket.java:468)
    //        at com.android.org.conscrypt.ConscryptFileDescriptorSocket.getInputStream(ConscryptFileDescriptorSocket.java:431)
    //        at org.java_websocket.client.WebSocketClient.run(WebSocketClient.java:474)
    //        at java.lang.Thread.run(Thread.java:764)

    inner class Client(serverURI: URI, connectTimeout: Int) : WebSocketClient(serverURI, Draft_6455(), null, connectTimeout) {

        private lateinit var cont: Continuation<Unit>

        override fun onOpen(handshakedata: ServerHandshake) {
            if (webSocket.isOpen) cont.resumeSafe(Unit)
        }

        override fun onClose(code: Int, reason: String, remote: Boolean) {
            // FIXME: 2021/12/8 WebSocketClosedException: Websocket closed. Code 1006 Reason:
            cont.resumeWithExceptionSafe(WebSocketClosedException("Websocket closed."))
            onSocketClose(WebSocketClosedException("Websocket closed. Code $code Reason: $reason"))
        }

        override fun onError(e: Exception): Unit = onSocketClose(e)
        override fun onMessage(message: String): Unit = onSocketMessage(message)
        override fun onWebsocketPong(websocket: WebSocket, framedata: Framedata): Unit = onSocketMessage(framedata)

        suspend fun connectSuspend(): Unit = suspendCoroutine { cont ->
            this.cont = cont
            connect()
        }

    }

    private val socketJob = Job()
    private val socketScope = CoroutineScope(Dispatchers.IO + socketJob)

    private var state = WebSocketState.CLOSED

    private val stateLiveInternal = NonNullMutableLiveData(WebSocketState.CLOSED)
    val stateLive = stateLiveInternal.throttleLatest(socketScope)

    private var webSocket: Client = Client(URI(""), WEBSOCKET_TIMEOUT)

    private val atomicSequence = AtomicInteger(SEQUENCE_BEGIN)
    private var lastSequence = AtomicInteger(SEQUENCE_BEGIN)

    private val supportedApi = mutableMapOf<BlockchainAPI, Int>()

    private var pingTime: Long = 0
    private var pongTime: Long = Long.MAX_VALUE
    private var ponged: Boolean = true

    private val averageLatency = StabledMovingAverage()

    val isConnected: Boolean get() = state == WebSocketState.CONNECTED || state == WebSocketState.MESSAGING

    val isClosed: Boolean get() = state == WebSocketState.CLOSED

    fun reconnect() {
        if (isClosed) connect()
    }

    fun disconnect() = socketScope.launch {
//        if (!isClosed) onSocketClose(WebSocketClosedException("Manually disconnected."))
        if (!isClosed) webSocket.close()
    }

    val sendMap = ConcurrentHashMap<Int, Pair<Continuation<(Any?)>, Boolean>>()
    val subscribeMap = ConcurrentHashMap<Int, ProducerScope<Any?>>()
    val pendingList = ConcurrentLinkedDeque<Continuation<Boolean>>()

    // TODO: remove
    private suspend fun waitForLogin(): Boolean = if (isConnected) true else suspendCoroutine { pendingList.add(it) }

    suspend fun sendOrNull(method: CallMethod, params: List<Any>): Any? = sendSuspend(method, params, false)
    suspend fun sendOrThrow(method: CallMethod, params: List<Any>): Any? = sendSuspend(method, params, true)

    private suspend fun sendSuspend(method: CallMethod, params: List<Any> = emptyList(), throws: Boolean, wait: Boolean = true): Any? {
        if (wait) {
            if (!waitForLogin()) return null
        }
        val call = SocketCall(method, params)
        return suspendCoroutine { cont ->
            runCatching {
                call.apply {
                    id = atomicSequence.getAndIncrement()
                    apiId = supportedApi[call.api]!!
                }
                val data = call.toJsonElement().toString()
                sendMap[call.id] = cont to throws
                webSocket.send(data)
                checkWebSocketState()
                if (AppConfig.ENABLE_MESSAGE_LOG) Log.i(TAG, ">>> $data")
            }.onFailure {
                it.printStackTrace()
                runCatching { if (throws) cont.resumeWithException(it) else cont.resume(null) }
            }
        }
    }

    suspend fun sendSubscribe(method: CallMethod, params: List<Any>): Flow<Any?> {
        waitForLogin()
        val call = SocketCall(method, params, true)
        return channelFlow<Any?> {
            runCatching {
                call.apply {
                    id = atomicSequence.getAndIncrement()
                    apiId = supportedApi[call.api]!!
                }
                val data = call.toJsonElement().toString()
//                callMap[call.id] = call
                subscribeMap[call.id] = this@channelFlow
                webSocket.send(data)
                checkWebSocketState()
                Log.i(TAG, ">>> $data")
            }.onFailure {
                it.printStackTrace()
                close(it)
            }
            awaitClose()
        }
    }

    @Synchronized
    fun connect() {
        logcat(">>> >>> >>> START CONNECTING >>> >>> >>> ")
        changeState(WebSocketState.CONNECTING)
        socketScope.launch {
            updateNodeLatency(Node.LATENCY_CONNECTING)
            atomicSequence.set(0)
            sendMap.clear()
            subscribeMap.clear()
            supportedApi.clear()
            runCatching {
                webSocket = Client(URI(node.url), WEBSOCKET_TIMEOUT)
                webSocket.connectSuspend()
            }.onSuccess { onSocketOpen() }
        }

    }

    var timer = Timer()

    private fun startPinging() {
        timer = Timer()
        timer.schedule(1000, 9000) {
            if (isConnected) {
                pingTime = System.currentTimeMillis()
                runCatching { webSocket.sendPing() }
            }
        }
    }

    private fun stopPinging() {
        runCatching { timer.cancel() }
        pingTime = 0
        pongTime = Long.MAX_VALUE
    }

    // Called multi times
    private suspend fun onSocketOpen() {
        startPinging()
        if (login) {
            supportedApi[BlockchainAPI.LOGIN] = 1
            runCatching { sendSuspend(CallMethod.LOGIN, listOf(node.username, node.password), throws = true, wait = false) }.onFailure {
                it.printStackTrace()
//                onSocketClose(it)
                webSocket.close()
            }.onSuccess { onLoggedIn() }
        } else {
            changeState(WebSocketState.CONNECTED)
        }
    }

    private suspend fun onLoggedIn() {
        supportedApi.putAll(apiList.keys.mapParallel(socketScope.coroutineContext) {
            val index = sendSuspend(it, throws = false, wait = false)
            if (index is Int && index != 0) apiList[it]!! to index else null
        }.filterNotNull().toMap())
        Log.i(TAG, "Node ${node.url} API List: $supportedApi")
        if (supportedApi.size > 1) {
            node.apis = List(4) { index ->
                when (index) {
                    0 -> supportedApi.keys.contains(BlockchainAPI.NETWORK_BROADCAST)
                    1 -> supportedApi.keys.contains(BlockchainAPI.DATABASE)
                    2 -> supportedApi.keys.contains(BlockchainAPI.HISTORY)
                    3 -> supportedApi.keys.contains(BlockchainAPI.ORDERS)
                    else -> throw IndexOutOfBoundsException()
                }
            }
            updateNodeApis()
            changeState(WebSocketState.CONNECTED)
            pendingList.forEachIndexed { index, cont ->
                cont.resume(true)
            }
            pendingList.clear()
        } else {
//            onSocketClose(GrapheneException(ErrorCode.NO_SUPPORTED_API))
            webSocket.close()
        }
    }

    private fun onSocketMessage(message: Any) {
        if (message is String && AppConfig.ENABLE_MESSAGE_LOG) Log.i(TAG, "<<< $message")
        val response = try {
            when (message) {
                is String -> SocketResponse.fromJson(JSONObject(message))
                is PongFrame -> {
                    pongTime = System.currentTimeMillis()
                    ponged = true
                    updateNodeLatency(averageLatency.update(pongTime - pingTime).toLong())
                    return
                }
                else -> SocketResponse.fromJson(message as JSONObject)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return
        }
        if (response.id >= lastSequence.get()) {
            lastSequence.set(response.id)
            checkWebSocketState()
        }
        when (response) {
            is SocketResponse.EmptyResponse -> return
            is SocketResponse.CallbackResponse -> sendMap.remove(response.id).also {
                if (it != null) {
                    it.first.resumeSafe(response.data)
                } else {
                    socketScope.launch {
                        subscribeMap[response.id]?.send(response.data)
                    }
                }
            }
            is SocketResponse.NoticeResponse -> socketScope.launch {
                subscribeMap[response.id]?.send(response.data)
            }
            is SocketResponse.ErrorResponse -> sendMap.remove(response.id).also {
                if (it != null) {
                    if (it.second) it.first.resumeWithExceptionSafe(response.data.exception) else it.first.resumeSafe(null)
                } else {
                    socketScope.launch {
                        subscribeMap[response.id]?.send(response.data)
                    }
                }
            }
        }
    }

    private fun onSocketClose(e: Throwable) {
        if (login) {
            Log.e(TAG, "onSocketClose: ${e.message}")
            e.printStackTrace()
        }
        runCatching {
            changeState(WebSocketState.CLOSED)
            updateNodeLatency(Node.LATENCY_TIMEOUT)

            stopPinging()
            sendMap.values.forEach { (cont, throws) ->
                runCatching { if (throws) cont.resumeWithException(e) else cont.resume(null) }
            }
            sendMap.clear()
            pendingList.forEach { continuation ->
                continuation.resume(false)
            }
            pendingList.clear()
            subscribeMap.clear()
        }
    }

    private fun updateNodeLatency(latency: Long) {
        node.latency = latency
        node.lastUpdate = System.currentTimeMillis()
        blockchainDatabaseScope.launch {
            NodeRepository.updateLatency(node)
        }
    }

    private fun updateNodeApis() {
        blockchainDatabaseScope.launch {
            NodeRepository.updateApis(node)
        }
    }

    private fun updateNodeChainId() {
        blockchainDatabaseScope.launch {
            NodeRepository.updateApis(node)
        }
    }

    private fun checkWebSocketState() {
        if (supportedApi.size > 1) {
            state = WebSocketState.CONNECTED
            if (lastSequence.get() + 1 >= atomicSequence.get()) changeState(WebSocketState.CONNECTED) else changeState(WebSocketState.MESSAGING)
        }
    }

    private fun changeState(newState: WebSocketState) {
        state = newState
        stateLiveInternal.postValue(newState)
    }

}


