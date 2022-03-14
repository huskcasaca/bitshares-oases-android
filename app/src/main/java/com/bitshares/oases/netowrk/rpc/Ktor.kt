package com.bitshares.oases.netowrk.rpc

import bitshareskit.extensions.logcat
import graphene.app.BlockchainAPI
import graphene.app.CallMethod
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class GrapheneNode(
    val name: String,
    val url: String,
    val username: String = "",
    val password: String = ""
)

class GrapheneClient(
    val node: GrapheneNode
) {

    companion object {

        val ClientJson: Json = Json {
            encodeDefaults = true
            ignoreUnknownKeys = true
        }

        val APIS = mapOf(
            BlockchainAPI.BLOCK to CallMethod.BLOCK,
            BlockchainAPI.NETWORK_BROADCAST to CallMethod.NETWORK_BROADCAST,
            BlockchainAPI.DATABASE to CallMethod.DATABASE,
            BlockchainAPI.HISTORY to CallMethod.HISTORY,
            BlockchainAPI.NETWORK_NODE to CallMethod.NETWORK_NODE,
            BlockchainAPI.CRYPTO to CallMethod.CRYPTO,
            BlockchainAPI.ASSET to CallMethod.ASSET,
            BlockchainAPI.ORDERS to CallMethod.ORDERS,
        )

    }

    fun <T> T.console(title: Any = System.currentTimeMillis()) = apply { logcat("GrapheneClient", title, this.toString()) }

    private val client = HttpClient(OkHttp.create()) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(ClientJson)
            // TODO: 4/2/2022 pingInterval
        }
        install(ContentNegotiation)
    }

    private val session = Job()
    private val socketScope = CoroutineScope(Dispatchers.IO.limitedParallelism(32) + session)
    private val channelScope = CoroutineScope(Dispatchers.IO + session)


    private val sequence: AtomicInteger = AtomicInteger(0)
    private val connected: AtomicBoolean = AtomicBoolean(false)
    private val identifiers: MutableMap<BlockchainAPI, Int> = mutableMapOf<BlockchainAPI, Int>().apply {
        put(BlockchainAPI.LOGIN, 1)
    }

    val supportedAPIs = mutableSetOf<BlockchainAPI>()
    val unsupportedAPIs = mutableSetOf<BlockchainAPI>()

    private val broadcastChannel: Channel<SocketCall> = Channel()
    private val callbackMap: ConcurrentHashMap<Int, Continuation<SocketResult>> = ConcurrentHashMap()
    private val subscribeMap: ConcurrentHashMap<Int, Continuation<SocketResult>> = ConcurrentHashMap()

    private fun callback(id: Int, result: Continuation<SocketResult>) {
        callbackMap.set(id, result)
    }
    private fun callback(id: Int, result: SocketResult) {
        callbackMap.remove(id)?.resume(result)
    }
    private fun callback(id: Int, result: SocketException) {
        callbackMap.remove(id)?.resumeWithException(result)
    }
    private fun callback(id: Int, result: Result<SocketResult>) {
        callbackMap.remove(id)?.resumeWith(result)
    }

    private val waiting: MutableList<Continuation<Unit>> = mutableListOf()

    private fun buildSocketCall(method: CallMethod, subscribe: Boolean, params: JsonArray): SocketCall {
        val id = sequence.getAndIncrement()
        val identifier = identifiers.getValue(method.api)
        val paramsInCall = buildJsonArray {
            add(identifier)
            add(method.nameString)
            add(params)
        }
        return SocketCall(id, WebsocketRpc.JSON_RPC_VERSION, WebsocketRpc.METHOD_CALL, paramsInCall)
    }

    suspend fun waitForOpen() {
        if (connected.get()) return
        return suspendCoroutine { waiting.add(it) }
    }

//    suspend inline fun <reified T1, reified R1> send(method: CallMethod, param1: T1) : R1 {
//        val array = buildJsonArray {
//            add(encodeParam(param1))
//        }
//        wait()
//        return broadcast(method, array)
//    }

    private inline fun <reified R> decodeParamsFromJsonElement(result: SocketResult) : R {
        return when (result) {
            is SocketCallback -> ClientJson.decodeFromJsonElement(result.result)
            is SocketError -> throw SocketErrorException(result)
            is SocketNotice -> TODO()
        }
    }

    // send 0 params
    private suspend inline fun send(method: CallMethod) : SocketResult {
        val array = buildJsonArray {
        }
        return broadcast(method, array)
    }
    private suspend inline fun <reified R> sendForResult(method: CallMethod) : R {
        return decodeParamsFromJsonElement(send(method))
    }
    private suspend inline fun <reified R> sendForResultOrNull(method: CallMethod) : R? {
        return runCatching { sendForResult<R>(method) }.getOrNull()
    }
    // send 1 params
    private suspend inline fun <reified T1> send(method: CallMethod, param1: T1) : SocketResult {
        val array = buildJsonArray {
            add(ClientJson.encodeToJsonElement(param1))
        }
        return broadcast(method, array)
    }
    private suspend inline fun <reified T1, reified R> sendForResult(method: CallMethod, param1: T1) : R {
        return decodeParamsFromJsonElement(send(method, param1))
    }
    // send 2 params
    private suspend inline fun <reified T1, reified T2> send(method: CallMethod, param1: T1, param2: T2) : SocketResult {
        val array = buildJsonArray {
            add(ClientJson.encodeToJsonElement(param1))
            add(ClientJson.encodeToJsonElement(param2))
        }
        return broadcast(method, array)
    }
    private suspend inline fun <reified T1, reified T2, reified R> sendForResult(method: CallMethod, param1: T1, param2: T2) : R {
        return decodeParamsFromJsonElement(send(method, param1, param2))
    }

    // broadcast
    private suspend fun broadcast(method: CallMethod, params: JsonArray) : SocketResult {
        if (method.api != BlockchainAPI.LOGIN) waitForOpen()
        val call = buildSocketCall(method, false, params)
        return suspendCoroutine {
            callback(call.id, it)
            channelScope.launch {
                if (broadcastChannel.isClosedForReceive) {
                    callback(call.id, SocketClosedException())
                } else {
                    broadcastChannel.send(call)
                }

            }
        }
    }

//    suspend fun subscribe(method: CallMethod, params: JsonArray) : Channel<SocketResult> {
//        broadcast(method, params)
//
//    }

    private suspend fun login() {
        val result: Boolean = sendForResult(CallMethod.LOGIN, node.username, node.password)
        result.console("Login")
    }
    private suspend fun api() {
        APIS.map { (api, method) ->
            socketScope.launch(Dispatchers.IO) {
                val identifier = sendForResultOrNull<Int>(method)
                if (identifier != null) {
                    identifiers[api] = identifier
                    supportedAPIs.add(api)
                    identifier.console(api)
                } else {
                    unsupportedAPIs.add(api)
                }
            }
        }.joinAll()
    }
    private suspend fun open() {
        connected.set(true)
        waiting.forEach { it.resume(Unit) }
        console("OPEN ===")
    }
    private suspend fun DefaultClientWebSocketSession.pinging() {


    }

//    suspend inline fun <reified O: K000AbstractObject> getObject(uid: UInt64): O {
//        return Json { ignoreUnknownKeys = true }.decodeFromJsonElement(Any() as JsonElement)
//    }

    private suspend fun DefaultClientWebSocketSession.sendRPC() {
        while (true) {
            try {
                val call = broadcastChannel.receive().apply { ClientJson.encodeToString(this).console("Call >>>") }
                sendSerialized(call)
            } catch (e: Exception) {
                e.printStackTrace()
                stop()
            }
        }
    }

    private suspend fun DefaultClientWebSocketSession.receiveRPC() {
        while (true) {
            try {
                val result = receiveDeserialized<SocketResult>().console("Recv <<<")
                callback(result.id, result)
            } catch (e: Exception) {
                e.printStackTrace()
                if (e is IOException) {
                    e.console("STOP ===")
//                    stop()
                    break
                }
            }
        }
    }

    private suspend fun launchSocket() {
        client.wss(node.url) {
            console("Start Calling >>>")
            val sendJob = socketScope.launch { sendRPC() }
            console("Start Receiving <<<")
            val receiveJob = socketScope.launch { receiveRPC() }
            login()
            api()
            open()
            val pingJob = socketScope.launch { pinging() }
            listOf(sendJob, receiveJob).joinAll()
            pingJob.cancel()
        }
    }

    // public
    fun start() {
        socketScope.launch {
            try {
                launchSocket()
            } catch (e: Exception) {
                // UnknownHostException
                e.printStackTrace()
                stop()
            }
        }
    }

    fun stop() {
        broadcastChannel.close(SocketManualStopException())
        session.cancel()
        waiting.forEach {
            it.resumeWithException(SocketManualStopException())
        }
    }

    sealed class SocketException : IOException()

    class SocketManualStopException : SocketException()
    class SocketClosedException : SocketException()

    class SocketErrorException(error: SocketError) : SocketException()

}