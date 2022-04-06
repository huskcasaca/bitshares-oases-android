package graphene.rpc

import graphene.app.*
import graphene.extension.info
import graphene.serializers.GRAPHENE_JSON_PLATFORM_SERIALIZER
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.*
import io.ktor.util.collections.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlin.coroutines.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

data class Node(
    val name: String,
    val url: String,
    val username: String = "",
    val password: String = ""
)

class GrapheneClient(val node: Node, var debug: Boolean = true) : AbstractClient() {


    private fun <T> T.console(title: Any = System.currentTimeMillis()) = apply { if (debug) listOf("GrapheneClient", title, this.toString()).info() }

    private val sequence: AtomicInteger = AtomicInteger(0)
    private val connected: AtomicBoolean = AtomicBoolean(false)

    private val identifiers: MutableMap<APIType, Int> = ConcurrentMap<APIType, Int>().apply {
        put(APIType.LOGIN, 1)
    }

    private val supportedAPI   = mutableSetOf<APIType>()
    private val unsupportedAPI = mutableSetOf<APIType>()

    private val sendingChannel: Channel<BroadcastStruct> = Channel()
    val fallbackChannel: Channel<BroadcastStruct> = Channel(UNLIMITED)

    private val client = HttpClient(CIO.create()) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(GRAPHENE_JSON_PLATFORM_SERIALIZER)
            pingInterval = 10000
        }
        install(ContentNegotiation)
    }

    private val callbackMap: MutableMap<Int, Continuation<SocketResult>> = mutableMapOf()
    private val subscribeMap: MutableMap<Int, Continuation<SocketResult>> = mutableMapOf()

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

    private val waiting: MutableList<Continuation<Unit>> = ConcurrentList()

    private fun buildSocketCall(struct: BroadcastStruct): SocketCall {
        val id = sequence.getAndIncrement()
        val version = SocketCall.JSON_RPC_VERSION
        val method = SocketCall.METHOD_CALL
        val callParams = buildJsonArray {
            add(identifiers.getValue(struct.method.type))
            add(struct.method.nameString)
            add(struct.params)
        }
        return SocketCall(id, version, method, callParams)
    }

    suspend fun waitForOpen() {
        if (connected.get()) return
        return suspendCoroutine { waiting.add(it) }
    }


    // broadcast
    override suspend fun broadcast(struct: BroadcastStruct) {
        if (struct.method.type != APIType.LOGIN) waitForOpen()
        sendingChannel.send(struct)
    }


    private suspend fun login() {
        val result: Boolean = sendForResult(LoginAPI.LOGIN, node.username, node.password) ?: throw SocketErrorException("Login Failed")
        result.console("Login")
    }

    private suspend fun api() {
        API_TYPE_MAP.map { (type, api) ->
            sendScope.launch(Dispatchers.IO) {
                val identifier = sendForResultOrNull<Int>(api)
                if (identifier != null) {
                    identifiers[type] = identifier
                    supportedAPI.add(type)
                    identifier.console(type)
                } else {
                    unsupportedAPI.add(type)
                }
            }
        }.joinAll()

    }
    private suspend fun open() {
        connected.set(true)
        waiting.forEach { it.resume(Unit) }
        console("================ WEBSOCKET OPEN ================")
    }

    private suspend fun DefaultClientWebSocketSession.sendRPC() {
        console("Start Calling >>>")
        while (true) {
            try {
                val struct = sendingChannel.receive()
                val socketCall = buildSocketCall(struct)
                try {
                    callback(socketCall.id, struct.cont)
                    GRAPHENE_JSON_PLATFORM_SERIALIZER.encodeToString(socketCall).console("Call >>>")
                    sendSerialized(socketCall)
                } catch (e: Exception) {
                    callbackMap.remove(socketCall.id)
                    fallbackChannel.send(struct)
                    sendingChannel.consumeEach { fallbackChannel.send(it) }

                    e.printStackTrace()
                    break
                }
            } catch (e: Throwable) {
                e.printStackTrace()
                break
            }
        }
    }

    private suspend fun DefaultClientWebSocketSession.receiveRPC() {
        console("Start Receiving <<<")
        while (true) {
            try {
                val result = receiveDeserialized<SocketResult>().console("Recv <<<")
                callback(result.id, result)
            } catch (e: Exception) {
                e.printStackTrace()
                e.console("================ WEBSOCKET STOP ================")
                break
            }
        }
    }

    private suspend fun launchSocket() {
        client.wss(node.url) {
            try {
                val sendJob = sendScope.launch { sendRPC() }
                val receiveJob = receiveScope.launch { receiveRPC() }
                login()
                api()
                open()
                listOf(sendJob, receiveJob).joinAll()
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    // public
    fun start() {
        sendScope.launch {
            launchSocket() // TODO: 2022/4/1 java.net.SocketException: Bad file descriptor
        }
    }

    fun stop(reason: Exception = SocketManualStopException()) {
        "STOP() CALLED FROM".console()
        try {
            sendingChannel.close(reason)
            fallbackChannel.close(reason)
            session.cancel()
            waiting.forEach {
                it.resumeWithException(reason)
            }
            waiting.clear()
        } catch (e: Throwable) {
//            e.printStackTrace()
        }
    }

}


abstract class AbstractClient {

    val session = SupervisorJob()
    val sendScope = CoroutineScope(Dispatchers.IO.limitedParallelism(10) + session)
    val receiveScope = CoroutineScope(Dispatchers.IO.limitedParallelism(10) + session)

    val channelScope = CoroutineScope(Dispatchers.IO)


    inline fun <reified R> decodeParamsFromJsonElement(result: SocketResult) : R? {
        return when (result) {
            is SocketCallback -> GRAPHENE_JSON_PLATFORM_SERIALIZER.decodeFromJsonElement(result.result)
            is SocketError -> null // throw SocketErrorException(result)
            is SocketNotice -> TODO()
        }
    }

    // send 0 params
    suspend inline fun send(method: API) : SocketResult {
        val array = buildJsonArray {
        }
        return broadcast(method, array)
    }
    suspend inline fun <reified R> sendForResult(method: API) : R? {
        return decodeParamsFromJsonElement(send(method))
    }
    suspend inline fun <reified R> sendForResultOrNull(method: API) : R? {
        return runCatching { sendForResult<R>(method) }.getOrNull()
    }

    // send 1 params
    suspend inline fun <reified T1> send(method: API, param1: T1) : SocketResult {
        val array = buildJsonArray {
            add(GRAPHENE_JSON_PLATFORM_SERIALIZER.encodeToJsonElement(param1))
        }
        return broadcast(method, array)
    }
    suspend inline fun <reified T1, reified R> sendForResult(method: API, param1: T1) : R? {
        return decodeParamsFromJsonElement(send(method, param1))
    }

    // send 2 params
    suspend inline fun <reified T1, reified T2> send(method: API, param1: T1, param2: T2) : SocketResult {
        val array = buildJsonArray {
            add(GRAPHENE_JSON_PLATFORM_SERIALIZER.encodeToJsonElement(param1))
            add(GRAPHENE_JSON_PLATFORM_SERIALIZER.encodeToJsonElement(param2))
        }
        return broadcast(method, array)
    }

    suspend inline fun <reified T1, reified T2, reified R> sendForResult(method: API, param1: T1, param2: T2) : R? {
        return decodeParamsFromJsonElement(send(method, param1, param2))
    }

    suspend fun broadcast(method: API, params: JsonArray) : SocketResult {
        return suspendCoroutine {
            val struct = BroadcastStruct(method, false, params, it)
            channelScope.launch { broadcast(struct) }
        }
    }

    abstract suspend fun broadcast(struct: BroadcastStruct)


    sealed class SocketException : IOException()

    class SocketManualStopException : SocketException()
    class SocketClosedException : SocketException()
    class SocketErrorException(override val message: String) : SocketException() {

        constructor(error: SocketError): this(error.error.toString())
    }

}
